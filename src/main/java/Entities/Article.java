package Entities;

public class Article {
    private int id;
    private String ref;
    private String designation;
    private double prix;
    private int idFou;

    // Default constructor
    public Article() {}

    // Parameterized constructor
    public Article(int id, String ref, String designation, double prix, int idFou) {
        this.id = id;
        this.ref = ref;
        this.designation = designation;
        this.prix = prix;
        this.idFou = idFou;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRef() { return ref; }
    public void setRef(String ref) { this.ref = ref; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    public int getIdFou() { return idFou; }
    public void setIdFou(int idFou) { this.idFou = idFou; }
}
