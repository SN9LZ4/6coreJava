package tn.esprit.services;

import java.awt.Desktop;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javafx.application.HostServices;
import javafx.application.Platform;

public class SocialSharingService {
    
    private static HostServices hostServices;
    
    public static void setHostServices(HostServices services) {
        hostServices = services;
    }
    
    public static void shareOnTwitter(String title, String url, String hashtags) {
        try {
            String tweetText = String.format("%s %s", title, hashtags);
            String tweetUrl = String.format(
                "https://twitter.com/intent/tweet?text=%s&url=%s",
                URLEncoder.encode(tweetText, StandardCharsets.UTF_8),
                URLEncoder.encode(url, StandardCharsets.UTF_8)
            );
            openLink(tweetUrl);
        } catch (Exception e) {
            throw new ShareException("Error sharing on Twitter", e);
        }
    }

    public static void shareOnFacebook(String url, String quote) {
        try {
            String shareUrl = String.format(
                "https://www.facebook.com/sharer/sharer.php?u=%s&quote=%s",
                URLEncoder.encode(url, StandardCharsets.UTF_8),
                URLEncoder.encode(quote, StandardCharsets.UTF_8)
            );
            openLink(shareUrl);
        } catch (Exception e) {
            throw new ShareException("Error sharing on Facebook", e);
        }
    }

    public static void shareOnLinkedIn(String url, String title, String summary) {
        try {
            String shareUrl = String.format(
                "https://www.linkedin.com/sharing/share-offsite/?url=%s",
                URLEncoder.encode(url, StandardCharsets.UTF_8)
            );
            openLink(shareUrl);
        } catch (Exception e) {
            throw new ShareException("Error sharing on LinkedIn", e);
        }
    }

    public static void shareOnWhatsApp(String text, String url) {
        try {
            String shareText = text + " " + url;
            String shareUrl = String.format(
                "https://api.whatsapp.com/send?text=%s",
                URLEncoder.encode(shareText, StandardCharsets.UTF_8)
            );
            openLink(shareUrl);
        } catch (Exception e) {
            throw new ShareException("Error sharing on WhatsApp", e);
        }
    }

    public static void shareByEmail(String subject, String body, String[] recipients) {
        try {
            String mailtoUrl = String.format(
                "mailto:%s?subject=%s&body=%s",
                String.join(",", recipients),
                URLEncoder.encode(subject, StandardCharsets.UTF_8),
                URLEncoder.encode(body, StandardCharsets.UTF_8)
            );
            openLink(mailtoUrl);
        } catch (Exception e) {
            throw new ShareException("Error sharing via email", e);
        }
    }

    private static void openLink(String url) {
        try {
            if (hostServices != null) {
                Platform.runLater(() -> hostServices.showDocument(url));
            } else {
                // Fallback to Desktop API
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new URI(url));
                } else {
                    throw new ShareException("Desktop browsing not supported on this platform");
                }
            }
        } catch (Exception e) {
            throw new ShareException("Error opening link: " + url, e);
        }
    }

    public static class ShareException extends RuntimeException {
        public ShareException(String message) {
            super(message);
        }
        
        public ShareException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}