package universitymanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.io.PrintStream;
import java.util.List;

public class GUI extends JFrame {

    private University university;
    private FileManager fileManager;
    private JTextArea outputArea;
    private JScrollPane outputScrollPane;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private User loggedInUser;
    private PrintStream originalOut;
    private JPanel buttonPanel;
    private JButton loginButton;
    private JButton logoutButton;

    public GUI(University university, FileManager fileManager) {
        this.university = university;
        this.fileManager = fileManager;

        // Initialize output area
        outputArea = new JTextArea(15, 50);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.append("System initialized.\n");

        outputScrollPane = new JScrollPane(outputArea);
        outputScrollPane.setBorder(BorderFactory.createTitledBorder("Output"));

        // Redirect System.out to output area
        originalOut = System.out;
        PrintStream customOut = new PrintStream(System.out) {
            @Override
            public void print(String x) {
                SwingUtilities.invokeLater(() -> updateOutput(x));
            }

            @Override
            public void println(String x) {
                SwingUtilities.invokeLater(() -> updateOutput(x + "\n"));
            }
        };
        System.setOut(customOut);

        // Set look and feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            updateOutput("Failed to set Nimbus Look and Feel: " + e.getMessage() + "\n");
        }

        setTitle("University Management System");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Input panel for login
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Login"));
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton = createStyledButton("Login", new Color(50, 150, 50));
        JLabel roleLabel = new JLabel("Role:");
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Student", "Faculty", "AdminStaff", "SystemAdmin"});

        inputPanel.add(new JLabel("Username:"));
        inputPanel.add(usernameField);
        inputPanel.add(new JLabel("Password:"));
        inputPanel.add(passwordField);
        inputPanel.add(roleLabel);
        inputPanel.add(roleCombo);

        // Button panel for actions
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        logoutButton = createStyledButton("Logout", new Color(200, 50, 50));
        logoutButton.setVisible(false); // Hidden until logged in
        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(logoutButton);
        buttonPanel.add(Box.createVerticalGlue());

