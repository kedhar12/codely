package server;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class User {
    public final String id;
    public final String name;
    public final String username;
    public final String email;
    public final String passwordHash;
    public final String salt;
    public int streak;
    public int xp;
    public String token;
    public final String createdAt;

    public User(String id, String name, String username, String email, String passwordHash, String salt, int streak, int xp, String token) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.streak = streak;
        this.xp = xp;
        this.token = token;
        this.createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }
}
