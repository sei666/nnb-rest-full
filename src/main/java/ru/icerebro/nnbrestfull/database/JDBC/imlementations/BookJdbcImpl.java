package ru.icerebro.nnbrestfull.database.JDBC.imlementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.icerebro.nnbrestfull.database.JDBC.entities.Book;
import ru.icerebro.nnbrestfull.database.JDBC.entities.CachedBooks;
import ru.icerebro.nnbrestfull.database.JDBC.entities.Item;
import ru.icerebro.nnbrestfull.database.JDBC.interfaces.BookJdbc;
import ru.icerebro.nnbrestfull.database.JDBC.interfaces.MyConPool;
import ru.icerebro.nnbrestfull.services.TwoQCache;

import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class BookJdbcImpl implements BookJdbc {

    private final MyConPool myConPool;
    private TwoQCache<String, CachedBooks> cache;

    private String baseQuery = "SELECT" +
            "  author.TERM 'author'," +
            "  title.TERM 'title'," +
            "  yearTable.TERM 'yearPublished', doci.ITEM 'item'," +
            "  doci.DOC_ID 'docid'" +

            "FROM books_rusmarc.dbo.IDX700a author" +
            "  JOIN IDX700aX authorDoc ON author.IDX_ID = authorDoc.IDX_ID" +

            "  JOIN IDX200aX titleDoc ON authorDoc.DOC_ID = titleDoc.DOC_ID" +
            "  JOIN books_rusmarc.dbo.IDX200a title ON title.IDX_ID = titleDoc.IDX_ID" +

            "  JOIN IDX210dX yearDoc ON authorDoc.DOC_ID = yearDoc.DOC_ID" +
            "  JOIN books_rusmarc.dbo.IDX210d yearTable ON yearTable.IDX_ID = yearDoc.IDX_ID" +

            "  JOIN DOC doci ON doci.DOC_ID = authorDoc.DOC_ID";

    @Autowired
    public BookJdbcImpl(MyConPool myConPool){
        this.myConPool = myConPool;

        cache = new TwoQCache<>(100);
    }



    @Override
    public String getBookPath(String searchName, Integer bid, HttpServletResponse response) {
        List<Book> books = this.checkCached(searchName);

        Book searchedBook = null;

        if (books == null) {
            searchedBook = this.getBook(bid);
        }else {
            for (Book book:books) {
                if (book.getDocId() == bid)
                    searchedBook = book;
            }
        }

        if (searchedBook == null)
            return null;
        else{
            Item item = new Item(searchedBook.getItemStr());
            return item.getVal("8564", 'd');
        }
    }

    @Override
    public Book getBook(Integer bid) {
        Connection connection = myConPool.getConnection();
        Book book = null;
        PreparedStatement statement = null;
        try {
            String query = baseQuery + "  WHERE doci.DOC_ID = ?";

            statement = connection.prepareStatement(query);
            statement.setInt(1, bid);
            final ResultSet resultSet = statement.executeQuery();

            book = this.getBooksFromRS(resultSet).get(0);

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (statement != null)
                this.closeStatement(statement);

            myConPool.freeConnection(connection);
        }

        return book;
    }

    @Override
    public List<Book> searchBooks(String searchString) {
        if ((searchString == null || searchString.length() == 0))
            return new ArrayList<>();

        List<Book> books = this.checkCached(searchString);

        if (books == null)
            books = new ArrayList<>();
        else
            return books;

        String sqlVar = "%" + searchString + "%";
        Connection connection = myConPool.getConnection();

        PreparedStatement statement = null;
        try {
            String query = baseQuery + "  WHERE title.TERM LIKE ? OR author.TERM LIKE ?";

            statement = connection.prepareStatement(query);
            statement.setString(1, sqlVar);
            statement.setString(2, sqlVar);
            final ResultSet resultSet = statement.executeQuery();

            books.addAll(this.getBooksFromRS(resultSet));

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {

            if (statement != null)
                this.closeStatement(statement);

            myConPool.freeConnection(connection);

        }
        if (!books.isEmpty())
            cache.put(searchString, new CachedBooks(books));

        return books;
    }

    @Override
    public List<Book> searchBooks(String searchAuthor, String searchName, String searchYear) {
        if ((searchName == null || searchName.length() == 0)
                && (searchAuthor == null || searchAuthor.length() == 0)
                && (searchYear == null || searchYear.length() == 0))
            return new ArrayList<>();

        List<Book> books = this.checkCached(searchAuthor+"  "+searchName+"  "+searchYear);

        if (books == null)
            books = new ArrayList<>();
        else
            return books;

        String sqlName;
        String sqlAuthor;
        String sqlYear;

        StringBuilder strQuery = new StringBuilder("  WHERE ");
        int paramCount = 0, paramName = 0, paramAuthor = 0, paramYear = 0;

        if (searchName != null && searchName.length() > 0){
            strQuery.append("title.TERM LIKE ? ");
            paramName = ++paramCount;
        }

        if (searchAuthor != null && searchAuthor.length() > 0){
            if (paramCount > 0){
                strQuery.append("AND ");
            }
            strQuery.append("author.TERM LIKE ? ");
            paramAuthor = ++paramCount;
        }

        if (searchYear != null && searchYear.length() > 0){
            if (paramCount > 0){
                strQuery.append("AND ");
            }
            strQuery.append("yearTable.TERM LIKE ?");
            paramYear = ++paramCount;
        }

        Connection connection = myConPool.getConnection();

        PreparedStatement statement = null;

        try {
            String query;

                query = baseQuery
                        + strQuery.toString();
//                    + "  WHERE title.TERM LIKE ? AND author.TERM LIKE ? AND yearTable.TERM LIKE ?";

            statement = connection.prepareStatement(query);


            if (paramName > 0){
                sqlName = "%" + searchName + "%";
                statement.setString(paramName, sqlName);
            }

            if (paramAuthor > 0){
                sqlAuthor = "%" + searchAuthor + "%";
                statement.setString(paramAuthor, sqlAuthor);
            }

            if (paramYear > 0){
                sqlYear = "%" + searchYear + "%";
                statement.setString(paramYear, sqlYear);
            }

            final ResultSet resultSet = statement.executeQuery();

            books.addAll(this.getBooksFromRS(resultSet));

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {

            if (statement != null)
                this.closeStatement(statement);

            myConPool.freeConnection(connection);

        }
        if (!books.isEmpty())
            cache.put(searchAuthor+"  "+searchName+"  "+searchYear, new CachedBooks(books));

        return books;
    }


    /**
     * @param key Key to cache map
     * @return set of books if found and still valid, null if not
     */
    private List<Book> checkCached(String key){
        CachedBooks cachedBooks = cache.get(key);
        List<Book> books = null;
        if (cachedBooks != null && cachedBooks.elapsed() < 60000) { //3600000 - hour
            books = cachedBooks.getBooks();
        }

        return books;
    }

    private List<Book> getBooksFromRS(ResultSet resultSet) throws SQLException {

        List<Book> books = new ArrayList<>();

        while (resultSet.next()){
            Book book = new Book();

            int docid = resultSet.getInt("docid");
            book.setDocId(docid);

            book.setAuthor(resultSet.getString("author"));
            book.setTitle(resultSet.getString("title"));
            book.setYear(resultSet.getString("yearPublished"));

            book.setItemStr(resultSet.getString("item"));

            boolean exists = false;
            for (Book b:books) {
                if (b.getDocId() == book.getDocId()){
                    exists = true;
                }
            }

            if (!exists){
                books.add(book);
            }
        }


        return books;
    }


    private void closeStatement (Statement statement){
        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
