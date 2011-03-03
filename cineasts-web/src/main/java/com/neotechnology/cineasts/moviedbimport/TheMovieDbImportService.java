package com.neotechnology.cineasts.moviedbimport;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.neotechnology.cineasts.domain.Movie;
import com.neotechnology.cineasts.service.CineastsService;

@Component
public class TheMovieDbImportService {

    @Autowired
    CineastsService cineastsService;
    
    MovieDbApiClient client;
    MovieDbJsonMapper movieDbJsonMapper;
    
    public void importMovie(String movieId) {
        JSONObject movieJson = client.getMovie(movieId);
        Movie movie = movieDbJsonMapper.mapToMovie(movieJson);
        cineastsService.save(movie);
    }
}
