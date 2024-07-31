package DAOs.Article;

import Entities.Article;
import Utilities.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticleDAO implements IArticleDAO {

    @Override
    public void addArticle(Article article) {
        String sql = "INSERT INTO ARTICLE (REF, DESIGNATION, PRIX, ID_FOU) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, article.getRef());
            stmt.setString(2, article.getDesignation());
            stmt.setDouble(3, article.getPrix()); // Use setDouble for prix
            stmt.setInt(4, article.getIdFou());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateArticle(Article article) {
        String sql = "UPDATE ARTICLE SET REF = ?, DESIGNATION = ?, PRIX = ?, ID_FOU = ? WHERE ID = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, article.getRef());
            stmt.setString(2, article.getDesignation());
            stmt.setDouble(3, article.getPrix()); // Use setDouble for prix
            stmt.setInt(4, article.getIdFou());
            stmt.setInt(5, article.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteArticle(int id) {
        String sql = "DELETE FROM ARTICLE WHERE ID = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Article> getAllArticles() {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT * FROM ARTICLE";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("ID");
                String ref = rs.getString("REF");
                String designation = rs.getString("DESIGNATION");
                double prix = rs.getDouble("PRIX"); // Use getDouble for prix
                int idFou = rs.getInt("ID_FOU");

                articles.add(new Article(id, ref, designation, prix, idFou));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return articles;
    }
}
