package DAOs.Article;

import Entities.Article;

import java.util.List;

public interface IArticleDAO {
    void addArticle(Article article);

    void updateArticle(Article article);

    void deleteArticle(int id);

    List<Article> getAllArticles();
}
