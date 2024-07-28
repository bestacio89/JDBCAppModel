package Entities;

public class User {
    private int id;
    private String name;
    private String email;
    private String discordName;
    private String linkedinUrl;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDiscordName() { return discordName; }
    public void setDiscordName(String discordName) { this.discordName = discordName; }

    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }
}
