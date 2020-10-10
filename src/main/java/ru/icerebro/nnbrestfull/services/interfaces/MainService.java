package ru.icerebro.nnbrestfull.services.interfaces;

import javax.servlet.http.HttpServletResponse;

public interface MainService {

    void downloadBook(String searchName, Integer bid, HttpServletResponse response);
}
