package DAOs.Fourniseur;

import Entities.Fourniseur;

import java.util.List;

public interface IFourniseurDAO {
    void addFourniseur(String name, String contactPerson, String email, String phoneNumber);

    void updateFourniseur(int id, String name, String contactPerson, String email, String phoneNumber);

    List<Fourniseur> getAllFourniseurs();

    void deleteFournisseur(int id);
}
