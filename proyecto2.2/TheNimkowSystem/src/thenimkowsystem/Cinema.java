
package thenimkowsystem;

/**
 *
 * @author Diego
 */
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Cinema extends JFrame {
    private JPanel contentPane;
    private int currentPage = 1;
    private static final String API_KEY = "f846867b6184611eeff179631d3f9e26";

    public Cinema() {
        setTitle("Movies from TMDb");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.setBackground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(contentPane);

        JTextField searchField = new JTextField();
        searchField.setBackground(Color.DARK_GRAY);
        searchField.setForeground(Color.WHITE);
        JButton searchButton = new JButton("Buscar");
        searchButton.setBackground(Color.BLUE);
        searchButton.setForeground(Color.WHITE);

        searchButton.addActionListener(e -> {
            currentPage = 1;
            contentPane.removeAll();
            fetchMovies(searchField.getText().trim());
        });

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BorderLayout());
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        searchPanel.setBackground(Color.BLACK);

        setLayout(new BorderLayout());
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        JButton prevButton = new JButton("<< Página anterior");
        JButton nextButton = new JButton("Siguiente página >>");

        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        paginationPanel.add(prevButton);
        paginationPanel.add(nextButton);
        add(paginationPanel, BorderLayout.SOUTH);

        prevButton.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                contentPane.removeAll();
                fetchMovies(searchField.getText().trim());
            }
        });

        nextButton.addActionListener(e -> {
            currentPage++;
            contentPane.removeAll();
            fetchMovies(searchField.getText().trim());
        });

        fetchMovies("");
        setVisible(true);
    }

    private void fetchMovies(String searchTerm) {
        new Thread(() -> {
            try {
                String urlStr = "https://api.themoviedb.org/3/trending/movie/week?api_key=" + API_KEY + "&language=es-ES&page=" + currentPage;
                if (!searchTerm.isEmpty()) {
                    urlStr = "https://api.themoviedb.org/3/search/movie?api_key=" + API_KEY + "&language=es-ES&page=" + currentPage + "&query=" + searchTerm;
                }

                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                StringBuilder response;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }

                JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
                JsonArray results = jsonObject.getAsJsonArray("results");

                for (int i = 0; i < results.size(); i++) {
                    JsonObject movie = results.get(i).getAsJsonObject();
                    displayMovieInfo(movie);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void displayMovieInfo(JsonObject movie) {
        String title = movie.get("title").getAsString();
        double voteAverage = movie.get("vote_average").getAsDouble();
        String posterPath = movie.get("poster_path").getAsString();

        JPanel moviePanel = new JPanel();
        moviePanel.setLayout(new BoxLayout(moviePanel, BoxLayout.Y_AXIS));
        moviePanel.setBackground(Color.DARK_GRAY);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Gotham", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        moviePanel.add(titleLabel);

        JLabel voteLabel = new JLabel("Votación: " + voteAverage);
        voteLabel.setForeground(Color.WHITE);
        moviePanel.add(voteLabel);

        try {
            URL posterURL = new URL("https://image.tmdb.org/t/p/w200" + posterPath);
            ImageIcon posterIcon = new ImageIcon(posterURL);
            JLabel posterLabel = new JLabel(posterIcon);

            // Agregar MouseListener al panel de la película
            moviePanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int movieId = movie.get("id").getAsInt();

                    // Realizar una solicitud adicional a la API para obtener detalles específicos de la película
                    JsonObject movieDetails = fetchMovieDetails(movieId);

                    // Obtener detalles adicionales como el nombre del director y los nombres de los actores
                    String title = movieDetails.get("title").getAsString();
                    String overview = movieDetails.get("overview").getAsString();
                    String director = fetchDirector(movieId);
                    String releaseDate = movieDetails.get("release_date").getAsString(); // Ajustar según la API
                    List<String> genres = fetchGenres(movieId);
                    List<String> cast = fetchCast(movieId);

                    // Obtener el enlace del tráiler
                    String trailerLink = fetchTrailerLink(movieId);

                    // Pasar esta información a MovieDetailsFrame
                    new MovieDetailsFrame(title, overview, director, releaseDate, genres, cast, trailerLink);
                }
            });

            moviePanel.add(posterLabel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        contentPane.add(moviePanel);
        contentPane.add(Box.createRigidArea(new Dimension(0, 10))); // Espaciado vertical
        contentPane.revalidate();
        contentPane.repaint();
    }

    private JsonObject fetchMovieDetails(int movieId) {
        try {
            String urlStr = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + API_KEY + "&language=es-";
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            StringBuilder response;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            return JsonParser.parseString(response.toString()).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    private String fetchDirector(int movieId) {
        try {
            String urlStr = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + API_KEY + "&language=es-ES&append_to_response=credits";
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            StringBuilder response;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            JsonObject movieDetails = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonObject credits = movieDetails.getAsJsonObject("credits");
            JsonArray crew = credits.getAsJsonArray("crew");

            for (JsonElement crewMember : crew) {
                JsonObject crewObject = crewMember.getAsJsonObject();
                if (crewObject.get("job").getAsString().equals("Director")) {
                    String directorName = crewObject.get("name").getAsString();
                    return "" + directorName;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Director no encontrado";
    }

    private List<String> fetchGenres(int movieId) {
        List<String> genresList = new ArrayList<>();

        try {
            String urlStr = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + API_KEY + "&language=es-ES";
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            StringBuilder response;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            JsonObject movieDetails = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonArray genresArray = movieDetails.getAsJsonArray("genres");

            for (int i = 0; i < genresArray.size(); i++) {
                JsonObject genre = genresArray.get(i).getAsJsonObject();
                String genreName = genre.get("name").getAsString();
                genresList.add(genreName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return genresList;
    }

    private List<String> fetchCast(int movieId) {
        List<String> castList = new ArrayList<>();

        try {
            String urlStr = "https://api.themoviedb.org/3/movie/" + movieId + "/credits?api_key=" + API_KEY;
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            StringBuilder response;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            JsonObject creditsObject = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonArray castArray = creditsObject.getAsJsonArray("cast");

            for (int i = 0; i < castArray.size(); i++) {
                JsonObject actor = castArray.get(i).getAsJsonObject();
                String actorName = actor.get("name").getAsString();
                castList.add(actorName);
            }

            // Ordenar la lista alfabéticamente
            castList.sort(String::compareToIgnoreCase);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return castList;
    }
 private String fetchTrailerLink(int movieId) {
        try {
            String urlStr = "https://api.themoviedb.org/3/movie/" + movieId + "/videos?api_key=" + API_KEY + "&language=es-ES";
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            StringBuilder response;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            JsonObject videosObject = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonArray resultsArray = videosObject.getAsJsonArray("results");

            if (resultsArray.size() > 0) {
                JsonObject firstVideo = resultsArray.get(0).getAsJsonObject();
                return "https://www.youtube.com/watch?v=" + firstVideo.get("key").getAsString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Cinema::new);
    }
}
