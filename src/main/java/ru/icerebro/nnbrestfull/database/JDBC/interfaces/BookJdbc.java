package ru.icerebro.nnbrestfull.database.JDBC.interfaces;

import ru.icerebro.nnbrestfull.database.JDBC.entities.Book;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface BookJdbc {
    List<Book> searchBooks(String searchString);
    List<Book> searchBooks(String searchAuthor, String searchName, String searchYear);

    String getBookPath(String searchName, Integer bid, HttpServletResponse response);

    Book getBook(Integer bid);
}
