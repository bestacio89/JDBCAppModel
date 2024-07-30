package Entities;

import java.sql.Timestamp;

public class Bon {
    private int id;
    private int numero;
    private Timestamp dateCmde;
    private int delai;
    private int idFou;

    // Default constructor
    public Bon() {}

    // Parameterized constructor
    public Bon(int id, int numero, Timestamp dateCmde, int delai, int idFou) {
        this.id = id;
        this.numero = numero;
        this.dateCmde = dateCmde;
        this.delai = delai;
        this.idFou = idFou;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public Timestamp getDateCmde() { return dateCmde; }
    public void setDateCmde(Timestamp dateCmde) { this.dateCmde = dateCmde; }

    public int getDelai() { return delai; }
    public void setDelai(int delai) { this.delai = delai; }

    public int getIdFou() { return idFou; }
    public void setIdFou(int idFou) { this.idFou = idFou; }
}
