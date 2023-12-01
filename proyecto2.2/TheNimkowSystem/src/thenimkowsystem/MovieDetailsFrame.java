
package thenimkowsystem;

/**
 *
 * @author Julieth
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class MovieDetailsFrame extends JFrame {
    private static final int LINE_LENGTH_LIMIT = 50;
    private static final int MAX_CAST_HEIGHT = 150;
    private static final int CAST_PER_LINE = 3;

    public MovieDetailsFrame(String title, String overview, String director, String releaseDate, List<String> genres, List<String> cast, String trailerLink) {
        setTitle(title);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panel.setBackground(new Color(255, 248, 225)); // Fondo color almendra

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(255, 248, 225)); // Fondo color almendra

        JLabel titleLabel = createStyledLabel(title, Font.BOLD, 26, new Color(178, 34, 34)); // Rojo oscuro
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel overviewLabel = createStyledLabel("Overview", Font.BOLD, 20, new Color(178, 34, 34)); // Rojo oscuro

        JTextArea overviewTextArea = createStyledTextArea(insertLineBreaks(overview, LINE_LENGTH_LIMIT), Font.PLAIN, 16, Color.BLACK);
        overviewTextArea.setEditable(false);
        overviewTextArea.setLineWrap(true);
        overviewTextArea.setWrapStyleWord(true);

        JLabel directorLabel = createStyledLabel("Director: " + director, Font.BOLD, 16, new Color(178, 34, 34)); // Rojo oscuro

        JLabel releaseDateLabel = createStyledLabel("Release Date: " + releaseDate, Font.BOLD, 16, new Color(178, 34, 34)); // Rojo oscuro

        JLabel genresLabel = createStyledLabel("Genres: " + String.join(", ", genres), Font.BOLD, 16, new Color(178, 34, 34)); // Rojo oscuro

        JPanel castPanel = new JPanel(new GridLayout(0, CAST_PER_LINE, 5, 5));
        for (String actor : cast) {
            JLabel actorLabel = createStyledLabel(actor, Font.PLAIN, 14, Color.BLACK);
            castPanel.add(actorLabel);
        }

        JScrollPane castScrollPane = new JScrollPane(castPanel);
        castScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, MAX_CAST_HEIGHT));

        // Modificación para el enlace del tráiler
        JLabel trailerLabel = createStyledLabel("Ver Tráiler", Font.BOLD, 16, Color.BLUE);
        trailerLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        trailerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (trailerLink != null && !trailerLink.isEmpty()) {
                    try {
                        // Abrir el enlace del tráiler en el navegador
                        Desktop.getDesktop().browse(new URI(trailerLink));
                    } catch (IOException | URISyntaxException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    System.out.println("El enlace del tráiler es nulo o vacío.");
                }
            }
        });

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Espacio vertical
        contentPanel.add(overviewLabel);
        contentPanel.add(overviewTextArea);
        contentPanel.add(directorLabel);
        contentPanel.add(releaseDateLabel);
        contentPanel.add(genresLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Espacio vertical
        contentPanel.add(castScrollPane);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Espacio vertical
        contentPanel.add(trailerLabel); // Nuevo enlace del tráiler

        panel.add(contentPanel, BorderLayout.WEST);

        add(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JLabel createStyledLabel(String text, int style, int fontSize, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", style, fontSize));
        label.setForeground(color);
        return label;
    }

    private JTextArea createStyledTextArea(String text, int style, int fontSize, Color color) {
        JTextArea textArea = new JTextArea(text);
        textArea.setFont(new Font("Arial", style, fontSize));
        textArea.setForeground(color);
        textArea.setOpaque(false); // Hacer el fondo transparente
        return textArea;
    }

    private String insertLineBreaks(String input, int lineLength) {
        StringBuilder result = new StringBuilder();
        int length = input.length();

        for (int i = 0; i < length; i++) {
            result.append(input.charAt(i));

            if ((i + 1) % lineLength == 0 && i < length - 1) {
                result.append("\n");
            }
        }

        return result.toString();
    }

    public static void main(String[] args) {
       SwingUtilities.invokeLater(() -> {
    String title = "Movie Title";
    String overview = "This is a long overview that needs line breaks to fit in the frame. This is a long overview that needs line breaks to fit in the frame.";
    String director = "Director Name";
    String releaseDate = "2023-01-01";
    List<String> genres = List.of("Genre 1", "Genre 2", "Genre 3");
    List<String> cast = List.of("Actor 1", "Actor 2", "Actor 3", "Actor 4", "Actor 5", "Actor 6", "Actor 7");

    // Enlace de ejemplo del tráiler de YouTube
    String trailerLink = "https://www.youtube.com/watch?v=XXXXXXXXXXX";

    new MovieDetailsFrame(title, overview, director, releaseDate, genres, cast, trailerLink);
});
    }
}
