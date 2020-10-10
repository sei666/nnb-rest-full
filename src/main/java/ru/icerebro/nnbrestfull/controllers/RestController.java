package ru.icerebro.nnbrestfull.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.icerebro.nnbrestfull.JSON.JsonBooksInfo;
import ru.icerebro.nnbrestfull.database.JDBC.entities.Book;
import ru.icerebro.nnbrestfull.database.JDBC.interfaces.BookJdbc;
import ru.icerebro.nnbrestfull.entities.ConstStrings;
import ru.icerebro.nnbrestfull.entities.NnbAttributes;
import ru.icerebro.nnbrestfull.services.interfaces.MainService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@CrossOrigin
public class RestController {

    private final MainService mainService;
    private final BookJdbc bookJdbc;

    @Autowired
    public RestController(MainService mainService, BookJdbc bookJdbc) {
        this.mainService = mainService;
        this.bookJdbc = bookJdbc;
    }

    @PostMapping(value = "/search")
    @ResponseBody
    public JsonBooksInfo search(@RequestParam(value = "searchAuthor", required = false) String searchAuthor,
                                @RequestParam(value = "searchName", required = false) String searchName,
                                @RequestParam(value = "searchYear", required = false) String searchYear,
                                @RequestParam(value = "pageNumber") Integer pageNumber,
                                @RequestParam(value = "fromMainSearch") Boolean fromMainSearch,
                                HttpServletRequest request){

        List<Book> bookList;

        if (fromMainSearch) {
            bookList = bookJdbc.searchBooks(searchName);
        }else {
            bookList = bookJdbc.searchBooks(searchAuthor, searchName, searchYear);
        }

        int begin = 15*(pageNumber-1), end = 15*pageNumber;
        if (end > bookList.size()){
            end = bookList.size();
        }

        JsonBooksInfo jsonBooksInfo = new JsonBooksInfo(bookList.subList(begin, end));

        if (bookList.size() > 0) {

            jsonBooksInfo.setPagesCount((int) Math.ceil(bookList.size() / 15.0));
            jsonBooksInfo.setCountBooks(bookList.size());

        }
        else{
            jsonBooksInfo.setPagesCount(0);
            jsonBooksInfo.setCountBooks(0);
        }
        jsonBooksInfo.setPageNumber(pageNumber);

        if (fromMainSearch) {
            jsonBooksInfo.setSearchKey(searchName);
        }else {
            jsonBooksInfo.setSearchKey(searchAuthor+"  "+searchName+"  "+searchYear);
        }

        HttpSession session = request.getSession();
        NnbAttributes nnbAttributes = (NnbAttributes) session.getAttribute(ConstStrings.ATTRIBUTES);
        if (nnbAttributes == null)
            nnbAttributes = new NnbAttributes();

        if (fromMainSearch){
            nnbAttributes.setBasicSearch(false);
            nnbAttributes.setFromMainSearch(true);
            nnbAttributes.setSearchString(searchName);
        }else {
            nnbAttributes.setBasicSearch(true);
            nnbAttributes.setFromMainSearch(false);
            nnbAttributes.setSearchAuthor(searchAuthor);
            nnbAttributes.setSearchName(searchName);
            nnbAttributes.setSearchYear(searchYear);
        }
        session.setAttribute(ConstStrings.ATTRIBUTES, nnbAttributes);

        return  jsonBooksInfo;
    }
}
