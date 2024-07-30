package DAOs;

import Entities.Compo;
import Utilities.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompoDAO {

    public void addCompo(Compo compo) throws SQLException {
        String query = "INSERT INTO COMPO (ID_ART, ID_BON, QTE) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, compo.getIdArt());
            stmt.setInt(2, compo.getIdBon());
            stmt.setInt(3, compo.getQte());
            stmt.executeUpdate();
        }
    }

    public void updateCompo(Compo compo) throws SQLException {
        String query = "UPDATE COMPO SET ID_ART = ?, ID_BON = ?, QTE = ? WHERE ID = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, compo.getIdArt());
            stmt.setInt(2, compo.getIdBon());
            stmt.setInt(3, compo.getQte());
            stmt.setInt(4, compo.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteCompo(int id) throws SQLException {
        String query = "DELETE FROM COMPO WHERE ID = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Compo getCompo(int id) throws SQLException {
        String query = "SELECT * FROM COMPO WHERE ID = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Compo compo = new Compo();
                    compo.setId(rs.getInt("ID"));
                    compo.setIdArt(rs.getInt("ID_ART"));
                    compo.setIdBon(rs.getInt("ID_BON"));
                    compo.setQte(rs.getInt("QTE"));
                    return compo;
                }
            }
        }
        return null;
    }

    public List<Compo> getAllCompos() throws SQLException {
        List<Compo> compos = new ArrayList<>();
        String query = "SELECT * FROM COMPO";
        try (Connection connection = DatabaseUtil.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Compo compo = new Compo();
                compo.setId(rs.getInt("ID"));
                compo.setIdArt(rs.getInt("ID_ART"));
                compo.setIdBon(rs.getInt("ID_BON"));
                compo.setQte(rs.getInt("QTE"));
                compos.add(compo);
            }
        }
        return compos;
    }
}
