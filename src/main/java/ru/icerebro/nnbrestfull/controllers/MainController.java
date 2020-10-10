package ru.icerebro.nnbrestfull.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.icerebro.nnbrestfull.entities.ConstStrings;
import ru.icerebro.nnbrestfull.entities.NnbAttributes;
import ru.icerebro.nnbrestfull.services.interfaces.MainService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@Controller
@CrossOrigin
public class MainController {

    private final MainService mainService;

    @Autowired
    public MainController(MainService mainService) {
        this.mainService = mainService;
    }

    @GetMapping(value = "/search")
    public ModelAndView search(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("search_books_page");

        HttpSession session = request.getSession();
        NnbAttributes nnbAttributes = (NnbAttributes) session.getAttribute(ConstStrings.ATTRIBUTES);
        if (nnbAttributes == null)
            nnbAttributes = new NnbAttributes();


        if (nnbAttributes.isFromMainSearch()){
            modelAndView.addObject("fromMainPage", true);
            modelAndView.addObject("basicSearch", false);

            nnbAttributes.setBasicSearch(false);

            modelAndView.addObject("searchString", nnbAttributes.getSearchString());
        }else if (nnbAttributes.isBasicSearch()){
            modelAndView.addObject("fromMainPage", false);
            modelAndView.addObject("basicSearch", true);

            nnbAttributes.setFromMainSearch(false);

            modelAndView.addObject("searchName", nnbAttributes.getSearchName());
            modelAndView.addObject("searchAuthor", nnbAttributes.getSearchAuthor());
            modelAndView.addObject("searchYear", nnbAttributes.getSearchYear());
        }else {
            modelAndView.addObject("fromMainPage", false);
            modelAndView.addObject("basicSearch", false);

            nnbAttributes.setFromMainSearch(false);
            nnbAttributes.setBasicSearch(false);
        }

        session.setAttribute(ConstStrings.ATTRIBUTES, nnbAttributes);

        return  modelAndView;
    }

    @PostMapping(value = "/mainsearch")
    public String mainsearch(@RequestParam("searchString") String searchString,
                             HttpServletRequest request){

        HttpSession session = request.getSession();
        NnbAttributes nnbAttributes = (NnbAttributes) session.getAttribute(ConstStrings.ATTRIBUTES);
        if (nnbAttributes == null)
            nnbAttributes = new NnbAttributes();

        nnbAttributes.setFromMainSearch(true);
        nnbAttributes.setSearchString(searchString);

        session.setAttribute(ConstStrings.ATTRIBUTES, nnbAttributes);

        return  "redirect:/search";
    }


    @GetMapping (value = "/downloadBook")
    public void downloadBook(@RequestParam("searchName") String searchName,
                             @RequestParam("bid") Integer bid,
                             HttpServletResponse response){
        mainService.downloadBook(searchName, bid,response);
    }
}
