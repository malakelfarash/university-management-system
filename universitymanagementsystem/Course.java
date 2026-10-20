package universitymanagementsystem;

import java.util.*;

public abstract class Course {

    private String courseID;
    private String title;
    private String description;
    private int creditHours;
    private String departmentID;
    private List<Course> prerequisites;
    private String schedule;
    private Faculty instructor;
    private List<Student> enrolledStudents;
    private GradingSystem gradingSystem;

    public Course(String courseID, String title, String description, int creditHours, String departmentID) {
        if (courseID == null || courseID.trim().isEmpty()) {
            throw new IllegalArgumentException("Course ID cannot be empty");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (creditHours <= 0) {
            throw new IllegalArgumentException("Credit hours must be positive");
        }
        if (departmentID == null || departmentID.trim().isEmpty()) {
            throw new IllegalArgumentException("Department ID cannot be empty");
        }
        this.courseID = courseID;
        this.title = title;
        this.description = description;
        this.creditHours = creditHours;
        this.departmentID = departmentID;
        this.prerequisites = new ArrayList<>();
        this.enrolledStudents = new ArrayList<>();
    }

    public String getCourseID() {
        return courseID;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getCreditHours() {
        return creditHours;
    }

    public String getDepartmentID() {
        return departmentID;
    }

    public List<Course> getPrerequisites() {
        return prerequisites;
    }

    public String getSchedule() {
        return schedule;
    }

    public Faculty getInstructor() {
        return instructor;
    }

    public List<Student> getEnrolledStudents() {
        return enrolledStudents;
    }

    public GradingSystem getGradingSystem() {
        return gradingSystem;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrerequisites(List<Course> prerequisites) {
        this.prerequisites = prerequisites != null ? prerequisites : new ArrayList<>();
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule != null ? schedule : "";
    }

    public void setInstructor(Faculty instructor) {
        this.instructor = instructor;
    }

    public void setGradingSystem(GradingSystem gradingSystem) {
        this.gradingSystem = gradingSystem;
    }

    public void addStudent(Student student) {
        if (student != null && !enrolledStudents.contains(student)) {
            enrolledStudents.add(student);
        }
    }

    public void removeStudent(Student student) {
        enrolledStudents.remove(student);
    }

    public boolean isPrerequisiteSatisfied(Student student) {
        if (student == null || prerequisites == null || prerequisites.isEmpty()) {
            return true;
        }
        for (Course prereq : prerequisites) {
            boolean satisfied = student.getEnrolledCourses().stream()
                    .anyMatch(e -> e.getCourse().getCourseID().equals(prereq.getCourseID())
                    && e.getGrade() != null
                    && prereq.getGradingSystem() != null
                    && prereq.getGradingSystem().isValidGrade(e.getGrade())
                    && !e.getGrade().equals("F") && !e.getGrade().equals("Fail"));
            if (!satisfied) {
                return false;
            }
        }
        return true;
    }

    public int getAvailableSeats() {
        return 30 - enrolledStudents.size();
    }

    public abstract String getCourseType();

    public abstract void assignGrade(Enrollment enrollment, String grade);
}
