package DAOs;

import Entities.Bon;
import Utilities.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BonDAO {

    public void addBon(Bon bon) throws SQLException {
        String query = "INSERT INTO BON (NUMERO, DATE_CMDE, DELAI, ID_FOU) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, bon.getNumero());
            stmt.setTimestamp(2, bon.getDateCmde()); // Directly set the Timestamp
            stmt.setInt(3, bon.getDelai());
            stmt.setInt(4, bon.getIdFou());
            stmt.executeUpdate();
        }
    }

    public void updateBon(Bon bon) throws SQLException {
        String query = "UPDATE BON SET NUMERO = ?, DATE_CMDE = ?, DELAI = ?, ID_FOU = ? WHERE ID = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, bon.getNumero());
            stmt.setTimestamp(2, bon.getDateCmde()); // Directly set the Timestamp
            stmt.setInt(3, bon.getDelai());
            stmt.setInt(4, bon.getIdFou());
            stmt.setInt(5, bon.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteBon(int id) throws SQLException {
        String query = "DELETE FROM BON WHERE ID = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Bon getBon(int id) throws SQLException {
        String query = "SELECT * FROM BON WHERE ID = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Bon bon = new Bon();
                    bon.setId(rs.getInt("ID"));
                    bon.setNumero(rs.getInt("NUMERO"));
                    bon.setDateCmde(rs.getTimestamp("DATE_CMDE")); // Retrieve Timestamp directly
                    bon.setDelai(rs.getInt("DELAI"));
                    bon.setIdFou(rs.getInt("ID_FOU"));
                    return bon;
                }
            }
        }
        return null;
    }

    public List<Bon> getAllBons() throws SQLException {
        List<Bon> bons = new ArrayList<>();
        String query = "SELECT * FROM BON";
        try (Connection connection = DatabaseUtil.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Bon bon = new Bon();
                bon.setId(rs.getInt("ID"));
                bon.setNumero(rs.getInt("NUMERO"));
                bon.setDateCmde(rs.getTimestamp("DATE_CMDE")); // Retrieve Timestamp directly
                bon.setDelai(rs.getInt("DELAI"));
                bon.setIdFou(rs.getInt("ID_FOU"));
                bons.add(bon);
            }
        }
        return bons;
    }
}
