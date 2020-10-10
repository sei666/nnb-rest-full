package ru.icerebro.nnbrestfull.JSON;

import ru.icerebro.nnbrestfull.database.JDBC.entities.Book;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonBooksInfo {
    private Map<Integer, JsonBook> booksInfo;
    private Integer pageNumber;
    private Integer pagesCount;
    private Integer countBooks;
    private String searchKey;

    public JsonBooksInfo(List<Book> bookList) {
        booksInfo = new HashMap<>();
        for (Book b:bookList) {
            booksInfo.put(b.getDocId(), new JsonBook(b));
        }
    }


    public Map<Integer, JsonBook> getBooksInfo() {
        return booksInfo;
    }

    public void setBooksInfo(Map<Integer, JsonBook> booksInfo) {
        this.booksInfo = booksInfo;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPagesCount() {
        return pagesCount;
    }

    public void setPagesCount(Integer pagesCount) {
        this.pagesCount = pagesCount;
    }

    public Integer getCountBooks() {
        return countBooks;
    }

    public void setCountBooks(Integer countBooks) {
        this.countBooks = countBooks;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }
}
