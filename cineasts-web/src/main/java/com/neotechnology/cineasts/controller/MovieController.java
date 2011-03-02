package com.neotechnology.cineasts.controller;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.neotechnology.cineasts.domain.Movie;
import com.neotechnology.cineasts.service.CineastsService;

@RequestMapping(value = "/movie/**")
@Controller
public class MovieController {

    @Autowired
    CineastsService repository;

	@RequestMapping(value = "{id}")
    public ModelAndView getMovieById(@PathVariable String id) throws IOException {
	    try {
	        return new ModelAndView("movie","movie",repository.findMovieById(id));
	    }
	    catch (RuntimeException e) {
	        e.printStackTrace();
	        throw e;
	    }
	}
    
	@RequestMapping("all/{keyword}")
    public ModelAndView searchByKeyword(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response,@PathVariable String keyword) throws IOException { 
    	return new ModelAndView("movies","movies",Arrays.asList(new Movie[]{new Movie(),new Movie(),new Movie()}));
	}

}
