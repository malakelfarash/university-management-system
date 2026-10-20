package universitymanagementsystem;

import java.util.*;

public class Faculty extends User {

    private String facultyID;
    private String department;
    private String expertise;
    private List<String> coursesTeaching;
    private String officeHours;
    private FileManager fileManager;

    public Faculty() {
        if (this.facultyID == null || this.facultyID.trim().isEmpty()) {
            throw new IllegalArgumentException("Faculty ID must be set");
        }
        if (this.department == null || this.department.trim().isEmpty()) {
            throw new IllegalArgumentException("Department must be set");
        }
        this.coursesTeaching = new ArrayList<>();
        this.officeHours = "Not Set";
        this.fileManager = new FileManager();
    }

    public Faculty(String userID, String username, String password, String name, String email, String contactInfo,
            String facultyID, String department, String expertise) {
        super(userID, username, password, name, email, contactInfo);
        if (facultyID == null || facultyID.trim().isEmpty()) {
            throw new IllegalArgumentException("Faculty ID cannot be empty");
        }
        if (department == null || department.trim().isEmpty()) {
            throw new IllegalArgumentException("Department cannot be empty");
        }
        this.facultyID = facultyID;
        this.department = department;
        this.expertise = expertise != null ? expertise : "";
        this.coursesTeaching = new ArrayList<>();
        this.officeHours = "Not Set";
        this.fileManager = new FileManager();
    }

    public String getFacultyID() {
        return facultyID;
    }

    public String getDepartment() {
        return department;
    }

    public String getExpertise() {
        return expertise;
    }

    public List<String> getCoursesTeaching() {
        return coursesTeaching;
    }

    public String getOfficeHours() {
        return officeHours;
    }

    public void setFacultyId(String facultyID) {
        if (facultyID == null || facultyID.trim().isEmpty()) {
            throw new IllegalArgumentException("Faculty ID cannot be empty");
        }
        this.facultyID = facultyID;
    }

    public void setDepartment(String department) {
        if (department == null || department.trim().isEmpty()) {
            throw new IllegalArgumentException("Department cannot be empty");
        }
        this.department = department;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise != null ? expertise : "";
    }

    public void setOfficeHours(String officeHours) {
        this.officeHours = officeHours != null ? officeHours : "Not Set";
        System.out.println("Office hours set to: " + this.officeHours);
    }

    public void addCourseTeaching(String courseID) {
        if (courseID == null || courseID.trim().isEmpty()) {
            System.out.println("Error: Course ID cannot be empty.");
            return;
        }
        if (!coursesTeaching.contains(courseID)) {
            coursesTeaching.add(courseID);
            Course course = University.getInstance().getCourses().stream()
                    .filter(c -> c.getCourseID().equals(courseID))
                    .findFirst()
                    .orElse(null);
            if (course != null) {
                course.setInstructor(this);
                fileManager.saveCourses(University.getInstance().getCourses());
                System.out.println("Course " + courseID + " added to teaching list.");
            } else {
                System.out.println("Course " + courseID + " not found.");
                coursesTeaching.remove(courseID); // Rollback if course not found
            }
        } else {
            System.out.println("Course " + courseID + " already assigned.");
        }
    }

    public void assignGrades(String studentID, String courseID, String grade) {
        if (studentID == null || courseID == null || grade == null) {
            System.out.println("Error: Student ID, course ID, or grade cannot be null.");
            return;
        }
        if (!coursesTeaching.contains(courseID)) {
            System.out.println("Cannot assign grade: Faculty not assigned to course " + courseID);
            return;
        }
        Course course = University.getInstance().getCourses().stream()
                .filter(c -> c.getCourseID().equals(courseID))
                .findFirst()
                .orElse(null);
        if (course == null) {
            System.out.println("Course " + courseID + " not found.");
            return;
        }
        Student student = University.getInstance().getUsers().stream()
                .filter(u -> u instanceof Student && ((Student) u).getStudentID().equals(studentID))
                .map(u -> (Student) u)
                .findFirst()
                .orElse(null);
        if (student == null) {
            System.out.println("Student " + studentID + " not found.");
            return;
        }
        Enrollment enrollment = student.getEnrolledCourses().stream()
                .filter(e -> e.getCourse().getCourseID().equals(courseID))
                .findFirst()
                .orElse(null);
        if (enrollment == null) {
            System.out.println("Student " + studentID + " not enrolled in course " + courseID);
            return;
        }
        try {
            course.assignGrade(enrollment, grade);
            University.getInstance().saveAllEnrollments(); // Single save point
        } catch (IllegalArgumentException e) {
            System.out.println("Error assigning grade: " + e.getMessage());
        }
    }

    public void manageCourse(String courseID, String newTitle) {
        if (courseID == null || newTitle == null) {
            System.out.println("Error: Course ID or new title cannot be null.");
            return;
        }
        if (!coursesTeaching.contains(courseID)) {
            System.out.println("You are not assigned to course " + courseID);
            return;
        }
        Course course = University.getInstance().getCourses().stream()
                .filter(c -> c.getCourseID().equals(courseID))
                .findFirst()
                .orElse(null);
        if (course == null) {
            System.out.println("Course " + courseID + " not found.");
            return;
        }
        course.setTitle(newTitle);
        fileManager.saveCourses(University.getInstance().getCourses());
        System.out.println("Course " + courseID + " title updated to: " + newTitle);
    }

    public void viewStudentRoster(String courseID) {
        if (courseID == null) {
            System.out.println("Error: Course ID cannot be null.");
            return;
        }
        if (!coursesTeaching.contains(courseID)) {
            System.out.println("You are not assigned to course " + courseID);
            return;
        }
        Course course = University.getInstance().getCourses().stream()
                .filter(c -> c.getCourseID().equals(courseID))
                .findFirst()
                .orElse(null);
        if (course == null) {
            System.out.println("Course " + courseID + " not found.");
            return;
        }
        List<Student> students = course.getEnrolledStudents();
        if (students.isEmpty()) {
            System.out.println("No students enrolled in course " + courseID);
        } else {
            System.out.println("Student roster for course " + courseID + ":");
            for (Student student : students) {
                System.out.println("- " + student.getName() + " (" + student.getStudentID() + ")");
            }
        }
    }

    @Override
    public boolean login(String username, String password) {
        return super.login(username, password);
    }

    @Override
    public void logout() {
        System.out.println("Faculty " + this.name + " logged out.");
    }

    @Override
    public void updateProfile(String name, String email, String contactInfo) {
        setName(name);
        setEmail(email);
        setContactInfo(contactInfo);
        System.out.println("Profile updated successfully for Faculty " + name + ".");
    }

    @Override
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("Faculty Report for ").append(name).append(" (").append(facultyID).append("):\n");
        report.append("Department: ").append(department).append("\n");
        report.append("Expertise: ").append(expertise).append("\n");
        report.append("Courses Teaching:\n");
        if (coursesTeaching.isEmpty()) {
            report.append("  No courses assigned.\n");
        } else {
            List<Course> courses = University.getInstance().getCourses();
            for (String courseID : coursesTeaching) {
                Course course = courses.stream()
                        .filter(c -> c.getCourseID().equals(courseID))
                        .findFirst()
                        .orElse(null);
                if (course != null) {
                    report.append("  - ").append(courseID).append(" (").append(course.getTitle()).append("): ")
                            .append(course.getEnrolledStudents().size()).append(" students\n");
                }
            }
        }
        return report.toString();
    }

    @Override
    public double calculatePayroll() {
        return coursesTeaching.size() * 5000.0;
    }
}
