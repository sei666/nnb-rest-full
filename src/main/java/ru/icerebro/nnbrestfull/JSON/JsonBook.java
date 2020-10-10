package ru.icerebro.nnbrestfull.JSON;

import ru.icerebro.nnbrestfull.database.JDBC.entities.Book;

public class JsonBook {
    private String item;
    private String author;
    private String title;
    private String year;

    JsonBook(Book book) {
        this.item = book.getItemStr();
        if (book.getTitle().equals(" <null>"))
            this.title = "";
        else
            this.title = book.getTitle();

        if (book.getAuthor().equals(" <null>"))
            this.author = "";
        else
            this.author = book.getAuthor();

        if (book.getYear().equals(" <null>"))
            this.year = "";
        else
            this.year = book.getYear();
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
