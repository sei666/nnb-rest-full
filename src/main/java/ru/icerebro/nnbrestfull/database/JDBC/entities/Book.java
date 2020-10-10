package ru.icerebro.nnbrestfull.database.JDBC.entities;


public class Book {
    private int docId;
    private String author;
    private String title;
    private String year;
    private String itemStr;

    public Book() {
    }


    public String getItemStr() {
        return itemStr;
    }

    public void setItemStr(String itemStr) {
        this.itemStr = itemStr;
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

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass().equals(this.getClass())){
            Book book = (Book) obj;
            return this.docId == book.docId;
        }else
            return false;
    }
}
