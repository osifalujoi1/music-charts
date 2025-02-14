package org.example.musiccharts;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class MusicChartsController {
    @FXML
    private Label welcomeText;

    @FXML
    private Button homeButton;

    @FXML
    public Label listViewText;

    @FXML
    private VBox mainContent;

    @FXML
    private ListView<String> playlistView;

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private Button downloadButton;

    @FXML
    private NumberAxis yAxis;
    private LinkedList<Song> iconicTopBillboard = new LinkedList();
    private LinkedList<Song> iconicTopLastfm = new LinkedList();
    private LinkedList<Song> iconicBBCPlaylist = new LinkedList();
    private LinkedList<Song> iconicPlaylist = new LinkedList();
    private LinkedList<Song> suggestedPlaylist = new LinkedList();

    @FXML
    protected void onHelloButtonClick() {

        if (homeButton.getText().equals("Get Started!")){
            mainContent.setVisible(true);
            homeButton.setText("Go Back");
            welcomeText.setText("Music Chart Application");
        }else {
            mainContent.setVisible(false);
            homeButton.setText("Get Started!");
            welcomeText.setText("Welcome to Music Chart Application");
        }
    }

    public void displayBillboardSongs() {
        this.listViewText.setVisible(false);
        this.fetchBillboardSongs();
        this.updateListView(this.iconicTopBillboard);
        this.showSuccess("Billboard songs loaded successfully!");
        this.updateBarChart();
    }

    public void displayLastfmSongs() {
        this.listViewText.setVisible(false);
        this.fetchLastfmSongs();
        this.updateListView(this.iconicTopLastfm);
        this.showSuccess("Last fm songs loaded successfully!");
        this.updateBarChart();
    }

    public void displayBBCSongs() {
        this.listViewText.setVisible(false);
        this.fetchBBCSongs();
        this.updateListView(this.iconicBBCPlaylist);
        this.showSuccess("BBC songs loaded successfully!");
        this.updateBarChart();
    }

    public void fetchBillboardSongs() {
        this.iconicTopBillboard.clear();
        this.iconicTopBillboard.addAll(this.scrapeSongs("https://www.billboard.com/charts/hot-100/", "h3#title-of-a-story.c-title", "span.c-label.a-no-trucate", "Pop/Various"));
    }

    public void fetchLastfmSongs() {
        this.iconicTopLastfm.clear();
        this.iconicTopLastfm.addAll(this.scrapeSongs("https://www.last.fm/charts", "td a.link-block-target", "td.globalchart-track-artist-name a", "Various"));
    }

    public void fetchBBCSongs() {
        this.iconicBBCPlaylist.clear();
        this.iconicBBCPlaylist.addAll(this.scrapeSongs("https://www.officialcharts.com/charts/singles-chart/", "a.chart-name span", "a.chart-artist span", "Various"));
    }

    public void generateSuggestedPlaylist() {
        if (this.iconicTopBillboard.isEmpty()) {
            this.fetchBillboardSongs();
        }
        if (this.iconicTopLastfm.isEmpty()) {
            this.fetchLastfmSongs();
        }
        if (this.iconicBBCPlaylist.isEmpty()) {
            this.fetchBBCSongs();
        }
        this.iconicPlaylist.clear();
        this.iconicPlaylist.addAll(this.iconicTopBillboard);
        this.iconicPlaylist.addAll(this.iconicTopLastfm);
        this.iconicPlaylist.addAll(this.iconicBBCPlaylist);
        HashMap<String, Integer> songCount = new HashMap<String, Integer>();
        LinkedList<Song> temp = new LinkedList<Song>();
        HashSet<String> addedSongs = new HashSet<String>();
        for (Song song : this.iconicPlaylist) {
            String title = song.getTitle().toLowerCase();
            songCount.put(title, songCount.getOrDefault(title, 0) + 1);
            if ((Integer)songCount.get(title) < 2 || !addedSongs.add(title)) continue;
            temp.add(song);
        }
        this.suggestedPlaylist.clear();
        if (temp.size() > 10) {
            Collections.shuffle(temp);
            for (int i = 0; i < 10; ++i) {
                this.suggestedPlaylist.add((Song)temp.get(i));
            }
        } else {
            this.suggestedPlaylist.addAll(temp);
        }
        this.updateListView(this.suggestedPlaylist);
        this.updateBarChart();
        this.barChart.setVisible(true);
        this.showSuccess("Suggested playlist generated!");
    }

    private LinkedList<Song> scrapeSongs(String url, String titleSelector, String artistSelector, String defaultGenre) {
        LinkedList<Song> playlist = new LinkedList<Song>();
        try {
            Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Safari/605.1.15").header("Accept-Language", "*").get();
            Elements songTitles = doc.select(titleSelector);
            Elements artists = doc.select(artistSelector);
            for (int i = 0; i < songTitles.size(); ++i) {
                String title = ((Element)songTitles.get(i)).text().trim();
                String artist = i < artists.size() ? ((Element)artists.get(i)).text().trim() : "Unknown Artist";
                playlist.add(new Song(title, artist, defaultGenre));
            }
        } catch (IOException e) {
            this.showError("Error fetching songs from " + url);
        }
        return playlist;
    }

    private void updateListView(LinkedList<Song> playlist) {
        this.playlistView.getItems().clear();
        for (Song song : playlist) {
            this.playlistView.getItems().add(song.getTitle() + " - " + song.getArtist() + " (" + song.getGenre() + ")");
        }
    }

    private void updateBarChart() {
        this.barChart.getData().clear();
        LinkedList<Song> allSongs = new LinkedList<Song>();
        allSongs.addAll(this.iconicTopBillboard);
        allSongs.addAll(this.iconicTopLastfm);
        allSongs.addAll(this.iconicBBCPlaylist);
        HashMap<String, Integer> artistSongCount = new HashMap<String, Integer>();
        for (Song song : allSongs) {
            String[] artists;
            for (String artist : artists = song.getArtist().split(",")) {
                if (artist.equals("Unknown Artist")) continue;
                artist = artist.toLowerCase().trim();
                artistSongCount.put(artist, artistSongCount.getOrDefault(artist, 0) + 1);
            }
        }
        List<Map.Entry<String, Integer>> topArtists = artistSongCount.entrySet().stream().sorted((a, b) -> ((Integer)b.getValue()).compareTo((Integer)a.getValue())).limit(10L).collect(Collectors.toList());
        Map<String, Integer> artistListeners = this.fetchArtistListeners(topArtists);
        this.configureYAxis();
        XYChart.Series series = new XYChart.Series();
        String[] colors = new String[]{"#ffdbc6", "#ffd2b7", "#ffc9a8", "#ffb998", "#ffaa89", "#ff9a79", "#ff8a6a", "#ff7a5b"};
        int index = 0;
        for (Map.Entry<String, Integer> entry : topArtists) {
            String artist = entry.getKey();
            int songCount = entry.getValue();
            int listeners = artistListeners.getOrDefault(artist, 0);
            int finalIndex = index++;
            if (listeners == 0) continue;
            XYChart.Data<String, Integer> data = new XYChart.Data<String, Integer>(artist, listeners);
            data.nodeProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    newValue.setStyle("-fx-bar-fill:" + colors[finalIndex % colors.length] + ";");
                }
            });
            series.getData().add(data);
            this.playlistView.getItems().add(artist + " - " + songCount + " songs - " + listeners + " listeners");
        }
        this.barChart.getData().add(series);
    }

    public Map<String, Integer> fetchArtistListeners(List<Map.Entry<String, Integer>> topArtists) {
        HashMap<String, Integer> listenerData = new HashMap<String, Integer>();
        try {
            Document doc = Jsoup.connect("https://www.last.fm/charts/weekly").userAgent("Mozilla/5.0").get();
            Elements rows = doc.select("table.weeklychart tr");
            for (Element row : rows) {
                String artistName = row.select("td.weeklychart-name a").text().trim();
                String listenersText = row.select("td.weeklychart-listeners").text().replace(",", "").trim();
                int listeners = listenersText.isEmpty() ? 0 : Integer.parseInt(listenersText);
                listenerData.put(artistName.toLowerCase(), listeners);
            }
        } catch (IOException e) {
            this.showError("Error fetching listener data from Last.fm.");
        }
        return listenerData;
    }

    private void configureYAxis() {
        this.yAxis.setTickUnit(50000.0);
        this.yAxis.setAutoRanging(false);
        this.yAxis.setLowerBound(0.0);
        this.yAxis.setUpperBound(1000000.0);
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void downloadPlaylist() {
        if (suggestedPlaylist.isEmpty()){
            showError("No Playlist available to download");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Playlist");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null){
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))){
                for (Song song: suggestedPlaylist){
                    writer.println(song.getTitle() + " - " + song.getArtist());
                }
                showSuccess("Playlist downloaded successfully");
            }catch (IOException e) {
                showError("Error saving playlist file");
            }
        }
    }
}