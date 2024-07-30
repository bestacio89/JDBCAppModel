package DAOs;

import Entities.Fourniseur;
import Utilities.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FourniseurDAO {

    public void addFourniseur(String name, String contactPerson, String email, String phoneNumber) {
        String sql = "INSERT INTO fourniseurs (name, contact_person, email, phone_number) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, contactPerson);
            stmt.setString(3, email);
            stmt.setString(4, phoneNumber);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateFourniseur(int id, String name, String contactPerson, String email, String phoneNumber) {
        String sql = "UPDATE fourniseurs SET name = ?, contact_person = ?, email = ?, phone_number = ? WHERE id = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, contactPerson);
            stmt.setString(3, email);
            stmt.setString(4, phoneNumber);
            stmt.setInt(5, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Fourniseur> getAllFourniseurs() {
        List<Fourniseur> fourniseurs = new ArrayList<>();
        String sql = "SELECT * FROM fourniseurs";
        try (Connection connection = DatabaseUtil.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Fourniseur fourniseur = new Fourniseur();
                fourniseur.setId(rs.getInt("id"));
                fourniseur.setName(rs.getString("name"));
                fourniseur.setContactPerson(rs.getString("contact_person"));
                fourniseur.setEmail(rs.getString("email"));
                fourniseur.setPhoneNumber(rs.getString("phone_number"));
                fourniseurs.add(fourniseur);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fourniseurs;
    }

    public void deleteFournisseur(int id) {
        String sql = "DELETE FROM fourniseurs WHERE id = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
