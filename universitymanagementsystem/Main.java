package universitymanagementsystem;

import javax.swing.SwingUtilities;
import java.util.*;

public class Main {

    private static University university = University.getInstance();
    private static FileManager fileManager = new FileManager();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        fileManager.loadAllData(university);
        initializeData();

        System.out.println("Choose interface:");
        System.out.println("1. Console");
        System.out.println("2. GUI");
        System.out.print("Enter choice (1 or 2): ");
        int choice;
        try {
            choice = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Using console interface.");
            scanner.nextLine();
            choice = 1;
        }

        if (choice == 2) {
            SwingUtilities.invokeLater(() -> new GUI(university, fileManager));
        } else {
            showMainMenu();
        }
    }

    private static void initializeData() {
        if (university.getDepartments().stream().noneMatch(d -> d.getDepartmentID().equals("CS"))) {
            Department csDept = new Department("CS", "Computer Science", "Department of Computer Science");
            university.createDepartment(csDept);
        }
        if (university.getDepartments().stream().noneMatch(d -> d.getDepartmentID().equals("MATH"))) {
            Department mathDept = new Department("MATH", "Mathematics", "Department of Mathematics");
            university.createDepartment(mathDept);
        }

        Department csDept = university.getDepartments().stream()
                .filter(d -> d.getDepartmentID().equals("CS"))
                .findFirst()
                .orElse(null);
        Department mathDept = university.getDepartments().stream()
                .filter(d -> d.getDepartmentID().equals("MATH"))
                .findFirst()
                .orElse(null);

        if (csDept != null && university.getCourses().stream().noneMatch(c -> c.getCourseID().equals("CS101"))) {
            Course cs101 = new Lecture("CS101", "Introduction to Programming", "Intro to Java", 3, "CS");
            cs101.setSchedule("MW 10:00-11:30");
            cs101.setGradingSystem(new LetterGradingStrategy());
            university.offerCourse(cs101, csDept);
        }
        if (csDept != null && university.getCourses().stream().noneMatch(c -> c.getCourseID().equals("CS102"))) {
            Course cs102 = new Lab("CS102", "Programming Lab", "Java Lab", 1, "CS");
            cs102.setSchedule("F 14:00-16:00");
            cs102.setGradingSystem(new PassFailGradingStrategy());
            university.offerCourse(cs102, csDept);
        }
        if (mathDept != null && university.getCourses().stream().noneMatch(c -> c.getCourseID().equals("MATH201"))) {
            Course math201 = new Lecture("MATH201", "Calculus I", "Differential Calculus", 4, "MATH");
            math201.setSchedule("TR 13:00-14:30");
            math201.setGradingSystem(new LetterGradingStrategy());
            Course cs101 = university.getCourses().stream()
                    .filter(c -> c.getCourseID().equals("CS101"))
                    .findFirst()
                    .orElse(null);
            if (cs101 != null) {
                math201.setPrerequisites(Collections.singletonList(cs101));
            }
            university.offerCourse(math201, mathDept);
        }

        if (csDept != null && university.getUsers().stream().noneMatch(u -> u.getUserID().equals("prof_smith"))) {
            Faculty profSmith = new Faculty("prof_smith", "prof_smith", "faculty789", "Prof Smith",
                    "smith@uni.edu", "5555555555", "prof_smith", "CS", "AI");
            profSmith.setOfficeHours("MW 10-12");
            university.hireFaculty(profSmith, csDept);
            Course cs101 = university.getCourses().stream()
                    .filter(c -> c.getCourseID().equals("CS101"))
                    .findFirst()
                    .orElse(null);
            if (cs101 != null) {
                profSmith.addCourseTeaching("CS101");
                cs101.setInstructor(profSmith);
                fileManager.saveCourses(university.getCourses());
            }
        }

        if (university.getUsers().stream().noneMatch(u -> u.getUserID().equals("john_doe"))) {
            Student john = new Student("john_doe", "john_doe", "student123", "John Doe",
                    "john@uni.edu", "1234567890", "john_doe", "2023-09-01", "Active");
            university.registerStudent(john);
        }
        if (university.getUsers().stream().noneMatch(u -> u.getUserID().equals("jane_smith"))) {
            Student jane = new Student("jane_smith", "jane_smith", "student456", "Jane Smith",
                    "jane@uni.edu", "0987654321", "jane_smith", "2023-09-01", "Active");
            university.registerStudent(jane);
        }

        university.saveAllStudents();

        if (university.getUsers().stream().noneMatch(u -> u.getUserID().equals("admin1"))) {
            AdminStaff admin = new AdminStaff("admin1", "admin1", "admin123", "Admin One",
                    "admin@uni.edu", "1112223333", "admin1", "Registrar", "Registrar");
            university.getUsers().add(admin);
            fileManager.saveUsers(university.getUsers());
        }

        if (university.getUsers().stream().noneMatch(u -> u.getUserID().equals("sysadmin1"))) {
            SystemAdmin sysAdmin = new SystemAdmin("sysadmin1", "sysadmin1", "sysadmin123", "Sys Admin",
                    "sysadmin@uni.edu", "4445556666", "sysadmin1", "High");
            university.getUsers().add(sysAdmin);
            fileManager.saveUsers(university.getUsers());
        }

        Student john = university.getUsers().stream()
                .filter(u -> u instanceof Student && u.getUserID().equals("john_doe"))
                .map(u -> (Student) u)
                .findFirst()
                .orElse(null);
        Course cs101 = university.getCourses().stream()
                .filter(c -> c.getCourseID().equals("CS101"))
                .findFirst()
                .orElse(null);
        if (john != null && cs101 != null && john.getEnrolledCourses().stream()
                .noneMatch(e -> e.getCourse().getCourseID().equals("CS101"))) {
            System.out.println("Enrolling john_doe in CS101, title: " + cs101.getTitle());
            john.registerForCourse(cs101);
        }

        university.saveAllEnrollments();
    }

    private static void showMainMenu() {
        while (true) {
            System.out.println("\n=== University Management System ===");
            System.out.println("1. Login as Student");
            System.out.println("2. Login as Faculty");
            System.out.println("3. Login as Admin Staff");
            System.out.println("4. Login as System Admin");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int choice;
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
                continue;
            }

            if (choice == 5) {
                break;
            }

            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            User user = university.getUsers().stream()
                    .filter(u -> u.getUsername().equals(username))
                    .findFirst()
                    .orElse(null);

            if (user == null) {
                System.out.println("User not found.");
                continue;
            }

            if (user.login(username, password)) {
                fileManager.saveUsers(university.getUsers());
                if (choice == 1 && user instanceof Student) {
                    showStudentMenu((Student) user);
                } else if (choice == 2 && user instanceof Faculty) {
                    showFacultyMenu((Faculty) user);
                } else if (choice == 3 && user instanceof AdminStaff) {
                    showAdminStaffMenu((AdminStaff) user);
                } else if (choice == 4 && user instanceof SystemAdmin) {
                    showSystemAdminMenu((SystemAdmin) user);
                } else {
                    System.out.println("Invalid user role for selected option.");
                }
            }
        }
    }

    private static void showStudentMenu(Student student) {
        while (true) {
            System.out.println("\n=== Student Menu ===");
            System.out.println("1. Register for Course");
            System.out.println("2. Drop Course");
            System.out.println("3. View Grades");
            System.out.println("4. Calculate GPA");
            System.out.println("5. Generate Report");
            System.out.println("6. Calculate Payroll");
            System.out.println("7. Update Profile");
            System.out.println("8. Logout");
            System.out.print("Choose an option: ");
            int choice;
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
                continue;
            }

            if (choice == 8) {
                student.logout();
                break;
            }

            switch (choice) {
                case 1:
                    System.out.print("Enter course ID: ");
                    String courseID = scanner.nextLine();
                    Course course = university.getCourses().stream()
                            .filter(c -> c.getCourseID().equals(courseID))
                            .findFirst()
                            .orElse(null);
                    if (course != null) {
                        student.registerForCourse(course);
                        university.saveAllEnrollments();
                    } else {
                        System.out.println("Course not found.");
                    }
                    break;
                case 2:
                    System.out.print("Enter course ID: ");
                    courseID = scanner.nextLine();
                    course = university.getCourses().stream()
                            .filter(c -> c.getCourseID().equals(courseID))
                            .findFirst()
                            .orElse(null);
                    if (course != null) {
                        student.dropCourse(course);
                        university.saveAllEnrollments();
                    } else {
                        System.out.println("Course not found.");
                    }
                    break;
                case 3:
                    student.viewGrades();
                    break;
                case 4:
                    System.out.println("GPA: " + student.calculateGPA());
                    break;
                case 5:
                    System.out.println(student.generateReport());
                    break;
                case 6:
                    System.out.println("Payroll: $" + student.calculatePayroll());
                    break;
                case 7:
                    System.out.print("Enter new name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter new email: ");
                    String email = scanner.nextLine();
                    System.out.print("Enter new contact info: ");
                    String contactInfo = scanner.nextLine();
                    System.out.print("Enter new password (or press Enter to keep current): ");
                    String newPassword = scanner.nextLine();
                    try {
                        student.updateProfile(name, email, contactInfo);
                        if (!newPassword.isEmpty()) {
                            student.setPassword(newPassword);
                        }
                        fileManager.saveUsers(university.getUsers());
                        university.saveAllStudents();
                        System.out.println("Profile updated successfully.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error updating profile: " + e.getMessage());
                    }
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void showFacultyMenu(Faculty faculty) {
        while (true) {
            System.out.println("\n=== Faculty Menu ===");
            System.out.println("1. Assign Grades");
            System.out.println("2. Manage Course");
            System.out.println("3. View Student Roster");
            System.out.println("4. Set Office Hours");
            System.out.println("5. Generate Report");
            System.out.println("6. Calculate Payroll");
            System.out.println("7. Update Profile");
            System.out.println("8. Logout");
            System.out.print("Choose an option: ");
            int choice;
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
                continue;
            }

            if (choice == 8) {
                faculty.logout();
                break;
            }

            switch (choice) {
                case 1:
                    System.out.print("Enter student ID: ");
                    String studentID = scanner.nextLine();
                    System.out.print("Enter course ID: ");
                    String courseID = scanner.nextLine();
                    Course course = university.getCourses().stream()
                            .filter(c -> c.getCourseID().equals(courseID))
                            .findFirst()
                            .orElse(null);
                    if (course != null) {
                        String gradingType = course.getGradingSystem() != null
                                ? course.getGradingSystem().getGradingType()
                                : "Unknown";
                        System.out.println("Course uses " + gradingType + " grading.");
                        if ("Letter".equals(gradingType)) {
                            System.out.print("Enter grade (A, A-, B+, B, B-, C+, C, C-, D+, D, D-, F): ");
                        } else if ("PassFail".equals(gradingType)) {
                            System.out.print("Enter grade (Pass, Fail): ");
                        } else {
                            System.out.println("Unknown grading type.");
                            break;
                        }
                        String grade = scanner.nextLine();
                        faculty.assignGrades(studentID, courseID, grade);
                    } else {
                        System.out.println("Course not found.");
                    }
                    break;
                case 2:
                    System.out.print("Enter course ID: ");
                    courseID = scanner.nextLine();
                    System.out.print("Enter new title: ");
                    String newTitle = scanner.nextLine();
                    faculty.manageCourse(courseID, newTitle);
                    break;
                case 3:
                    System.out.print("Enter course ID: ");
                    courseID = scanner.nextLine();
                    faculty.viewStudentRoster(courseID);
                    break;
                case 4:
                    System.out.print("Enter office hours: ");
                    String officeHours = scanner.nextLine();
                    faculty.setOfficeHours(officeHours);
                    fileManager.saveUsers(university.getUsers());
                    break;
                case 5:
                    System.out.println(faculty.generateReport());
                    break;
                case 6:
                    System.out.println("Payroll: $" + faculty.calculatePayroll());
                    break;
                case 7:
                    System.out.print("Enter new name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter new email: ");
                    String email = scanner.nextLine();
                    System.out.print("Enter new contact info: ");
                    String contactInfo = scanner.nextLine();
                    System.out.print("Enter new password (or press Enter to keep current): ");
                    String newPassword = scanner.nextLine();
                    try {
                        faculty.updateProfile(name, email, contactInfo);
                        if (!newPassword.isEmpty()) {
                            faculty.setPassword(newPassword);
                        }
                        fileManager.saveUsers(university.getUsers());
                        System.out.println("Profile updated successfully.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error updating profile: " + e.getMessage());
                    }
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void showAdminStaffMenu(AdminStaff admin) {
        while (true) {
            System.out.println("\n=== Admin Staff Menu ===");
            System.out.println("1. Register Student");
            System.out.println("2. Create Course");
            System.out.println("3. Assign Faculty");
            System.out.println("4. Generate Report");
            System.out.println("5. Calculate Payroll");
            System.out.println("6. Update Profile");
            System.out.println("7. Logout");
            System.out.print("Choose an option: ");
            int choice;
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
                continue;
            }

            if (choice == 7) {
                admin.logout();
                break;
            }

            Course course = null;
            switch (choice) {
                case 1:
                    System.out.print("Enter student ID: ");
                    String studentID = scanner.nextLine();
                    System.out.print("Enter username: ");
                    String username = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();
                    System.out.print("Enter name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter email: ");
                    String email = scanner.nextLine();
                    System.out.print("Enter contact info: ");
                    String contactInfo = scanner.nextLine();
                    System.out.print("Enter admission date (yyyy-mm-dd): ");
                    String admissionDate = scanner.nextLine();
                    try {
                        Student student = new Student(studentID, username, password, name, email, contactInfo,
                                studentID, admissionDate, "Active");
                        admin.registerStudent(student);
                        university.registerStudent(student);
                        university.saveAllStudents();
                        university.saveAllEnrollments();
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error creating student: " + e.getMessage());
                    }
                    break;
                case 2:
                    System.out.print("Enter course ID: ");
                    String courseID = scanner.nextLine();
                    System.out.print("Enter title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter description: ");
                    String description = scanner.nextLine();
                    System.out.print("Enter credit hours: ");
                    int creditHours;
                    try {
                        creditHours = scanner.nextInt();
                        scanner.nextLine();
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid credit hours. Please enter a number.");
                        scanner.nextLine();
                        continue;
                    }
                    System.out.print("Enter department ID: ");
                    String deptId = scanner.nextLine();
                    System.out.print("Enter course type (Lecture/Lab): ");
                    String courseType = scanner.nextLine();
                    System.out.print("Enter grading type (Letter/PassFail): ");
                    String gradingType = scanner.nextLine();
                    try {
                        Course newCourse;
                        GradingSystem gradingSystem;
                        if ("Letter".equalsIgnoreCase(gradingType)) {
                            gradingSystem = new LetterGradingStrategy();
                        } else if ("PassFail".equalsIgnoreCase(gradingType)) {
                            gradingSystem = new PassFailGradingStrategy();
                        } else {
                            System.out.println("Invalid grading type. Use Letter or PassFail.");
                            continue;
                        }
                        if ("Lecture".equalsIgnoreCase(courseType)) {
                            newCourse = new Lecture(courseID, title, description, creditHours, deptId);
                        } else if ("Lab".equalsIgnoreCase(courseType)) {
                            newCourse = new Lab(courseID, title, description, creditHours, deptId);
                        } else {
                            System.out.println("Invalid course type. Use Lecture or Lab.");
                            continue;
                        }
                        newCourse.setGradingSystem(gradingSystem);
                        admin.createCourse(newCourse);
                        Department dept = university.getDepartments().stream()
                                .filter(d -> d.getDepartmentID().equals(deptId))
                                .findFirst()
                                .orElse(null);
                        if (dept != null) {
                            university.offerCourse(newCourse, dept);
                        } else {
                            System.out.println("Department not found.");
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error creating course: " + e.getMessage());
                    }
                    break;
                case 3:
                    System.out.print("Enter faculty ID: ");
                    String facultyID = scanner.nextLine();
                    System.out.print("Enter course ID: ");
                    courseID = scanner.nextLine();
                    Faculty faculty = university.getUsers().stream()
                            .filter(u -> u instanceof Faculty && ((Faculty) u).getFacultyID().equals(facultyID))
                            .map(u -> (Faculty) u)
                            .findFirst()
                            .orElse(null);
                    course = university.getCourses().stream()
                            .filter(c -> c.getCourseID().equals(courseID))
                            .findFirst()
                            .orElse(null);
                    if (faculty != null && course != null) {
                        try {
                            admin.assignFaculty(faculty, course);
                            System.out.println("Faculty " + faculty.getName() + " assigned to course " + courseID);
                        } catch (IllegalArgumentException e) {
                            System.out.println("Error assigning faculty: " + e.getMessage());
                        }
                    } else {
                        System.out.println("Faculty or course not found.");
                    }
                    break;
                case 4:
                    System.out.println(admin.generateReport());
                    break;
                case 5:
                    System.out.println("Payroll: $" + admin.calculatePayroll());
                    break;
                case 6:
                    System.out.print("Enter new name: ");
                    name = scanner.nextLine();
                    System.out.print("Enter new email: ");
                    email = scanner.nextLine();
                    System.out.print("Enter new contact info: ");
                    contactInfo = scanner.nextLine();
                    System.out.print("Enter new password (or press Enter to keep current): ");
                    String newPassword = scanner.nextLine();
                    try {
                        admin.updateProfile(name, email, contactInfo);
                        if (!newPassword.isEmpty()) {
                            admin.setPassword(newPassword);
                        }
                        fileManager.saveUsers(university.getUsers());
                        System.out.println("Profile updated successfully.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error updating profile: " + e.getMessage());
                    }
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void showSystemAdminMenu(SystemAdmin sysAdmin) {
        while (true) {
            System.out.println("\n=== System Admin Menu ===");
            System.out.println("1. Create User");
            System.out.println("2. Modify System Settings");
            System.out.println("3. Backup Data");
            System.out.println("4. Manage Permissions");
            System.out.println("5. Generate Report");
            System.out.println("6. Calculate Payroll");
            System.out.println("7. Update Profile");
            System.out.println("8. Logout");
            System.out.print("Choose an option: ");
            int choice;
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
                continue;
            }

            if (choice == 8) {
                sysAdmin.logout();
                break;
            }

            switch (choice) {
                case 1:
                    System.out.print("Enter user role (Student/Faculty/AdminStaff/SystemAdmin): ");
                    String role = scanner.nextLine();
                    System.out.print("Enter user ID: ");
                    String userID = scanner.nextLine();
                    System.out.print("Enter username: ");
                    String username = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();
                    System.out.print("Enter name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter email: ");
                    String email = scanner.nextLine();
                    System.out.print("Enter contact info: ");
                    String contactInfo = scanner.nextLine();
                    User user = null;
                    String department = null;
                    try {
                        switch (role) {
                            case "Student":
                                System.out.print("Enter student ID: ");
                                String studentId = scanner.nextLine();
                                System.out.print("Enter admission date (yyyy-mm-dd): ");
                                String admissionDate = scanner.nextLine();
                                user = new Student(userID, username, password, name, email, contactInfo,
                                        studentId, admissionDate, "Active");
                                break;
                            case "Faculty":
                                System.out.print("Enter faculty ID: ");
                                String facultyID = scanner.nextLine();
                                System.out.print("Enter department: ");
                                department = scanner.nextLine();
                                System.out.print("Enter expertise: ");
                                String expertise = scanner.nextLine();
                                user = new Faculty(userID, username, password, name, email, contactInfo,
                                        facultyID, department, expertise);
                                break;
                            case "AdminStaff":
                                System.out.print("Enter staff ID: ");
                                String staffID = scanner.nextLine();
                                System.out.print("Enter department: ");
                                department = scanner.nextLine();
                                System.out.print("Enter role: ");
                                String adminRole = scanner.nextLine();
                                user = new AdminStaff(userID, username, password, name, email, contactInfo,
                                        staffID, department, adminRole);
                                break;
                            case "SystemAdmin":
                                System.out.print("Enter admin ID: ");
                                String adminID = scanner.nextLine();
                                System.out.print("Enter security level: ");
                                String securityLevel = scanner.nextLine();
                                user = new SystemAdmin(userID, username, password, name, email, contactInfo,
                                        adminID, securityLevel);
                                break;
                        }
                        if (user != null) {
                            sysAdmin.createUser(user);
                            university.getUsers().add(user);
                            if (user instanceof Student) {
                                university.saveAllStudents();
                                university.saveAllEnrollments();
                            }
                        } else {
                            System.out.println("Invalid role.");
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error creating user: " + e.getMessage());
                    }
                    break;
                case 2:
                    System.out.print("Enter setting name: ");
                    String setting = scanner.nextLine();
                    System.out.print("Enter value: ");
                    String value = scanner.nextLine();
                    sysAdmin.modifySystemSettings(setting, value);
                    break;
                case 3:
                    sysAdmin.backupData();
                    break;
                case 4:
                    System.out.print("Enter user ID: ");
                    userID = scanner.nextLine();
                    System.out.print("Enter permission: ");
                    String permission = scanner.nextLine();
                    sysAdmin.managePermissions(userID, permission);
                    break;
                case 5:
                    System.out.println(sysAdmin.generateReport());
                    break;
                case 6:
                    System.out.println("Payroll: $" + sysAdmin.calculatePayroll());
                    break;
                case 7:
                    System.out.print("Enter new name: ");
                    name = scanner.nextLine();
                    System.out.print("Enter new email: ");
                    email = scanner.nextLine();
                    System.out.print("Enter new contact info: ");
                    contactInfo = scanner.nextLine();
                    System.out.print("Enter new password (or press Enter to keep current): ");
                    String newPassword = scanner.nextLine();
                    try {
                        sysAdmin.updateProfile(name, email, contactInfo);
                        if (!newPassword.isEmpty()) {
                            sysAdmin.setPassword(newPassword);
                        }
                        fileManager.saveUsers(university.getUsers());
                        System.out.println("Profile updated successfully.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error updating profile: " + e.getMessage());
                    }
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}
