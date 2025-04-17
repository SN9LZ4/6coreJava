package tn.esprit.test;

import tn.esprit.entities.article;
import tn.esprit.entities.commentaire;
import tn.esprit.entities.User;
import tn.esprit.services.ArticleService;
import tn.esprit.services.commentaireService;

import java.util.Date;

public class Main {

    public static void main(String[] args) {
        // Create ArticleService and commentaireService instances
        ArticleService articleService = new ArticleService();
        commentaireService commentaireService = new commentaireService();

        try {
            // Create an article
            article newArticle = new article();
            newArticle.setTitre("Sample Article Title");
            newArticle.setDate_publication(new Date());
            newArticle.setContenu("This is the content of the article.");
            newArticle.setImage("sample_image.jpg");
            newArticle.setCategorie("Technology");
            newArticle.setTotal_rating(4.5);
            newArticle.setRating_count(100);
            newArticle.setLikes(50);
            newArticle.setDislikes(10);

            // Add the article to the database
            articleService.add(newArticle);

            // Edit the article
            newArticle.setTitre("Updated Article Title");
            articleService.edit(newArticle);

            // Create a user for the comment
            User user = new User();
            user.setUser_id(1); // Assuming this user ID exists in your system
            user.setAdmin(true); // Assuming this user is an admin

            // Create a comment
            commentaire newComment = new commentaire();
            newComment.setContenuCom("This is a sample comment.");
            newComment.setDate(new Date());
            newComment.setArticle(newArticle);
            newComment.setUser(user);

            // Add the comment to the article
            commentaireService.add(newComment);

            // Edit the comment
            newComment.setContenuCom("This is an updated comment.");
            commentaireService.edit(newComment, user);

            // Remove the comment
            //commentaireService.remove(newComment.getId(), user);

            // List comments for an article (if any comments exist for the article)
          //  for (commentaire comment : commentaireService.listCommentsByArticle(newArticle.getId())) {
//System.out.println("Comment: " + comment.getContenuCom());
           // }

            // Remove the article
            articleService.remove(newArticle);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
