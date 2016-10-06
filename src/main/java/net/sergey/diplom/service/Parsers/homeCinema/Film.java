package net.sergey.diplom.service.Parsers.homeCinema;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sergey on 10.02.16.
 */
public final class Film {
    //<div class="top_box_parent">
    private String movieTitle;//название фильма
    private String time;//время сеансов
    private String hall;//кинозал
    //<li class="pic_film">картинка
    private Image image;
    //<li class="description_film">описание
    private Map<String, String> filmInfo;

    public Map<String, String> getFilmInfo() {
        return new HashMap<String, String>(filmInfo);
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHall() {
        return hall;
    }

    public void setHall(String hall) {
        this.hall = hall;
    }

    public void addFilmInfo(String key, String Value) {
        filmInfo.put(key, Value);
    }

    @Override
    public String toString() {
        return "Film{" +
                "movieTitle='" + movieTitle + '\'' +
                ", time='" + time + '\'' +
                ", hall='" + hall + '\'' +
                /*", image=" + image +*/
                ", filmInfo=" + filmInfo +
                '}';
    }

    public Film() {
        this.filmInfo = new HashMap<String, String>();
    }
}
