package universitymanagementsystem;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public abstract class User {

    protected String userID;
    protected String username;
    protected String password;
    protected String name;
    protected String email;
    protected String contactInfo;
    private double baseSalary;

    public User() {
    }

    public User(String userID, String username, String password, String name, String email, String contactInfo) {
        this.userID = userID;
        this.username = username;
        validatePassword(password);
        this.password = hashPassword(password);
        this.name = name;
        this.email = email;
        this.contactInfo = contactInfo;
        this.baseSalary = 0.0;
    }

    public String getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public double getBaseSalary() {
        return baseSalary;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        validatePassword(password);
        this.password = hashPassword(password);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public void setBaseSalary(double baseSalary) {
        if (baseSalary < 0) {
            throw new IllegalArgumentException("Base salary cannot be negative");
        }
        this.baseSalary = baseSalary;
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
    }

    private String hashPassword(String plainPassword) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            PBEKeySpec spec = new PBEKeySpec(plainPassword.toCharArray(), salt, 65536, 256);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password: " + e.getMessage());
        }
    }

    private boolean checkPassword(String plainPassword) {
        if (plainPassword == null || !password.contains(":")) {
            return false;
        }
        try {
            String[] parts = password.split(":");
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] storedHash = Base64.getDecoder().decode(parts[1]);
            PBEKeySpec spec = new PBEKeySpec(plainPassword.toCharArray(), salt, 65536, 256);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] inputHash = skf.generateSecret(spec).getEncoded();
            return Arrays.equals(storedHash, inputHash);
        } catch (Exception e) {
            System.err.println("Error verifying password: " + e.getMessage());
            return false;
        }
    }

    public void resetPassword(String newPassword) {
        validatePassword(newPassword);
        this.password = hashPassword(newPassword);
        System.out.println("Password reset successful for " + this.name + ".");
    }

    public boolean login(String username, String password) {
        if (this.username.equals(username) && checkPassword(password)) {
            System.out.println("User " + this.name + " logged in successfully");
            return true;
        } else {
            System.out.println("Login failed.");
            return false;
        }
    }

    public abstract void logout();

    public abstract void updateProfile(String name, String email, String contactInfo);

    public abstract String generateReport();

    public abstract double calculatePayroll();
}
