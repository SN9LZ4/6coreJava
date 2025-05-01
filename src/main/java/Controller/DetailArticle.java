package Controller;

import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.entities.User;
import tn.esprit.entities.article;
import tn.esprit.entities.commentaire;
import tn.esprit.services.commentaireService;
import tn.esprit.services.ArticleService;
import tn.esprit.services.ServiceException;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONObject;

public class DetailArticle {

    @FXML private Label titleLabel;
    @FXML private Label categorieLabel;
    @FXML private Label dateLabel;
    @FXML private Label contenuLabel;
    @FXML private ImageView articleImage;
    @FXML private TextArea commentaireTextArea;
    @FXML private VBox commentairesContainer;
    @FXML private Label commentaireErrorLabel;
    @FXML private Label ratingLabel;
    @FXML private Label ratingCountLabel;
    @FXML private HBox starsContainer;
    @FXML private HBox userRatingContainer;
    @FXML private Label likesCount;
    @FXML private Label dislikesCount;
    @FXML private Button likeButton;
    @FXML private Button dislikeButton;
    @FXML private Label sentimentLabel;
    @FXML private Label movieQuoteLabel; // Ajout d'un label pour la citation

    private final commentaireService commentaireService = new commentaireService();
    private final ArticleService articleService = new ArticleService();
    private article currentArticle;
    private User currentUser = new User(32, "nom", "prenom", false);
    private static final Map<String, Integer> userRatings = new HashMap<>();
    private static final Map<String, String> userReactions = new HashMap<>();

    public void setArticle(article a) {
        this.currentArticle = a;
        titleLabel.setText(a.getTitre());
        categorieLabel.setText("Catégorie : " + a.getCategorie());
        dateLabel.setText("Date de publication : " + new SimpleDateFormat("dd MMM yyyy").format(a.getDate_publication()));
        contenuLabel.setText(a.getContenu());

        // Analyze article sentiment
        String sentiment = SentimentAnalyzerRapidAPI.analyzeSentiment(a.getContenu());
        sentimentLabel.setText("Sentiment: " + sentiment);
        if (sentiment.contains("positif")) {
            sentimentLabel.setStyle("-fx-text-fill: green;");
        } else if (sentiment.contains("négatif")) {
            sentimentLabel.setStyle("-fx-text-fill: red;");
        } else {
            sentimentLabel.setStyle("-fx-text-fill: orange;");
        }

        File file = new File("src/main/resources/images/" + a.getImage());
        if (file.exists()) {
            articleImage.setImage(new Image(file.toURI().toString()));
        }

        // Charger une citation de film aléatoire
        fetchRandomMovieQuote();

        afficherCommentaires();
        initializeRatingAndReactions();
    }