        // Add components to main panel
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.WEST);
        mainPanel.add(outputScrollPane, BorderLayout.CENTER);

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // Action listeners
        loginButton.addActionListener(e -> performLogin(usernameField.getText(), new String(passwordField.getPassword()), (String) roleCombo.getSelectedItem()));
        logoutButton.addActionListener(e -> logout());
        exitItem.addActionListener(e -> {
            System.setOut(originalOut);
            System.exit(0);
        });

        add(mainPanel);
        setVisible(true);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 50, 50), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return button;
    }

    private void updateOutput(String message) {
        outputArea.append(message);
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    private void clearOutput() {
        outputArea.setText("");
    }

    private void performLogin(String username, String password, String role) {
        User user = university.getUsers().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);

        if (user == null) {
            updateOutput("User not found.\n");
            return;
        }

        if (user.login(username, password)) {
            fileManager.saveUsers(university.getUsers());
            loggedInUser = user;
            boolean validRole = false;
            if (role.equals("Student") && user instanceof Student) {
                updateOutput("Logged in as Student: " + user.getName() + "\n");
                validRole = true;
                setupStudentButtons();
            } else if (role.equals("Faculty") && user instanceof Faculty) {
                updateOutput("Logged in as Faculty: " + user.getName() + "\n");
                validRole = true;
                setupFacultyButtons();
            } else if (role.equals("AdminStaff") && user instanceof AdminStaff) {
                updateOutput("Logged in as Admin Staff: " + user.getName() + "\n");
                validRole = true;
                setupAdminStaffButtons();
            } else if (role.equals("SystemAdmin") && user instanceof SystemAdmin) {
                updateOutput("Logged in as System Admin: " + user.getName() + "\n");
                validRole = true;
                setupSystemAdminButtons();
            }
            if (!validRole) {
                updateOutput("Invalid user role for selected option.\n");
                loggedInUser = null;
                clearButtons();
            } else {
                loginButton.setVisible(false);
                logoutButton.setVisible(true);
                buttonPanel.revalidate();
                buttonPanel.repaint();
            }
        } else {
            updateOutput("Login failed. Incorrect username or password.\n");
        }
    }

    private void logout() {
        if (loggedInUser != null) {
            String userName = loggedInUser.getName();
            loggedInUser.logout();
            loggedInUser = null;
            clearOutput();
            updateOutput("Logged out: " + userName + "\n");
            clearButtons();
            loginButton.setVisible(true);
            logoutButton.setVisible(false);
            buttonPanel.revalidate();
            buttonPanel.repaint();
        } else {
            updateOutput("No user is currently logged in.\n");
        }
    }

    private void clearButtons() {
        buttonPanel.removeAll();
        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(logoutButton);
        buttonPanel.add(Box.createVerticalGlue());
    }

    private void setupStudentButtons() {
        clearButtons();
        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Register for Course button
        JButton registerButton = createStyledButton("Register for Course", new Color(100, 150, 200));
        registerButton.addActionListener(e -> registerCourse());
        buttonPanel.add(registerButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Drop Course button
        JButton dropButton = createStyledButton("Drop Course", new Color(100, 150, 200));
        dropButton.addActionListener(e -> dropCourse());
        buttonPanel.add(dropButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // View Grades button
        JButton gradesButton = createStyledButton("View Grades", new Color(100, 150, 200));
        gradesButton.addActionListener(e -> viewGrades());
        buttonPanel.add(gradesButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Calculate GPA button
        JButton gpaButton = createStyledButton("Calculate GPA", new Color(100, 150, 200));
        gpaButton.addActionListener(e -> calculateGPA());
        buttonPanel.add(gpaButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Generate Report button
        JButton reportButton = createStyledButton("Generate Report", new Color(100, 150, 200));
        reportButton.addActionListener(e -> generateReport());
        buttonPanel.add(reportButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Update Profile button
        JButton updateProfileButton = createStyledButton("Update Profile", new Color(100, 150, 200));
        updateProfileButton.addActionListener(e -> updateProfile());
        buttonPanel.add(updateProfileButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // View Courses button
        JButton viewCoursesButton = createStyledButton("View Courses", new Color(100, 150, 200));
        viewCoursesButton.addActionListener(e -> viewCourses());
        buttonPanel.add(viewCoursesButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        buttonPanel.add(logoutButton);
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private void setupFacultyButtons() {
        clearButtons();
        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Assign Grades button
        JButton assignGradesButton = createStyledButton("Assign Grades", new Color(100, 150, 200));
        assignGradesButton.addActionListener(e -> assignGrades());
        buttonPanel.add(assignGradesButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Manage Course button
        JButton manageCourseButton = createStyledButton("Manage Course", new Color(100, 150, 200));
        manageCourseButton.addActionListener(e -> manageCourse());
        buttonPanel.add(manageCourseButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // View Student Roster button
        JButton rosterButton = createStyledButton("View Student Roster", new Color(100, 150, 200));
        rosterButton.addActionListener(e -> viewStudentRoster());
        buttonPanel.add(rosterButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Set Office Hours button
        JButton officeHoursButton = createStyledButton("Set Office Hours", new Color(100, 150, 200));
        officeHoursButton.addActionListener(e -> setOfficeHours());
        buttonPanel.add(officeHoursButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Generate Report button
        JButton reportButton = createStyledButton("Generate Report", new Color(100, 150, 200));
        reportButton.addActionListener(e -> generateReport());
        buttonPanel.add(reportButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // View Courses button
        JButton viewCoursesButton = createStyledButton("View Courses", new Color(100, 150, 200));
        viewCoursesButton.addActionListener(e -> viewCourses());
        buttonPanel.add(viewCoursesButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        buttonPanel.add(logoutButton);
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private void setupAdminStaffButtons() {
        clearButtons();
        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Register Student button
        JButton registerStudentButton = createStyledButton("Register Student", new Color(100, 150, 200));
        registerStudentButton.addActionListener(e -> showStudentRegistrationForm());
        buttonPanel.add(registerStudentButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Create Course button
        JButton createCourseButton = createStyledButton("Create Course", new Color(100, 150, 200));
        createCourseButton.addActionListener(e -> showCourseCreationForm());
        buttonPanel.add(createCourseButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Assign Faculty button
        JButton assignFacultyButton = createStyledButton("Assign Faculty", new Color(100, 150, 200));
        assignFacultyButton.addActionListener(e -> assignFaculty());
        buttonPanel.add(assignFacultyButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Generate Report button
        JButton reportButton = createStyledButton("Generate Report", new Color(100, 150, 200));
        reportButton.addActionListener(e -> generateReport());
        buttonPanel.add(reportButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        buttonPanel.add(logoutButton);
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private void setupSystemAdminButtons() {
        clearButtons();
        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Create User button
        JButton createUserButton = createStyledButton("Create User", new Color(100, 150, 200));
        createUserButton.addActionListener(e -> createUser());
        buttonPanel.add(createUserButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Modify Settings button
        JButton modifySettingsButton = createStyledButton("Modify Settings", new Color(100, 150, 200));
        modifySettingsButton.addActionListener(e -> modifySettings());
        buttonPanel.add(modifySettingsButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Backup Data button
        JButton backupDataButton = createStyledButton("Backup Data", new Color(100, 150, 200));
        backupDataButton.addActionListener(e -> backupData());
        buttonPanel.add(backupDataButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Manage Permissions button
        JButton managePermissionsButton = createStyledButton("Manage Permissions", new Color(100, 150, 200));
        managePermissionsButton.addActionListener(e -> managePermissions());
        buttonPanel.add(managePermissionsButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Generate Report button
        JButton reportButton = createStyledButton("Generate Report", new Color(100, 150, 200));
        reportButton.addActionListener(e -> generateReport());
        buttonPanel.add(reportButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // View All Users button
        JButton viewUsersButton = createStyledButton("View All Users", new Color(100, 150, 200));
        viewUsersButton.addActionListener(e -> displayUsers());
        buttonPanel.add(viewUsersButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        buttonPanel.add(logoutButton);
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    // Existing methods (unchanged)
    private void registerCourse() {
        if (!(loggedInUser instanceof Student)) {
            updateOutput("Error: Current user is not a student.\n");
            return;
        }
        JTextField courseIdField = new JTextField(15);
        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(new JLabel("Course ID:"));
        panel.add(courseIdField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Register for Course", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String courseID = courseIdField.getText();
            Course course = university.getCourses().stream()
                    .filter(c -> c.getCourseID().equals(courseID))
                    .findFirst()
                    .orElse(null);
            if (course != null) {
                ((Student) loggedInUser).registerForCourse(course);
                university.saveAllEnrollments();
                updateOutput("Registered for course: " + course.getTitle() + " (" + courseID + ")\n");
            } else {
                updateOutput("Course not found: " + courseID + "\n");
            }
        }
    }

    private void dropCourse() {
        if (!(loggedInUser instanceof Student)) {
            updateOutput("Error: Current user is not a student.\n");
            return;
        }
        JTextField courseIdField = new JTextField(15);
        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(new JLabel("Course ID:"));
        panel.add(courseIdField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Drop Course", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String courseID = courseIdField.getText();
            Course course = university.getCourses().stream()
                    .filter(c -> c.getCourseID().equals(courseID))
                    .findFirst()
                    .orElse(null);
            if (course != null) {
                ((Student) loggedInUser).dropCourse(course);
                university.saveAllEnrollments();
                updateOutput("Dropped course: " + course.getTitle() + " (" + courseID + ")\n");
            } else {
                updateOutput("Course not found: " + courseID + "\n");
            }
        }
    }

    private void viewGrades() {
        if (!(loggedInUser instanceof Student)) {
            updateOutput("Error: Current user is not a student.\n");
            return;
        }
        Student student = (Student) loggedInUser;
        StringBuilder gradesOutput = new StringBuilder("Grades for " + student.getName() + ":\n");
        List<Enrollment> enrollments = student.getEnrolledCourses();
        if (enrollments == null || enrollments.isEmpty()) {
            gradesOutput.append("No enrolled courses.\n");
        } else {
            for (Enrollment enrollment : enrollments) {
                if (enrollment != null && enrollment.getCourse() != null) {
                    Course course = enrollment.getCourse();
                    String grade = enrollment.getGrade() != null ? enrollment.getGrade() : "Not graded";
                    gradesOutput.append("- ").append(course.getTitle())
                            .append(" (").append(course.getCourseID())
                            .append("): ").append(grade).append("\n");
                }
            }
        }
        updateOutput(gradesOutput.toString());
    }

    private void calculateGPA() {
        if (!(loggedInUser instanceof Student)) {
            updateOutput("Error: Current user is not a student.\n");
            return;
        }
        double gpa = ((Student) loggedInUser).calculateGPA();
        updateOutput("GPA for " + loggedInUser.getName() + ": " + String.format("%.2f", gpa) + "\n");
    }

    private void assignGrades() {
        if (!(loggedInUser instanceof Faculty)) {
            updateOutput("Error: Current user is not a faculty member.\n");
            return;
        }
        JTextField studentIdField = new JTextField(15);
        JTextField courseIdField = new JTextField(15);
        JTextField gradeField = new JTextField(15);
        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Student ID:"));
        panel.add(studentIdField);
        panel.add(new JLabel("Course ID:"));
        panel.add(courseIdField);
        panel.add(new JLabel("Grade:"));
        panel.add(gradeField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Assign Grades", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String studentID = studentIdField.getText();
            String courseID = courseIdField.getText();
            String grade = gradeField.getText();
            Course course = university.getCourses().stream()
                    .filter(c -> c.getCourseID().equals(courseID))
                    .findFirst()
                    .orElse(null);
            if (course != null) {
                try {
                    ((Faculty) loggedInUser).assignGrades(studentID, courseID, grade);
                    updateOutput("Grade '" + grade + "' assigned for student " + studentID + " in course " + course.getTitle() + ".\n");
                } catch (IllegalArgumentException e) {
                    updateOutput("Error assigning grade: " + e.getMessage() + "\n");
                }
            } else {
                updateOutput("Course not found: " + courseID + "\n");
            }
        }
    }

    private void viewStudentRoster() {
        if (!(loggedInUser instanceof Faculty)) {
            updateOutput("Error: Current user is not a faculty member.\n");
            return;
        }
        JTextField courseIdField = new JTextField(15);
        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(new JLabel("Course ID:"));
        panel.add(courseIdField);

        int result = JOptionPane.showConfirmDialog(this, panel, "View Student Roster", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String courseID = courseIdField.getText();
            Course course = university.getCourses().stream()
                    .filter(c -> c.getCourseID().equals(courseID))
                    .findFirst()
                    .orElse(null);
            if (course != null && ((Faculty) loggedInUser).getCoursesTeaching().contains(courseID)) {
                List<Student> students = course.getEnrolledStudents();
                StringBuilder rosterOutput = new StringBuilder("Student Roster for " + course.getTitle() + " (" + courseID + "):\n");
                if (students == null || students.isEmpty()) {
                    rosterOutput.append("No students enrolled.\n");
                } else {
                    for (Student student : students) {
                        rosterOutput.append("- ").append(student.getName())
                                .append(" (ID: ").append(student.getStudentID()).append(")\n");
                    }
                }
                updateOutput(rosterOutput.toString());
            } else {
                updateOutput("Course not found or not assigned to you: " + courseID + "\n");
            }
        }
    }

    private void setOfficeHours() {
        if (!(loggedInUser instanceof Faculty)) {
            updateOutput("Error: Current user is not a faculty member.\n");
            return;
        }
        JTextField officeHoursField = new JTextField(15);
        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(new JLabel("Office Hours:"));
        panel.add(officeHoursField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Set Office Hours", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String officeHours = officeHoursField.getText();
            try {
                ((Faculty) loggedInUser).setOfficeHours(officeHours);
                fileManager.saveUsers(university.getUsers());
                updateOutput("Office hours set to: " + officeHours + "\n");
            } catch (IllegalArgumentException e) {
                updateOutput("Error setting office hours: " + e.getMessage() + "\n");
            }
        }
    }

    private void showStudentRegistrationForm() {
        if (!(loggedInUser instanceof AdminStaff)) {
            updateOutput("Error: Current user is not an admin staff member.\n");
            return;
        }
        JPanel panel = new JPanel(new GridLayout(4, 2));
        JTextField studentIdField = new JTextField(15);
        JTextField usernameField = new JTextField(15);
        JTextField nameField = new JTextField(15);
        JTextField admissionDateField = new JTextField("yyyy-mm-dd", 15);

        panel.add(new JLabel("Student ID:"));
        panel.add(studentIdField);
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Admission Date:"));
        panel.add(admissionDateField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Register Student", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Student student = new Student(
                        studentIdField.getText(),
                        usernameField.getText(),
                        "default", // Simplified: use default password
                        nameField.getText(),
                        nameField.getText() + "@example.com",
                        "1234567890",
                        studentIdField.getText(),
                        admissionDateField.getText(),
                        "Active"
                );
                ((AdminStaff) loggedInUser).registerStudent(student);
                university.registerStudent(student);
                university.saveAllStudents();
                updateOutput("Student registered: " + student.getName() + "\n");
            } catch (IllegalArgumentException e) {
                updateOutput("Error creating student: " + e.getMessage() + "\n");
            }
        }
    }

    private void showCourseCreationForm() {
        if (!(loggedInUser instanceof AdminStaff)) {
            updateOutput("Error: Current user is not an admin staff member.\n");
            return;
        }
        JPanel panel = new JPanel(new GridLayout(3, 2));
        JTextField courseIdField = new JTextField(15);
        JTextField titleField = new JTextField(15);
        JTextField deptIdField = new JTextField(15);

        panel.add(new JLabel("Course ID:"));
        panel.add(courseIdField);
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Department ID:"));
        panel.add(deptIdField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Create Course", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Course newCourse = new Lecture(
                        courseIdField.getText(),
                        titleField.getText(),
                        "Description",
                        3,
                        deptIdField.getText()
                );
                newCourse.setGradingSystem(new LetterGradingStrategy());
                ((AdminStaff) loggedInUser).createCourse(newCourse);
                Department dept = university.getDepartments().stream()
                        .filter(d -> d.getDepartmentID().equals(deptIdField.getText()))
                        .findFirst()
                        .orElse(null);
                if (dept != null) {
                    university.offerCourse(newCourse, dept);
                    updateOutput("Course created: " + newCourse.getTitle() + "\n");
                } else {
                    updateOutput("Department not found: " + deptIdField.getText() + "\n");
                }
            } catch (IllegalArgumentException e) {
                updateOutput("Error creating course: " + e.getMessage() + "\n");
            }
        }
    }

    private void assignFaculty() {
        if (!(loggedInUser instanceof AdminStaff)) {
            updateOutput("Error: Current user is not an admin staff member.\n");
            return;
        }
        JTextField facultyIdField = new JTextField(15);
        JTextField courseIdField = new JTextField(15);
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Faculty ID:"));
        panel.add(facultyIdField);
        panel.add(new JLabel("Course ID:"));
        panel.add(courseIdField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Assign Faculty", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Faculty faculty = university.getUsers().stream()
                    .filter(u -> u instanceof Faculty && ((Faculty) u).getFacultyID().equals(facultyIdField.getText()))
                    .map(u -> (Faculty) u)
                    .findFirst()
                    .orElse(null);
            Course course = university.getCourses().stream()
                    .filter(c -> c.getCourseID().equals(courseIdField.getText()))
                    .findFirst()
                    .orElse(null);
            if (faculty != null && course != null) {
                try {
                    ((AdminStaff) loggedInUser).assignFaculty(faculty, course);
                    updateOutput("Faculty assigned to course: " + course.getTitle() + "\n");
                } catch (IllegalArgumentException e) {
                    updateOutput("Error assigning faculty: " + e.getMessage() + "\n");
                }
            } else {
                updateOutput("Faculty or course not found.\n");
            }
        }
    }

    private void createUser() {
        if (!(loggedInUser instanceof SystemAdmin)) {
            updateOutput("Error: Current user is not a system admin.\n");
            return;
        }
        JPanel panel = new JPanel(new GridLayout(4, 2));
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Student", "Faculty", "AdminStaff", "SystemAdmin"});
        JTextField userIdField = new JTextField(15);
        JTextField usernameField = new JTextField(15);
        JTextField nameField = new JTextField(15);

        panel.add(new JLabel("Role:"));
        panel.add(roleCombo);
        panel.add(new JLabel("User ID:"));
        panel.add(userIdField);
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Create User", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String role = (String) roleCombo.getSelectedItem();
            try {
                User user = null;
                switch (role) {
                    case "Student":
                        user = new Student(userIdField.getText(), usernameField.getText(), "default", nameField.getText(), nameField.getText() + "@example.com", "1234567890", userIdField.getText(), "2023-01-01", "Active");
                        break;
                    case "Faculty":
                        user = new Faculty(userIdField.getText(), usernameField.getText(), "default", nameField.getText(), nameField.getText() + "@example.com", "1234567890", userIdField.getText(), "CS", "AI");
                        break;
                    case "AdminStaff":
                        user = new AdminStaff(userIdField.getText(), usernameField.getText(), "default", nameField.getText(), nameField.getText() + "@example.com", "1234567890", userIdField.getText(), "Admin", "Registrar");
                        break;
                    case "SystemAdmin":
                        user = new SystemAdmin(userIdField.getText(), usernameField.getText(), "default", nameField.getText(), nameField.getText() + "@example.com", "1234567890", userIdField.getText(), "High");
                        break;
                }
                if (user != null) {
                    ((SystemAdmin) loggedInUser).createUser(user);
                    university.getUsers().add(user);
                    updateOutput("User created: " + user.getName() + "\n");
                }
            } catch (IllegalArgumentException e) {
                updateOutput("Error creating user: " + e.getMessage() + "\n");
            }
        }
    }

    private void backupData() {
        if (!(loggedInUser instanceof SystemAdmin)) {
            updateOutput("Error: Current user is not a system admin.\n");
            return;
        }
        try {
            ((SystemAdmin) loggedInUser).backupData();
            updateOutput("Data backup completed.\n");
        } catch (Exception e) {
            updateOutput("Error during data backup: " + e.getMessage() + "\n");
        }
    }

    private void displayUsers() {
        StringBuilder usersOutput = new StringBuilder("All users:\n");
        List<User> users = university.getUsers();
        if (users == null || users.isEmpty()) {
            usersOutput.append("No users available.\n");
        } else {
            for (User user : users) {
                String role = user instanceof Student ? "Student" : user instanceof Faculty ? "Faculty" : user instanceof AdminStaff ? "AdminStaff" : "SystemAdmin";
                usersOutput.append("- ").append(user.getName()).append(" (Role: ").append(role).append(")\n");
            }
        }
        updateOutput(usersOutput.toString());
    }

    // New methods for additional buttons
    private void generateReport() {
        StringBuilder reportOutput = new StringBuilder("Generating report for " + loggedInUser.getName() + ":\n");
        if (loggedInUser instanceof Student) {
            reportOutput.append("Student Report:\n");
            List<Enrollment> enrollments = ((Student) loggedInUser).getEnrolledCourses();
            if (enrollments == null || enrollments.isEmpty()) {
                reportOutput.append("No enrolled courses.\n");
            } else {
                for (Enrollment enrollment : enrollments) {
                    if (enrollment != null && enrollment.getCourse() != null) {
                        Course course = enrollment.getCourse();
                        String grade = enrollment.getGrade() != null ? enrollment.getGrade() : "Not graded";
                        reportOutput.append("- ").append(course.getTitle())
                                .append(" (").append(course.getCourseID())
                                .append("): ").append(grade).append("\n");
                    }
                }
                reportOutput.append("GPA: ").append(String.format("%.2f", ((Student) loggedInUser).calculateGPA())).append("\n");
            }
        } else if (loggedInUser instanceof Faculty) {
            reportOutput.append("Faculty Report:\n");
            List<String> coursesTeaching = ((Faculty) loggedInUser).getCoursesTeaching();
            if (coursesTeaching == null || coursesTeaching.isEmpty()) {
                reportOutput.append("No courses assigned.\n");
            } else {
                for (String courseID : coursesTeaching) {
                    Course course = university.getCourses().stream()
                            .filter(c -> c.getCourseID().equals(courseID))
                            .findFirst()
                            .orElse(null);
                    if (course != null) {
                        reportOutput.append("- Teaching: ").append(course.getTitle())
                                .append(" (").append(courseID).append(")\n");
                    }
                }
            }
        } else if (loggedInUser instanceof AdminStaff) {
            reportOutput.append("Admin Staff Report:\n");
            reportOutput.append("Courses created: Not tracked in this version.\n");
        } else if (loggedInUser instanceof SystemAdmin) {
            reportOutput.append("System Admin Report:\n");
            reportOutput.append("Total users: ").append(university.getUsers().size()).append("\n");
        }
        updateOutput(reportOutput.toString());
    }

    private void updateProfile() {
        if (!(loggedInUser instanceof Student)) {
            updateOutput("Error: Current user is not a student.\n");
            return;
        }
        JPanel panel = new JPanel(new GridLayout(3, 2));
        JTextField nameField = new JTextField(loggedInUser.getName(), 15);
        JTextField emailField = new JTextField(loggedInUser.getEmail(), 15);
        JTextField phoneField = new JTextField(loggedInUser.getContactInfo(), 15);

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Update Profile", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                loggedInUser.setName(nameField.getText());
                loggedInUser.setEmail(emailField.getText());
                loggedInUser.setContactInfo(phoneField.getText());
                fileManager.saveUsers(university.getUsers());
                updateOutput("Profile updated for " + loggedInUser.getName() + "\n");
            } catch (IllegalArgumentException e) {
                updateOutput("Error updating profile: " + e.getMessage() + "\n");
            }
        }
    }

    private void viewCourses() {
        StringBuilder coursesOutput = new StringBuilder("Available Courses:\n");
        List<Course> courses = university.getCourses();
        if (courses == null || courses.isEmpty()) {
            coursesOutput.append("No courses available.\n");
        } else {
            if (loggedInUser instanceof Faculty) {
                List<String> coursesTeaching = ((Faculty) loggedInUser).getCoursesTeaching();
                for (Course course : courses) {
                    if (coursesTeaching.contains(course.getCourseID())) {
                        coursesOutput.append("- ").append(course.getTitle())
                                .append(" (").append(course.getCourseID()).append(")\n");
                    }
                }
            } else {
                for (Course course : courses) {
                    coursesOutput.append("- ").append(course.getTitle())
                            .append(" (").append(course.getCourseID()).append(")\n");
                }
            }
        }
        updateOutput(coursesOutput.toString());
    }

    private void manageCourse() {
        if (!(loggedInUser instanceof Faculty)) {
            updateOutput("Error: Current user is not a faculty member.\n");
            return;
        }
        JTextField courseIdField = new JTextField(15);
        JTextField descriptionField = new JTextField(15);
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Course ID:"));
        panel.add(courseIdField);
        panel.add(new JLabel("New Description:"));
        panel.add(descriptionField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Manage Course", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String courseID = courseIdField.getText();
            Course course = university.getCourses().stream()
                    .filter(c -> c.getCourseID().equals(courseID))
                    .findFirst()
                    .orElse(null);
            if (course != null && ((Faculty) loggedInUser).getCoursesTeaching().contains(courseID)) {
                try {
                    course.setDescription(descriptionField.getText());
                    updateOutput("Course " + course.getTitle() + " updated.\n");
                } catch (IllegalArgumentException e) {
                    updateOutput("Error updating course: " + e.getMessage() + "\n");
                }
            } else {
                updateOutput("Course not found or not assigned to you: " + courseID + "\n");
            }
        }
    }

    private void modifySettings() {
        if (!(loggedInUser instanceof SystemAdmin)) {
            updateOutput("Error: Current user is not a system admin.\n");
            return;
        }
        JTextField settingField = new JTextField(15);
        JTextField valueField = new JTextField(15);
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Setting:"));
        panel.add(settingField);
        panel.add(new JLabel("Value:"));
        panel.add(valueField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Modify Settings", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                ((SystemAdmin) loggedInUser).modifySystemSettings(settingField.getText(), valueField.getText());
                updateOutput("Setting " + settingField.getText() + " updated to " + valueField.getText() + ".\n");
            } catch (IllegalArgumentException e) {
                updateOutput("Error modifying settings: " + e.getMessage() + "\n");
            }
        }
    }

    private void managePermissions() {
        if (!(loggedInUser instanceof SystemAdmin)) {
            updateOutput("Error: Current user is not a system admin.\n");
            return;
        }
        JTextField userIdField = new JTextField(15);
        JTextField permissionField = new JTextField(15);
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("User ID:"));
        panel.add(userIdField);
        panel.add(new JLabel("Permission:"));
        panel.add(permissionField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Manage Permissions", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            User user = university.getUsers().stream()
                    .filter(u -> u.getUserID().equals(userIdField.getText()))
                    .findFirst()
                    .orElse(null);
            if (user != null) {
                try {
                    ((SystemAdmin) loggedInUser).managePermissions(userIdField.getText(), permissionField.getText());
                    updateOutput("Permission " + permissionField.getText() + " set for user " + user.getName() + ".\n");
                } catch (IllegalArgumentException e) {
                    updateOutput("Error managing permissions: " + e.getMessage() + "\n");
                }
            } else {
                updateOutput("User not found: " + userIdField.getText() + "\n");
            }
        }
    }
}
