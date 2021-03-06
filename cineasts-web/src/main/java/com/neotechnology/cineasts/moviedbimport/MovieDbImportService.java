package com.neotechnology.cineasts.moviedbimport;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.neotechnology.cineasts.domain.Actor;
import com.neotechnology.cineasts.domain.Movie;
import com.neotechnology.cineasts.domain.Role;
import com.neotechnology.cineasts.service.CineastsService;
import static com.neotechnology.cineasts.util.JXPathUtils.*;

@Component
public class MovieDbImportService {
    
    private static final Logger logger = LoggerFactory.getLogger(MovieDbImportService.class);

    @Autowired
    CineastsService cineastsService;    
    @Autowired
    MovieDbApiClient client;
    @Autowired
    MovieDbJsonMapper movieDbJsonMapper;
    @Autowired
    MovieDbLocalStorage localStorage;
    
    @Transactional
    public Movie importMovie(String movieId) {        
        logger.debug("Importing movie "+movieId);
        
        Movie movie = cineastsService.findMovieById(movieId);
        if(movie == null) { // Not found: Create fresh 
            movie = new Movie(movieId);
        }
        
        JSONArray movieJson;        
        if (localStorage.hasMovie(movieId)) {
            movieJson = localStorage.loadMovie(movieId);
        }
        else {
            movieJson = client.getMovie(movieId);
            localStorage.storeMovie(movieId, movieJson);
        }
        
        movieDbJsonMapper.mapToMovie(movieJson, movie);
        cineastsService.save(movie);
        relatePersonsToMovie(movie, movieJson);
        return movie;
    }

    private void relatePersonsToMovie(Movie movie, JSONArray movieJson) {        
        movie.removeParticipations(); 
        for (JSONObject personJson: jxJSONObjectCollection(movieJson, ".[1]/cast")) {
            String personId = jxString(personJson, "id");
            String personRole = jxString(personJson, "job");

            Actor person = cineastsService.findActorById(personId);
            if (person == null) {
                person = importPerson(personId);
            }
            if (person == null) {
                throw new RuntimeException("Person with id "+personId+" not found");
            }
            
            Role role = movieDbJsonMapper.mapToRole(personRole);
            if (role != null) {
                person.participateIn(movie, role);
            }
            else {
                logger.info("Role '{}' from movie DB not supported for person '{}'", personRole, person.getName() );
            }
        }
    }

    @Transactional
    public Actor importPerson(String personId) {        
        logger.debug("Importing person " + personId);
        Actor person = cineastsService.findActorById(personId);
        if (person == null) { // Not found, create fresh
            person = new Actor(personId);
        }
        
        JSONArray personJson;
        if (localStorage.hasPerson(personId)) {
            personJson = localStorage.loadPerson(personId);
        }
        else {
            personJson = client.getPerson(personId);
            localStorage.storePerson(personId, personJson);
        }
        
        movieDbJsonMapper.mapToPerson(personJson, person);
        cineastsService.save(person);
        return person;
    }
}
