package ru.icerebro.nnbrestfull.database.JDBC.entities;

import java.util.List;

public class CachedBooks {
    private long writtenMills;
    private List<Book> books;

    public CachedBooks(List<Book> books) {
        this.books = books;
        this.writtenMills = System.currentTimeMillis();
    }

    public List<Book> getBooks() {
        return books;
    }

    public long elapsed(){
        return System.currentTimeMillis() - this.writtenMills;
    }
}
