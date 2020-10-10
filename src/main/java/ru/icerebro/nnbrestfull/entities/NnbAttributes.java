package ru.icerebro.nnbrestfull.entities;

public class NnbAttributes {
    private boolean fromMainSearch;
    private String searchString;

    private boolean basicSearch;
    private String searchAuthor;
    private String searchName;
    private String searchYear;

    public NnbAttributes() {
        fromMainSearch = false;
        basicSearch = false;
    }

    public boolean isFromMainSearch() {
        return fromMainSearch;
    }

    public void setFromMainSearch(boolean fromMainSearch) {
        this.fromMainSearch = fromMainSearch;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public boolean isBasicSearch() {
        return basicSearch;
    }

    public void setBasicSearch(boolean basicSearch) {
        this.basicSearch = basicSearch;
    }

    public String getSearchAuthor() {
        return searchAuthor;
    }

    public void setSearchAuthor(String searchAuthor) {
        this.searchAuthor = searchAuthor;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public String getSearchYear() {
        return searchYear;
    }

    public void setSearchYear(String searchYear) {
        this.searchYear = searchYear;
    }
}
