package Controller;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import tn.esprit.entities.article;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Calendar;

public class PDFGenerator {

    public static void generateArticleStatsPDF(article article, PieChart reactionsPieChart, BarChart<String, Number> ratingChart) throws IOException, DocumentException {
        // Créer le document
        Document document = new Document(PageSize.A4);
        String fileName = "article_stats_" + article.getId() + ".pdf";
        String filePath = System.getProperty("user.home") + "/Downloads/" + fileName;
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));

        // Ajouter des métadonnées
        document.addTitle("Statistiques de l'article: " + article.getTitre());
        document.addAuthor("ChouFliFilm");
        document.addCreator("ChouFliFilm Article Stats Generator");

        document.open();

        // Ajouter un en-tête avec logo
        try {
            Image logo = Image.getInstance(PDFGenerator.class.getResource("/images/logo.png"));
            logo.scaleToFit(100, 100);
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);
        } catch (Exception e) {
            // Continuer sans logo si l'image n'est pas trouvée
        }

        // Titre du document
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.DARK_GRAY);
        Paragraph title = new Paragraph("Statistiques de l'article", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Informations de l'article
        Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.DARK_GRAY);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);

        Paragraph articleInfo = new Paragraph("Informations de l'article", subtitleFont);
        articleInfo.setSpacingBefore(15);
        articleInfo.setSpacingAfter(10);
        document.add(articleInfo);

        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingBefore(10f);
        infoTable.setSpacingAfter(10f);

        // Définir les largeurs relatives des colonnes
        float[] columnWidths = {1f, 3f};
        infoTable.setWidths(columnWidths);

        // Ajouter les informations de l'article
        addTableRow(infoTable, "Titre:", article.getTitre(), boldFont, normalFont);
        addTableRow(infoTable, "Catégorie:", article.getCategorie(), boldFont, normalFont);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        addTableRow(infoTable, "Date de publication:", dateFormat.format(article.getDate_publication()), boldFont, normalFont);
        addTableRow(infoTable, "Note moyenne:", String.format("%.1f/5", article.getTotal_rating()), boldFont, normalFont);
        addTableRow(infoTable, "Nombre de votes:", String.valueOf(article.getRating_count()), boldFont, normalFont);
        addTableRow(infoTable, "J'aime:", String.valueOf(article.getLikes()), boldFont, normalFont);
        addTableRow(infoTable, "Je n'aime pas:", String.valueOf(article.getDislikes()), boldFont, normalFont);

        document.add(infoTable);

        // Ajouter les graphiques
        Paragraph chartsTitle = new Paragraph("Graphiques statistiques", subtitleFont);
        chartsTitle.setSpacingBefore(20);
        chartsTitle.setSpacingAfter(10);
        document.add(chartsTitle);

        // Ajouter le graphique des réactions
        if (reactionsPieChart != null) {
            document.add(new Paragraph("Répartition des réactions:", boldFont));
            document.add(new Paragraph(" "));

            try {
                // Sauvegarder temporairement l'image du graphique
                File tempFile = saveNodeToTempFile(reactionsPieChart, "pie_chart");
                Image chartImage = Image.getInstance(tempFile.getAbsolutePath());
                chartImage.scaleToFit(400, 300);
                chartImage.setAlignment(Element.ALIGN_CENTER);
                document.add(chartImage);
                tempFile.delete(); // Supprimer le fichier temporaire
            } catch (Exception e) {
                document.add(new Paragraph("Impossible d'ajouter le graphique des réactions: " + e.getMessage(), normalFont));
            }
        }

        // Ajouter le graphique des notes
        if (ratingChart != null) {
            document.add(new Paragraph("Distribution des notes:", boldFont));
            document.add(new Paragraph(" "));

            try {
                // Sauvegarder temporairement l'image du graphique
                File tempFile = saveNodeToTempFile(ratingChart, "bar_chart");
                Image chartImage = Image.getInstance(tempFile.getAbsolutePath());
                chartImage.scaleToFit(400, 300);
                chartImage.setAlignment(Element.ALIGN_CENTER);
                document.add(chartImage);
                tempFile.delete(); // Supprimer le fichier temporaire
            } catch (Exception e) {
                document.add(new Paragraph("Impossible d'ajouter le graphique des notes: " + e.getMessage(), normalFont));
            }
        }

        // Ajouter un pied de page
        Paragraph footer = new Paragraph("Document généré le " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), normalFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(30);
        document.add(footer);

        // Fermer le document
        document.close();

        // Ouvrir le PDF
        try {
            java.awt.Desktop.getDesktop().open(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File saveNodeToTempFile(Node node, String prefix) throws IOException {
        // Prendre un snapshot du nœud JavaFX
        WritableImage snapshot = node.snapshot(new SnapshotParameters(), null);

        // Créer un fichier temporaire
        Path tempPath = Files.createTempFile(prefix, ".png");
        File tempFile = tempPath.toFile();

        // Écrire l'image dans le fichier temporaire
        javax.imageio.ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();
        javax.imageio.stream.ImageOutputStream output =
            ImageIO.createImageOutputStream(new FileOutputStream(tempFile));
        writer.setOutput(output);

        // Convertir WritableImage en BufferedImage
        int width = (int) snapshot.getWidth();
        int height = (int) snapshot.getHeight();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                javafx.scene.paint.Color color = snapshot.getPixelReader().getColor(x, y);
                int argb = (
                    (int) (color.getOpacity() * 255) << 24 |
                    (int) (color.getRed() * 255) << 16 |
                    (int) (color.getGreen() * 255) << 8 |
                    (int) (color.getBlue() * 255)
                );
                bufferedImage.setRGB(x, y, argb);
            }
        }

        // Écrire l'image
        writer.write(bufferedImage);
        output.close();
        writer.dispose();

        return tempFile;
    }

    private static void addTableRow(PdfPTable table, String key, String value, Font keyFont, Font valueFont) {
        PdfPCell keyCell = new PdfPCell(new Phrase(key, keyFont));
        keyCell.setBorder(Rectangle.NO_BORDER);
        keyCell.setPadding(5);
        keyCell.setBackgroundColor(BaseColor.LIGHT_GRAY);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);
        
        table.addCell(keyCell);
        table.addCell(valueCell);
    }

    private static void addTableRowAlternate(PdfPTable table, String key, String value, Font keyFont, Font valueFont, boolean alternate) {
        BaseColor bgColor = alternate ? new BaseColor(245, 245, 245) : BaseColor.WHITE;
        
        PdfPCell keyCell = new PdfPCell(new Phrase(key, keyFont));
        keyCell.setBorder(Rectangle.NO_BORDER);
        keyCell.setPadding(8);
        keyCell.setBackgroundColor(bgColor);
        
        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(8);
        valueCell.setBackgroundColor(bgColor);
        
        table.addCell(keyCell);
        table.addCell(valueCell);
    }

    public static void generateAllArticlesStatsPDF(List<article> articles) throws IOException, DocumentException {
        // Create document
        Document document = new Document(PageSize.A4);
        String fileName = "all_articles_stats_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".pdf";
        String filePath = System.getProperty("user.home") + "/Downloads/" + fileName;
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
        
        // Add metadata
        document.addTitle("Statistiques de tous les articles - ChouFliFilm");
        document.addAuthor("ChouFliFilm");
        document.addCreator("ChouFliFilm Statistics Generator");
        
        document.open();
        
        // Define theme colors to match the app
        BaseColor primaryColor = new BaseColor(26, 115, 232); // #1a73e8 blue
        BaseColor secondaryColor = new BaseColor(46, 58, 89); // #2E3A59 dark blue
        BaseColor accentColor = new BaseColor(245, 166, 35); // #F5A623 orange/gold
        BaseColor successColor = new BaseColor(76, 175, 80); // #4CAF50 green
        BaseColor dangerColor = new BaseColor(244, 67, 54); // #F44336 red
        BaseColor lightGrayBg = new BaseColor(248, 250, 255); // #f8faff light background
        
        // Add header with logo and app name
        try {
            // Create a table for header layout
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1, 2});
            headerTable.setSpacingAfter(20);
            
            // Logo cell
            PdfPCell logoCell = new PdfPCell();
            logoCell.setBorder(Rectangle.NO_BORDER);
            try {
                Image logo = Image.getInstance(PDFGenerator.class.getResource("/images/logo.png"));
                logo.scaleToFit(80, 80);
                logoCell.addElement(logo);
            } catch (Exception e) {
                // Add text instead if logo not found
                Paragraph logoText = new Paragraph("ChouFliFilm", new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, primaryColor));
                logoCell.addElement(logoText);
            }
            
            // App name and title cell
            PdfPCell titleCell = new PdfPCell();
            titleCell.setBorder(Rectangle.NO_BORDER);
            titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            
            Paragraph appName = new Paragraph("ChouFliFilm", new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, primaryColor));
            appName.setAlignment(Element.ALIGN_RIGHT);
            
            Paragraph reportTitle = new Paragraph("Rapport des Articles", new Font(Font.FontFamily.HELVETICA, 16, Font.NORMAL, secondaryColor));
            reportTitle.setAlignment(Element.ALIGN_RIGHT);
            
            titleCell.addElement(appName);
            titleCell.addElement(reportTitle);
            
            headerTable.addCell(logoCell);
            headerTable.addCell(titleCell);
            
            document.add(headerTable);
        } catch (Exception e) {
            // Fallback if header creation fails
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, primaryColor);
            Paragraph title = new Paragraph("ChouFliFilm - Statistiques des Articles", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
        }
        
        // Add decorative line
        PdfPTable lineTable = new PdfPTable(1);
        lineTable.setWidthPercentage(100);
        lineTable.setSpacingAfter(20);
        
        PdfPCell lineCell = new PdfPCell();
        lineCell.setFixedHeight(3f);
        lineCell.setBackgroundColor(primaryColor);
        lineCell.setBorder(Rectangle.NO_BORDER);
        lineTable.addCell(lineCell);
        
        document.add(lineTable);
        
        // Fonts
        Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, primaryColor);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, secondaryColor);
        Font categoryFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
        Font likesFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, successColor);
        Font dislikesFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, dangerColor);
        
        // Summary section
        Paragraph summaryTitle = new Paragraph("Résumé Global", subtitleFont);
        summaryTitle.setSpacingBefore(15);
        summaryTitle.setSpacingAfter(10);
        document.add(summaryTitle);
        
        // Calculate summary statistics
        int totalArticles = articles.size();
        int totalLikes = 0;
        int totalDislikes = 0;
        
        for (article a : articles) {
            try {
                totalLikes += a.getLikes();
                totalDislikes += a.getDislikes();
            } catch (Exception e) {
                // Skip if methods not available
            }
        }
        
        // Create summary table
        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(100);
        summaryTable.setSpacingBefore(10f);
        summaryTable.setSpacingAfter(20f);
        
        // Define relative column widths
        float[] columnWidths = {1f, 3f};
        summaryTable.setWidths(columnWidths);
        
        // Add summary information
        addTableRow(summaryTable, "Nombre total d'articles:", String.valueOf(totalArticles), boldFont, normalFont);
        
        try {
            addTableRow(summaryTable, "Total des J'aime:", String.valueOf(totalLikes), boldFont, likesFont);
            addTableRow(summaryTable, "Total des Je n'aime pas:", String.valueOf(totalDislikes), boldFont, dislikesFont);
        } catch (Exception e) {
            // Skip if methods not available
        }
        
        // Add categories distribution
        Map<String, Integer> categoryCounts = new HashMap<>();
        for (article a : articles) {
            String category = a.getCategorie();
            categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + 1);
        }
        
        document.add(summaryTable);
        
        // Categories section with colored boxes
        Paragraph categoriesTitle = new Paragraph("Distribution par Catégorie", subtitleFont);
        categoriesTitle.setSpacingBefore(10);
        categoriesTitle.setSpacingAfter(10);
        document.add(categoriesTitle);
        
        // Create a table for category distribution
        PdfPTable categoryTable = new PdfPTable(3);
        categoryTable.setWidthPercentage(100);
        categoryTable.setSpacingBefore(10f);
        categoryTable.setSpacingAfter(20f);
        categoryTable.setWidths(new float[]{2f, 1f, 1f});
        
        // Add header
        PdfPCell catHeaderCell = new PdfPCell(new Phrase("Catégorie", boldFont));
        catHeaderCell.setBackgroundColor(primaryColor);
        catHeaderCell.setPadding(8);
        catHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        catHeaderCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        
        PdfPCell countHeaderCell = new PdfPCell(new Phrase("Nombre", boldFont));
        countHeaderCell.setBackgroundColor(primaryColor);
        countHeaderCell.setPadding(8);
        countHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        countHeaderCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        
        PdfPCell percentHeaderCell = new PdfPCell(new Phrase("Pourcentage", boldFont));
        percentHeaderCell.setBackgroundColor(primaryColor);
        percentHeaderCell.setPadding(8);
        percentHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        percentHeaderCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        
        categoryTable.addCell(catHeaderCell);
        categoryTable.addCell(countHeaderCell);
        categoryTable.addCell(percentHeaderCell);
        
        // Add category rows with alternating colors
        int colorIndex = 0;
        BaseColor[] categoryColors = {
            new BaseColor(33, 150, 243), // Blue
            new BaseColor(156, 39, 176), // Purple
            new BaseColor(233, 30, 99),  // Pink
            new BaseColor(0, 150, 136),  // Teal
            new BaseColor(255, 152, 0),  // Orange
            new BaseColor(96, 125, 139)  // Blue Grey
        };
        
        for (Map.Entry<String, Integer> entry : categoryCounts.entrySet()) {
            String category = entry.getKey();
            int count = entry.getValue();
            float percentage = (float) count / totalArticles * 100;
            
            BaseColor catColor = categoryColors[colorIndex % categoryColors.length];
            colorIndex++;
            
            PdfPCell catCell = new PdfPCell(new Phrase(category, categoryFont));
            catCell.setBackgroundColor(catColor);
            catCell.setPadding(8);
            
            PdfPCell countCell = new PdfPCell(new Phrase(String.valueOf(count), normalFont));
            countCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            countCell.setPadding(8);
            
            PdfPCell percentCell = new PdfPCell(new Phrase(String.format("%.1f%%", percentage), normalFont));
            percentCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            percentCell.setPadding(8);
            
            categoryTable.addCell(catCell);
            categoryTable.addCell(countCell);
            categoryTable.addCell(percentCell);
        }
        
        document.add(categoryTable);
        
        // Articles list section
        Paragraph articlesTitle = new Paragraph("Liste des Articles", subtitleFont);
        articlesTitle.setSpacingBefore(10);
        articlesTitle.setSpacingAfter(10);
        document.add(articlesTitle);
        
        // Create articles table
        PdfPTable articlesTable = new PdfPTable(6);
        articlesTable.setWidthPercentage(100);
        articlesTable.setSpacingBefore(10f);
        articlesTable.setSpacingAfter(10f);
        
        // Define column widths for articles table
        articlesTable.setWidths(new float[]{0.5f, 2f, 1f, 1f, 0.7f, 0.7f});
        
        // Add table headers
        PdfPCell headerCell1 = new PdfPCell(new Phrase("ID", boldFont));
        headerCell1.setBackgroundColor(primaryColor);
        headerCell1.setPadding(8);
        headerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        PdfPCell headerCell2 = new PdfPCell(new Phrase("Titre", boldFont));
        headerCell2.setBackgroundColor(primaryColor);
        headerCell2.setPadding(8);
        
        PdfPCell headerCell3 = new PdfPCell(new Phrase("Catégorie", boldFont));
        headerCell3.setBackgroundColor(primaryColor);
        headerCell3.setPadding(8);
        
        PdfPCell headerCell4 = new PdfPCell(new Phrase("Date", boldFont));
        headerCell4.setBackgroundColor(primaryColor);
        headerCell4.setPadding(8);
        headerCell4.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        PdfPCell headerCell5 = new PdfPCell(new Phrase("J'aime", boldFont));
        headerCell5.setBackgroundColor(primaryColor);
        headerCell5.setPadding(8);
        headerCell5.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        PdfPCell headerCell6 = new PdfPCell(new Phrase("Je n'aime pas", boldFont));
        headerCell6.setBackgroundColor(primaryColor);
        headerCell6.setPadding(8);
        headerCell6.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        articlesTable.addCell(headerCell1);
        articlesTable.addCell(headerCell2);
        articlesTable.addCell(headerCell3);
        articlesTable.addCell(headerCell4);
        articlesTable.addCell(headerCell5);
        articlesTable.addCell(headerCell6);
        
        // Add article rows
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        boolean alternate = false;
        
        // Map to store category colors for consistency
        Map<String, BaseColor> categoryColorMap = new HashMap<>();
        colorIndex = 0;
        
        for (article a : articles) {
            BaseColor bgColor = alternate ? lightGrayBg : BaseColor.WHITE;
            
            // Get or assign color for this category
            String category = a.getCategorie();
            if (!categoryColorMap.containsKey(category)) {
                categoryColorMap.put(category, categoryColors[colorIndex % categoryColors.length]);
                colorIndex++;
            }
            BaseColor catColor = categoryColorMap.get(category);
            
            // ID cell
            PdfPCell cell1 = new PdfPCell(new Phrase(String.valueOf(a.getId()), normalFont));
            cell1.setBackgroundColor(bgColor);
            cell1.setPadding(8);
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            
            // Title cell
            PdfPCell cell2 = new PdfPCell(new Phrase(a.getTitre(), normalFont));
            cell2.setBackgroundColor(bgColor);
            cell2.setPadding(8);
            
            // Category cell with color
            PdfPCell cell3 = new PdfPCell(new Phrase(a.getCategorie(), categoryFont));
            cell3.setBackgroundColor(catColor);
            cell3.setPadding(8);
            
            // Date cell
            PdfPCell cell4 = new PdfPCell(new Phrase(dateFormat.format(a.getDate_publication()), normalFont));
            cell4.setBackgroundColor(bgColor);
            cell4.setPadding(8);
            cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
            
            // Likes cell
            PdfPCell cell5 = new PdfPCell();
            cell5.setBackgroundColor(bgColor);
            cell5.setPadding(8);
            cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
            
            // Dislikes cell
            PdfPCell cell6 = new PdfPCell();
            cell6.setBackgroundColor(bgColor);
            cell6.setPadding(8);
            cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
            
            try {
                cell5.setPhrase(new Phrase(String.valueOf(a.getLikes()), likesFont));
                cell6.setPhrase(new Phrase(String.valueOf(a.getDislikes()), dislikesFont));
            } catch (Exception e) {
                cell5.setPhrase(new Phrase("N/A", normalFont));
                cell6.setPhrase(new Phrase("N/A", normalFont));
            }
            
            articlesTable.addCell(cell1);
            articlesTable.addCell(cell2);
            articlesTable.addCell(cell3);
            articlesTable.addCell(cell4);
            articlesTable.addCell(cell5);
            articlesTable.addCell(cell6);
            
            alternate = !alternate;
        }
        
        document.add(articlesTable);
        
        // Add footer with page number and date
        PdfPTable footerTable = new PdfPTable(2);
        footerTable.setWidthPercentage(100);
        footerTable.setSpacingBefore(30);
        
        PdfPCell dateCell = new PdfPCell(new Phrase(
            "Document généré le " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), 
            new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY)
        ));
        dateCell.setBorder(Rectangle.TOP);
        dateCell.setBorderColor(BaseColor.LIGHT_GRAY);
        dateCell.setPaddingTop(10);
        
        PdfPCell appCell = new PdfPCell(new Phrase(
            "ChouFliFilm © " + Calendar.getInstance().get(Calendar.YEAR),
            new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY)
        ));
        appCell.setBorder(Rectangle.TOP);
        appCell.setBorderColor(BaseColor.LIGHT_GRAY);
        appCell.setPaddingTop(10);
        appCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        footerTable.addCell(dateCell);
        footerTable.addCell(appCell);
        
        document.add(footerTable);
        
        // Close the document
        document.close();
        
        // Open the PDF
        try {
            java.awt.Desktop.getDesktop().open(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
