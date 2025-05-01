package Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import tn.esprit.entities.article;
import tn.esprit.services.SocialSharingService;

public class ShareDialog {
    
    @FXML
    private VBox shareContainer;
    
    private article currentArticle;
    private String shareUrl;
    // Use a valid public URL for production
    private static final String BASE_URL = "https://f0a1-41-231-101-22.ngrok.io";
    private static final boolean IS_DEVELOPMENT = false; // Changed too false to enable sharing

    public void initialize() {
        setupSharingButtons();
    }
    
    public void setArticle(article article, String baseUrl) {
        this.currentArticle = article;
        // For testing purposes, use a public URL
        this.shareUrl = "https://example.com/article/" + article.getId();
    }
    
    private void setupSharingButtons() {
        // Twitter
        Button twitterButton = createSocialButton(
            "Twitter", 
            "images/twitter.png",
            () -> SocialSharingService.shareOnTwitter(
                formatTwitterText(currentArticle.getTitre()),
                shareUrl,
                "#EspritArticles #Blog"
            )
        );
        
        // Facebook
        Button facebookButton = createSocialButton(
            "Facebook",
            "images/facebook.png",
            () -> SocialSharingService.shareOnFacebook(
                shareUrl,
                "Check out this interesting article: " + currentArticle.getTitre()
            )
        );
        

        
        // WhatsApp
        Button whatsappButton = createSocialButton(
            "WhatsApp",
            "images/whatsapp.png",
            () -> SocialSharingService.shareOnWhatsApp(
                "📰 " + currentArticle.getTitre(),
                shareUrl
            )
        );

        
        shareContainer.getChildren().addAll(
            createSocialButtonRow(twitterButton, facebookButton),
            createSocialButtonRow( whatsappButton)
        );
    }
    
    private HBox createSocialButtonRow(Button... buttons) {
        HBox row = new HBox(10);
        row.getChildren().addAll(buttons);
        return row;
    }
    
    private Button createSocialButton(String platform, String iconPath, Runnable action) {
        Button button = new Button("Share on " + platform);
        button.getStyleClass().addAll("share-button", "share-" + platform.toLowerCase());
        
        // Add icon
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/" + iconPath)));
        icon.setFitHeight(20);
        icon.setFitWidth(20);
        button.setGraphic(icon);
        
        // Add tooltip
        button.setTooltip(new Tooltip("Share this article on " + platform));
        
        button.setOnAction(e -> {
            try {
                action.run();
            } catch (SocialSharingService.ShareException ex) {
                showError("Error sharing to " + platform, ex.getMessage());
            }
        });
        
        return button;
    }
    
    private String formatTwitterText(String title) {
        // Limit to Twitter's character limit
        int maxLength = 280 - (shareUrl.length() + 20); // Account for URL and hashtags
        return title.length() > maxLength ? 
               title.substring(0, maxLength - 3) + "..." : 
               title;
    }
    
    private String formatLinkedInSummary(article article) {
        String summary = article.getContenu();
        int maxLength = 256; // LinkedIn's summary limit
        return summary.length() > maxLength ? 
               summary.substring(0, maxLength - 3) + "..." : 
               summary;
    }
    
    private String formatEmailBody() {
        return String.format(
            "Bonjour,\n\n" +
            "Je pense que cet article pourrait vous intéresser :\n\n" +
            "%s\n\n" +
            "Par : %s\n" +
            "Catégorie : %s\n\n" +
            "Lire l'article : %s\n\n" +
            "Cordialement",
            currentArticle.getTitre(),
            currentArticle.getContenu(),
            currentArticle.getCategorie(),
            shareUrl
        );
    }
    
    private void showDevelopmentModeWarning() {
        showError(
            "Development Mode", 
            "Social sharing is disabled in development mode. Please use the production environment for sharing functionality."
        );
    }
    
    private void showError(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