    private void fetchRandomMovieQuote() {
        new Thread(() -> {
            try {
                // Liste d'APIs alternatives pour diversifier les citations
                List<String> quoteApis = Arrays.asList(
                    "https://api.quotable.io/random?tags=movies",
                    "https://api.quotable.io/random?tags=famous-quotes",
                    "https://api.quotable.io/random?tags=wisdom",
                    "https://api.themotivate365.com/stoic-quote",
                    "https://api.kanye.rest/",
                    "https://api.chucknorris.io/jokes/random"
                );
                
                // Choisir une API aléatoirement
                String apiUrl = quoteApis.get(new Random().nextInt(quoteApis.size()));
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                
                if (conn.getResponseCode() != 200) {
                    // Si l'API choisie ne fonctionne pas, essayer l'API principale
                    url = new URL("https://api.quotable.io/random");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "application/json");
                    
                    if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("Erreur HTTP: " + conn.getResponseCode());
                    }
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();
                conn.disconnect();

                JSONObject quoteJson = new JSONObject(response.toString());
                String quote = "";
                String author = "";
                
                // Extraire la citation selon le format de l'API
                if (apiUrl.contains("quotable.io")) {
                    quote = quoteJson.getString("content");
                    author = quoteJson.getString("author");
                } else if (apiUrl.contains("themotivate365")) {
                    quote = quoteJson.getString("quote");
                    author = quoteJson.getString("author");
                } else if (apiUrl.contains("kanye.rest")) {
                    quote = quoteJson.getString("quote");
                    author = "Kanye West";
                } else if (apiUrl.contains("chucknorris.io")) {
                    quote = quoteJson.getString("value");
                    author = "Chuck Norris";
                }

                // Variables finales pour utilisation dans le lambda
                final String finalQuote = quote;
                final String finalAuthor = author;
                
                // Mettre à jour l'interface utilisateur sur le thread JavaFX
                javafx.application.Platform.runLater(() -> {
                    if (movieQuoteLabel != null) {
                        // Créer un conteneur pour la citation avec un style amélioré
                        VBox quoteContainer = new VBox(10);
                        quoteContainer.getStyleClass().add("quote-container");
                        quoteContainer.setOpacity(0);
                        
                        // Titre de la section citation
                        Label quoteTitle = new Label("Citation du jour");
                        quoteTitle.getStyleClass().add("quote-title");
                        
                        // Citation avec style amélioré
                        Label quoteText = new Label("\"" + finalQuote + "\"");
                        quoteText.getStyleClass().add("quote-text");
                        quoteText.setWrapText(true);
                        
                        // Auteur avec style distinct
                        Label authorText = new Label("— " + finalAuthor);
                        authorText.getStyleClass().add("quote-author");
                        
                        // Ajouter les éléments au conteneur
                        quoteContainer.getChildren().addAll(quoteTitle, quoteText, authorText);
                        
                        // Remplacer le label simple par notre conteneur élaboré
                        // Note: Cela nécessite de modifier la structure FXML pour avoir un conteneur parent
                        // où nous pouvons ajouter notre quoteContainer
                        if (movieQuoteLabel.getParent() instanceof Pane) {
                            Pane parent = (Pane) movieQuoteLabel.getParent();
                            int index = parent.getChildren().indexOf(movieQuoteLabel);
                            parent.getChildren().remove(movieQuoteLabel);
                            parent.getChildren().add(index, quoteContainer);
                            
                            // Animation d'entrée élaborée
                            FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), quoteContainer);
                            fadeIn.setFromValue(0);
                            fadeIn.setToValue(1);
                            
                            // Animation de translation
                            TranslateTransition slideIn = new TranslateTransition(Duration.millis(1000), quoteContainer);
                            slideIn.setFromY(20);
                            slideIn.setToY(0);
                            
                            // Combiner les animations
                            ParallelTransition parallelTransition = new ParallelTransition(fadeIn, slideIn);
                            parallelTransition.play();
                        } else {
                            // Fallback si nous ne pouvons pas remplacer le label
                            movieQuoteLabel.setText("\"" + finalQuote + "\" — " + finalAuthor);
                            movieQuoteLabel.setOpacity(0);
                            
                            FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), movieQuoteLabel);
                            fadeIn.setFromValue(0);
                            fadeIn.setToValue(1);
                            fadeIn.play();
                        }
                    }
                });

            } catch (Exception e) {
                System.err.println("Erreur lors de la récupération de la citation: " + e.getMessage());
                
                // En cas d'erreur, utiliser une liste de citations locales
                List<String[]> localQuotes = Arrays.asList(
                    new String[]{"Le cinéma, c'est l'écriture moderne dont l'encre est la lumière.", "Jean Cocteau"},
                    new String[]{"Un film, c'est une étoile filante dans l'univers des rêves.", "Federico Fellini"},
                    new String[]{"Le cinéma, c'est un œil ouvert sur le monde.", "Joseph Kosma"},
                    new String[]{"La vie, c'est comme une boîte de chocolats, on ne sait jamais sur quoi on va tomber.", "Forrest Gump"},
                    new String[]{"May the Force be with you.", "Star Wars"},
                    new String[]{"Je suis le roi du monde!", "Titanic"},
                    new String[]{"La vie trouve toujours un chemin.", "Jurassic Park"},
                    new String[]{"Houston, nous avons un problème.", "Apollo 13"},
                    new String[]{"Je reviendrai.", "Terminator"},
                    new String[]{"Carpe diem. Saisissez l'instant présent.", "Le Cercle des poètes disparus"}
                );
                
                // Choisir une citation aléatoire
                String[] randomQuote = localQuotes.get(new Random().nextInt(localQuotes.size()));
                
                javafx.application.Platform.runLater(() -> {
                    if (movieQuoteLabel != null) {
                        movieQuoteLabel.setOpacity(0);
                        movieQuoteLabel.setText("\"" + randomQuote[0] + "\" — " + randomQuote[1]);
                        movieQuoteLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #1a4d8c; -fx-font-size: 16px;");
                        
                        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), movieQuoteLabel);
                        fadeIn.setFromValue(0);
                        fadeIn.setToValue(1);
                        fadeIn.play();
                    }
                });
            }
        }).start();
    }

    private void initializeRatingAndReactions() {
        updateRatingDisplay();
        updateReactionCounts();
        if (currentUser != null) {
            String key = currentUser.getUser_id() + "_" + currentArticle.getId();
            updateUserRatingButtons(key);
            updateReactionButtons(key);
        }
    }

    private void updateRatingDisplay() {
        double avgRating = currentArticle.getTotal_rating();
        int ratingCount = currentArticle.getRating_count();
        ratingLabel.setText(String.format("%.1f", avgRating));
        ratingCountLabel.setText("(" + ratingCount + " votes)");
        starsContainer.getChildren().clear();
        for (int i = 0; i < 5; i++) {
            ImageView star = new ImageView();
            star.setFitWidth(25);
            star.setFitHeight(25);
            if (i < Math.round(avgRating)) {
                star.setImage(new Image(getClass().getResourceAsStream("/images/star.png")));
            } else {
                star.setImage(new Image(getClass().getResourceAsStream("/images/favorite.png")));
            }
            starsContainer.getChildren().add(star);
        }
    }

    private void updateReactionCounts() {
        likesCount.setText(String.valueOf(currentArticle.getLikes()));
        dislikesCount.setText(String.valueOf(currentArticle.getDislikes()));
    }

    @FXML
    private void handleRating(ActionEvent event) {
        if (currentUser == null) {
            showInlineError("Veuillez vous connecter pour noter l'article.");
            return;
        }
        Button clickedButton = (Button) event.getSource();
        int newRating = Integer.parseInt(clickedButton.getText());
        String key = currentUser.getUser_id() + "_" + currentArticle.getId();
        Integer currentRating = userRatings.get(key);

        try {
            if (currentRating != null) {
                double currentTotal = currentArticle.getTotal_rating() * currentArticle.getRating_count();
                currentTotal -= currentRating;
                int newCount = currentArticle.getRating_count() - 1;
                double newAverage = newCount > 0 ? currentTotal / newCount : 0;
                currentArticle.setTotal_rating(newAverage);
                currentArticle.setRating_count(newCount);
            }
            if (currentRating != null && currentRating == newRating) {
                userRatings.remove(key);
            } else {
                userRatings.put(key, newRating);
                double currentTotal = currentArticle.getTotal_rating() * currentArticle.getRating_count();
                int newCount = currentArticle.getRating_count() + 1;
                double newAverage = (currentTotal + newRating) / newCount;
                currentArticle.setTotal_rating(newAverage);
                currentArticle.setRating_count(newCount);
            }
            articleService.edit(currentArticle);
            updateRatingDisplay();
            updateUserRatingButtons(key);
        } catch (ServiceException e) {
            showInlineError("Erreur lors de la notation de l'article.");
        }
    }

    @FXML
    private void handleLike() { handleReaction("LIKE"); }

    @FXML
    private void handleDislike() { handleReaction("DISLIKE"); }

    private void handleReaction(String newReaction) {
        if (currentUser == null) {
            showInlineError("Veuillez vous connecter pour réagir.");
            return;
        }
        String key = currentUser.getUser_id() + "_" + currentArticle.getId();
        String currentReaction = userReactions.get(key);
        try {
            if (currentReaction != null) {
                if ("LIKE".equals(currentReaction)) currentArticle.setLikes(currentArticle.getLikes() - 1);
                else currentArticle.setDislikes(currentArticle.getDislikes() - 1);
            }
            if (currentReaction != null && currentReaction.equals(newReaction)) {
                userReactions.remove(key);
            } else {
                userReactions.put(key, newReaction);
                if ("LIKE".equals(newReaction)) currentArticle.setLikes(currentArticle.getLikes() + 1);
                else currentArticle.setDislikes(currentArticle.getDislikes() + 1);
            }
            articleService.edit(currentArticle);
            updateReactionCounts();
            updateReactionButtons(key);
        } catch (ServiceException e) {
            showInlineError("Erreur de réaction.");
        }
    }

    private void updateReactionButtons(String key) {
        String currentReaction = userReactions.get(key);
        likeButton.getStyleClass().remove("active");
        dislikeButton.getStyleClass().remove("active");
        if ("LIKE".equals(currentReaction)) likeButton.getStyleClass().add("active");
        else if ("DISLIKE".equals(currentReaction)) dislikeButton.getStyleClass().add("active");
    }

    private void updateUserRatingButtons(String key) {
        Integer userRating = userRatings.get(key);
        for (Node node : userRatingContainer.getChildren()) {
            if (node instanceof Button btn) {
                btn.getStyleClass().remove("selected");
                if (userRating != null && Integer.parseInt(btn.getText()) <= userRating) {
                    btn.getStyleClass().add("selected");
                }
            }
        }
    }

    @FXML
    private void handleComment() {
        if (currentUser == null) {
            showInlineError("Veuillez vous connecter pour commenter.");
            return;
        }

        String userComment = commentaireTextArea.getText().trim();
        if (userComment.isEmpty()) {
            showInlineError("Le commentaire est vide.");
            return;
        }
        if (userComment.length() < 5) {
            showInlineError("Minimum 5 caractères.");
            return;
        }
        if (userComment.length() > 500) {
            showInlineError("Maximum 500 caractères.");
            return;
        }

        // Analyze comment sentiment before posting
        String sentiment = SentimentAnalyzerRapidAPI.analyzeSentiment(userComment);
        if (sentiment.contains("😠 Négatif")) {
            showInlineError("Attention: Votre commentaire semble très négatif. Voulez-vous le reformuler ?");
            return;
        }

        String filteredComment = ProfanityFilter.filterText(userComment);
        if (!filteredComment.equals(userComment)) {
            commentaireTextArea.setText(filteredComment);
            showInlineError("Contenu corrigé (mots inappropriés détectés).");
            return;
        }

        try {
            commentaire newComment = new commentaire(
                filteredComment,
                currentUser,
                currentArticle,
                new Date()
            );
            
            commentaireService.add(newComment);
            commentaireTextArea.clear();
            afficherCommentaires();
            commentaireErrorLabel.setText("");
            commentaireTextArea.setStyle("-fx-border-color: green;");
        } catch (ServiceException e) {
            showInlineError("Erreur d'enregistrement du commentaire: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void afficherCommentaires() {
        commentairesContainer.getChildren().clear();
        try {
            List<commentaire> commentaires = commentaireService.getCommentairesByArticle(currentArticle);
            for (commentaire com : commentaires) {
                VBox box = new VBox(5);
                box.setStyle("-fx-background-color: #f1f1f1; -fx-padding: 10; -fx-border-radius: 8;");
                
                // En-tête du commentaire avec nom d'utilisateur et actions
                HBox header = new HBox(10);
                header.setAlignment(Pos.CENTER_LEFT);
                
                Label userLabel = new Label(com.getUser().getNom() + " " + com.getUser().getPrenom());
                userLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #0d1b2a;");
                
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                
                // Boutons d'action avec icônes
                HBox actions = new HBox(5);
                actions.setAlignment(Pos.CENTER_RIGHT);
                
                // Bouton Modifier
                Button editBtn = new Button();
                editBtn.getStyleClass().add("icon-button");
                editBtn.setStyle("-fx-background-color: transparent;");
                ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/edit.png")));
                editIcon.setFitHeight(16);
                editIcon.setFitWidth(16);
                editBtn.setGraphic(editIcon);
                Tooltip.install(editBtn, new Tooltip("Modifier"));
                
                // Bouton Supprimer
                Button deleteBtn = new Button();
                deleteBtn.getStyleClass().add("icon-button");
                deleteBtn.setStyle("-fx-background-color: transparent;");
                ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/delete.png")));
                deleteIcon.setFitHeight(16);
                deleteIcon.setFitWidth(16);
                deleteBtn.setGraphic(deleteIcon);
                Tooltip.install(deleteBtn, new Tooltip("Supprimer"));
                
                // N'afficher les boutons que si l'utilisateur est l'auteur ou un admin
                boolean isAuthor = currentUser != null && currentUser.getUser_id() == com.getUser().getUser_id();
                boolean isAdmin = currentUser != null && currentUser.isAdmin();
                
                if (isAuthor || isAdmin) {
                    actions.getChildren().addAll(editBtn, deleteBtn);
                }
                
                // Ajouter les actions pour les boutons
                editBtn.setOnAction(e -> editComment(com));
                deleteBtn.setOnAction(e -> deleteComment(com));
                
                header.getChildren().addAll(userLabel, spacer, actions);
                
                // Contenu du commentaire
                Label contenuLabel = new Label(com.getContenuCom());
                contenuLabel.setWrapText(true);
                contenuLabel.setStyle("-fx-text-fill: #333333;");
                
                // Date du commentaire
                Label dateLabel = new Label(new SimpleDateFormat("dd MMM yyyy, HH:mm").format(com.getDate()));
                dateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666666;");
                
                box.getChildren().addAll(header, contenuLabel, dateLabel);
                commentairesContainer.getChildren().add(box);
            }
        } catch (ServiceException e) {
            showInlineError("Erreur chargement commentaires.");
        }
    }

    private void editComment(commentaire com) {
        // Créer une boîte de dialogue pour éditer le commentaire
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Modifier le commentaire");
        
        // Configurer les boutons
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Créer la zone de texte pour éditer le commentaire
        TextArea textArea = new TextArea(com.getContenuCom());
        textArea.setWrapText(true);
        textArea.setPrefRowCount(5);
        textArea.setPrefColumnCount(40);
        
        dialog.getDialogPane().setContent(textArea);
        
        // Convertir le résultat en String lorsque le bouton Enregistrer est cliqué
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return textArea.getText();
            }
            return null;
        });
        
        // Afficher la boîte de dialogue et traiter le résultat
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newContent -> {
            try {
                // Vérifier si le contenu a changé
                if (!newContent.equals(com.getContenuCom())) {
                    com.setContenuCom(newContent);
                    commentaireService.edit(com, currentUser);
                    afficherCommentaires(); // Rafraîchir la liste
                }
            } catch (ServiceException e) {
                showInlineError("Erreur lors de la modification du commentaire.");
            }
        });
    }

    private void deleteComment(commentaire com) {
        // Demander confirmation avant de supprimer
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le commentaire");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce commentaire ?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                commentaireService.delete(com, currentUser);
                afficherCommentaires(); // Rafraîchir la liste
            } catch (ServiceException e) {
                showInlineError("Erreur lors de la suppression du commentaire.");
            }
        }
    }

    @FXML
    private void initialize() {
        for (Node node : userRatingContainer.getChildren()) {
            if (node instanceof Button button) button.setOnAction(this::handleRating);
        }
        commentaireTextArea.textProperty().addListener((obs, oldText, newText) -> validateCommentaire(newText));
    }

    private void validateCommentaire(String text) {
        if (text.trim().isEmpty()) commentaireErrorLabel.setText("Le commentaire est vide.");
        else if (text.length() < 5) commentaireErrorLabel.setText("Minimum 5 caractères.");
        else if (text.length() > 500) commentaireErrorLabel.setText("Maximum 500 caractères.");
        else commentaireErrorLabel.setText("");
    }

    private void showInlineError(String message) {
        commentaireErrorLabel.setText(message);
        commentaireTextArea.setStyle("-fx-border-color: red;");
    }

    @FXML
    private void handleTranslateToArabic() {
        String title = titleLabel.getText();
        String content = contenuLabel.getText();

        String translatedTitle = TranslationService.translateText(title, "fr", "ar");
        String translatedContent = TranslationService.translateText(content, "fr", "ar");

        if (translatedTitle != null) {
            titleLabel.setText(translatedTitle);
        }
        if (translatedContent != null) {
            contenuLabel.setText(translatedContent);
        }
    }

    @FXML
    private void handleTranslateToFrench() {
        String title = titleLabel.getText();
        String content = contenuLabel.getText();

        String translatedTitle = TranslationService.translateText(title, "ar", "fr");
        String translatedContent = TranslationService.translateText(content, "ar", "fr");

        if (translatedTitle != null) {
            titleLabel.setText(translatedTitle);
        }
        if (translatedContent != null) {
            contenuLabel.setText(translatedContent);
        }
    }

    @FXML
    private void openShareDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ShareDialog.fxml"));
            Parent root = loader.load();
            
            ShareDialog controller = loader.getController();
            controller.setArticle(currentArticle, "https://your-website.com");
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(titleLabel.getScene().getWindow());
            dialogStage.setTitle("Share Article");
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            dialogStage.setScene(scene);
            
            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle error
        }
    }
}
