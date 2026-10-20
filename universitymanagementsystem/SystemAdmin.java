package universitymanagementsystem;

import java.util.*;

public class SystemAdmin extends User {

    private String adminID;
    private String securityLevel;
    private FileManager fileManager;

    public SystemAdmin() {
        this.fileManager = new FileManager();
    }

    public SystemAdmin(String userID, String username, String password, String name, String email, String contactInfo,
            String adminID, String securityLevel) {
        super(userID, username, password, name, email, contactInfo);
        this.adminID = adminID;
        this.securityLevel = securityLevel;
        this.fileManager = new FileManager();
        setBaseSalary(5000.0);
    }

    public String getAdminID() {
        return adminID;
    }

    public String getSecurityLevel() {
        return securityLevel;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }

    public void setSecurityLevel(String securityLevel) {
        this.securityLevel = securityLevel;
    }

    public void createUser(User user) {
        List<User> users = fileManager.loadUsers();
        if (users.stream().noneMatch(u -> u.getUserID().equals(user.getUserID()))) {
            users.add(user);
            fileManager.saveUsers(users);
            if (user instanceof Student) {
                List<Student> students = fileManager.loadStudents();
                students.add((Student) user);
                fileManager.saveStudents(students);
            }
            System.out.println("User " + user.getName() + " created successfully.");
        } else {
            System.out.println("User with ID " + user.getUserID() + " already exists.");
        }
    }

    public void modifySystemSettings(String setting, String value) {
        System.out.println("System setting " + setting + " updated to " + value);
    }

    public void backupData() {
        System.out.println("Data backup initiated...");
    }

    public void managePermissions(String userId, String permission) {
        System.out.println("Permission " + permission + " granted to user " + userId);
    }

    @Override
    public boolean login(String username, String password) {
        return super.login(username, password);
    }

    @Override
    public void logout() {
        System.out.println("System Admin " + this.name + " logged out.");
    }

    @Override
    public void updateProfile(String name, String email, String contactInfo) {
        setName(name);
        setEmail(email);
        setContactInfo(contactInfo);
        System.out.println("Profile updated successfully for System Admin " + name + ".");
    }

    @Override
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("System Admin Report for ").append(name).append(" (").append(adminID).append("):\n");
        report.append("Security Level: ").append(securityLevel).append("\n");
        report.append("User Statistics:\n");
        List<User> users = fileManager.loadUsers();
        long students = users.stream().filter(u -> u instanceof Student).count();
        long faculty = users.stream().filter(u -> u instanceof Faculty).count();
        long adminStaff = users.stream().filter(u -> u instanceof AdminStaff).count();
        long sysAdmins = users.stream().filter(u -> u instanceof SystemAdmin).count();
        report.append("  - Students: ").append(students).append("\n");
        report.append("  - Faculty: ").append(faculty).append("\n");
        report.append("  - Admin Staff: ").append(adminStaff).append("\n");
        report.append("  - System Admins: ").append(sysAdmins).append("\n");
        return report.toString();
    }

    @Override
    public double calculatePayroll() {
        return 5000.0;
    }
}
