package org.example.musiccharts;

import java.util.Objects;

public class Song {
    private String title;
    private String artist;
    private String genre;

    public Song (String title, String artist, String genre) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
    }

    public Song(){}

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getGenre() {
        return genre;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public int hashCode () {
        return Objects.hashCode(title);
    }

    @Override
    public String toString () {
        return "{" +
                "\"Title\" : \"" + title + "\", " +
                "\"Artist\" : \"" + artist + "\" " +
                "\"Genre\" : \"" + genre + "\" " +
                "}";
    }
}
