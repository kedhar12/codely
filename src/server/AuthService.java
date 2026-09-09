package server;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AuthService {

    private static final Map<String, User> usersById = new ConcurrentHashMap<>();
    private static final Map<String, User> usersByEmail = new ConcurrentHashMap<>();
    private static final Map<String, User> usersByUsername = new ConcurrentHashMap<>();
    private static final Map<String, User> sessions = new ConcurrentHashMap<>();
    private static final SecureRandom RANDOM = new SecureRandom();

    static {
        // Pre-seed a demo student account
        register(
            "Demo Learner",
            "demo_learner",
            "demo@codely.dev",
            "codely123"
        );
        // Add some initial stats to the demo user
        User demo = usersByEmail.get("demo@codely.dev");
        if (demo != null) {
            demo.streak = 5;
            demo.xp = 250;
        }
    }

    public static synchronized User register(String name, String username, String email, String password) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required.");
        }
        if (username == null || username.trim().length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters.");
        }
        if (email == null || !email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Please provide a valid email address.");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }

        String lowerUsername = username.trim().toLowerCase();
        String lowerEmail = email.trim().toLowerCase();

        if (usersByUsername.containsKey(lowerUsername)) {
            throw new IllegalArgumentException("Username '" + username + "' is already taken.");
        }
        if (usersByEmail.containsKey(lowerEmail)) {
            throw new IllegalArgumentException("An account with email '" + email + "' already exists.");
        }

        String salt = generateSalt();
        String passwordHash = hashPassword(password, salt);
        String id = UUID.randomUUID().toString().substring(0, 8);
        String token = UUID.randomUUID().toString();

        User user = new User(id, name.trim(), lowerUsername, lowerEmail, passwordHash, salt, 1, 50, token);

        usersById.put(id, user);
        usersByUsername.put(lowerUsername, user);
        usersByEmail.put(lowerEmail, user);
        sessions.put(token, user);

        return user;
    }

    public static synchronized User login(String identifier, String password) {
        if (identifier == null || password == null) return null;
        String idClean = identifier.trim().toLowerCase();

        User user = usersByEmail.get(idClean);
        if (user == null) {
            user = usersByUsername.get(idClean);
        }
        if (user == null) {
            return null;
        }

        String computedHash = hashPassword(password, user.salt);
        if (!computedHash.equals(user.passwordHash)) {
            return null;
        }

        // Generate fresh session token
        String newToken = UUID.randomUUID().toString();
        user.token = newToken;
        sessions.put(newToken, user);

        return user;
    }

    public static User getUserByToken(String token) {
        if (token == null || token.trim().isEmpty()) return null;
        // Clean Bearer prefix if passed
        if (token.startsWith("Bearer ")) {
            token = token.substring("Bearer ".length()).trim();
        }
        return sessions.get(token);
    }

    public static void logout(String token) {
        if (token != null) {
            if (token.startsWith("Bearer ")) {
                token = token.substring("Bearer ".length()).trim();
            }
            sessions.remove(token);
        }
    }

    private static String generateSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return HexFormat.of().formatHex(salt);
    }

    private static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm missing", e);
        }
    }
}
