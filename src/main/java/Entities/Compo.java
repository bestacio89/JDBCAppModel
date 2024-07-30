package Entities;

public class Compo {
    private int id;
    private int idArt;
    private int idBon;
    private int qte;

    // Default constructor
    public Compo() {}

    // Parameterized constructor
    public Compo(int id, int idArt, int idBon, int qte) {
        this.id = id;
        this.idArt = idArt;
        this.idBon = idBon;
        this.qte = qte;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdArt() { return idArt; }
    public void setIdArt(int idArt) { this.idArt = idArt; }

    public int getIdBon() { return idBon; }
    public void setIdBon(int idBon) { this.idBon = idBon; }

    public int getQte() { return qte; }
    public void setQte(int qte) { this.qte = qte; }
}
