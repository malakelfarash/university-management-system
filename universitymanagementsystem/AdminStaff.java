package universitymanagementsystem;

import java.util.*;

public class AdminStaff extends User {

    private String staffID;
    private String department;
    private String role;
    private FileManager fileManager;

    public AdminStaff() {
        this.fileManager = new FileManager();
    }

    public AdminStaff(String userID, String username, String password, String name, String email, String contactInfo,
            String staffID, String department, String role) {
        super(userID, username, password, name, email, contactInfo);
        this.staffID = staffID;
        this.department = department;
        this.role = role;
        this.fileManager = new FileManager();
        setBaseSalary(3000.0);
    }

    public String getStaffID() {
        return staffID;
    }

    public String getDepartment() {
        return department;
    }

    public String getRole() {
        return role;
    }

    public void setStaffID(String staffID) {
        this.staffID = staffID;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void registerStudent(Student student) {
        List<User> users = fileManager.loadUsers();
        if (users.stream().noneMatch(u -> u.getUserID().equals(student.getUserID()))) {
            users.add(student);
            fileManager.saveUsers(users);
            List<Student> students = fileManager.loadStudents();
            students.add(student);
            fileManager.saveStudents(students);
            System.out.println("Student " + student.getName() + " registered successfully.");
        } else {
            System.out.println("Student with ID " + student.getUserID() + " already exists.");
        }
    }

    public void createCourse(Course course) {
        List<Course> courses = fileManager.loadCourses();
        if (courses.stream().noneMatch(c -> c.getCourseID().equals(course.getCourseID()))) {
            courses.add(course);
            fileManager.saveCourses(courses);
            System.out.println("Course " + course.getCourseID() + " created successfully.");
        } else {
            System.out.println("Course with ID " + course.getCourseID() + " already exists.");
        }
    }

    public void assignFaculty(Faculty faculty, Course course) {
        if (faculty == null || course == null) {
            throw new IllegalArgumentException("Faculty or course cannot be null.");
        }
        if (course.getInstructor() != null) {
            throw new IllegalArgumentException("Course " + course.getCourseID() + " already has an instructor.");
        }
        // Check workload (e.g., max 5 courses)
        if (faculty.getCoursesTeaching().size() >= 5) {
            throw new IllegalArgumentException("Faculty " + faculty.getName() + " has reached maximum course load.");
        }
        // Check schedule conflicts
        String newSchedule = course.getSchedule();
        if (newSchedule != null && !newSchedule.isEmpty()) {
            for (String existingCourseId : faculty.getCoursesTeaching()) {
                Course existingCourse = University.getInstance().getCourses().stream()
                        .filter(c -> c.getCourseID().equals(existingCourseId))
                        .findFirst()
                        .orElse(null);
                if (existingCourse != null) {
                    String existingSchedule = existingCourse.getSchedule();
                    if (existingSchedule != null && !existingSchedule.isEmpty()) {
                        if (schedulesOverlap(existingSchedule, newSchedule)) {
                            throw new IllegalArgumentException("Schedule conflict with existing course " + existingCourseId);
                        }
                    }
                }
            }
        }
        faculty.addCourseTeaching(course.getCourseID());
        course.setInstructor(faculty);
        fileManager.saveUsers(University.getInstance().getUsers());
        fileManager.saveCourses(University.getInstance().getCourses());
        System.out.println("Faculty " + faculty.getName() + " assigned to course " + course.getCourseID());
    }

    private boolean schedulesOverlap(String schedule1, String schedule2) {
        try {
            String[] parts1 = schedule1.split(" ");
            String[] parts2 = schedule2.split(" ");
            String days1 = parts1[0];
            String days2 = parts2[0];
            String[] time1 = parts1[1].split("-");
            String[] time2 = parts2[1].split("-");

            for (char day : days1.toCharArray()) {
                if (days2.indexOf(day) != -1) {
                    int start1 = parseTime(time1[0]);
                    int end1 = parseTime(time1[1]);
                    int start2 = parseTime(time2[0]);
                    int end2 = parseTime(time2[1]);
                    if (start1 <= end2 && start2 <= end1) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            System.out.println("Error parsing schedules: " + schedule1 + " and " + schedule2);
            return false;
        }
    }

    private int parseTime(String time) {
        try {
            String[] parts = time.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59) {
                throw new IllegalArgumentException("Invalid time format");
            }
            return hours * 60 + minutes;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid time format: " + time);
        }
    }

    @Override
    public boolean login(String username, String password) {
        return super.login(username, password);
    }

    @Override
    public void logout() {
        System.out.println("Admin Staff " + this.name + " logged out.");
    }

    @Override
    public void updateProfile(String name, String email, String contactInfo) {
        setName(name);
        setEmail(email);
        setContactInfo(contactInfo);
        System.out.println("Profile updated successfully for Admin Staff " + name + ".");
    }

    @Override
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("Admin Staff Report for ").append(name).append(" (").append(staffID).append("):\n");
        report.append("Department: ").append(department).append("\n");
        report.append("Role: ").append(role).append("\n");
        report.append("Course Enrollment Statistics:\n");
        List<Course> courses = fileManager.loadCourses();
        for (Course course : courses) {
            int capacity = 30;
            report.append("  - Course: ").append(course.getCourseID())
                    .append(", Enrolled: ").append(course.getEnrolledStudents().size())
                    .append("/").append(capacity).append("\n");
        }
        return report.toString();
    }

    @Override
    public double calculatePayroll() {
        return 3000.0;
    }
}