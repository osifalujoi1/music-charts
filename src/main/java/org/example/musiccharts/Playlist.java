package org.example.musiccharts;

import java.util.LinkedList;

public class Playlist {
    private String playlistName;
    private LinkedList<Song> songs;

    public Playlist(String playlistName, LinkedList<Song> songs) {
        this.playlistName = playlistName;
        this.songs = songs;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public LinkedList<Song> getSongs() {
        return songs;
    }


    public Song findSong (String title) {
        for (Song song: songs){
            if (song.getTitle().equals(title)){
                return song;
            }
        }
        return null;
    }

    public boolean addSongToPlaylist(Song song) {
        if (findSong(song.getTitle()) == null){
            songs.add(song);
            System.out.println(song.getTitle() + " successfully added to the playlist");
            return true;
        }
        System.out.println(song.getTitle() + " is already in the playlist");
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Playlist: " + playlistName + "\nSongs:\n");
        for (Song song : songs) {
            sb.append(song).append("\n");  // Assuming Song has a good toString()
        }
        return sb.toString();
    }
}
