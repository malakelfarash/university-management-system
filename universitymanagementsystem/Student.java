package universitymanagementsystem;

import java.text.SimpleDateFormat;
import java.util.*;

public class Student extends User {

    private String studentID;
    private String admissionDate;
    private String academicStatus;
    private List<Enrollment> enrolledCourses;

    public Student() {
        this.enrolledCourses = new ArrayList<>();
        this.admissionDate = "2023-09-01";
        this.academicStatus = "Active";
    }

    public Student(String userID, String username, String password, String name, String email, String contactInfo,
            String studentID, String admissionDate, String academicStatus) {
        super(userID, username, password, name, email, contactInfo);
        if (studentID == null || studentID.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be empty");
        }
        this.studentID = studentID;
        this.admissionDate = validateDate(admissionDate) ? admissionDate : "2023-09-01";
        this.academicStatus = academicStatus != null ? academicStatus : "Active";
        this.enrolledCourses = new ArrayList<>();
    }

    public String getStudentID() {
        return studentID;
    }

    public String getAdmissionDate() {
        return admissionDate;
    }

    public String getAcademicStatus() {
        return academicStatus;
    }

    public List<Enrollment> getEnrolledCourses() {
        return enrolledCourses;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public void setAdmissionDate(String admissionDate) {
        this.admissionDate = validateDate(admissionDate) ? admissionDate : "2023-09-01";
        if (!validateDate(admissionDate)) {
            System.out.println("Invalid admission date format. Using default: 2023-09-01");
        }
    }

    private boolean validateDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            return false;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
            sdf.setLenient(false);
            sdf.parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void setAcademicStatus(String academicStatus) {
        this.academicStatus = academicStatus;
    }

    public void registerForCourse(Course course) {
        if (course == null) {
            System.out.println("Error: Course cannot be null.");
            return;
        }
        int currentCredits = enrolledCourses.stream()
                .mapToInt(e -> e.getCourse().getCreditHours())
                .sum();
        if (currentCredits + course.getCreditHours() > 17) {
            System.out.println("Cannot register: Exceeds maximum credit limit of 17");
            return;
        }
        if (isScheduleConflict(course)) {
            System.out.println("Schedule conflict with course: " + course.getCourseID());
            return;
        }
        try {
            if (!course.isPrerequisiteSatisfied(this)) {
                System.out.println("Cannot register for course " + course.getCourseID() + ": Prerequisites not met.");
                return;
            }
            if (course.getAvailableSeats() <= 0) {
                System.out.println("Cannot register for course " + course.getCourseID() + ": No seats available.");
                return;
            }
            if (course.getEnrolledStudents().contains(this)) {
                System.out.println("Course " + course.getCourseID() + " already registered.");
                return;
            }
            course.addStudent(this);
            if (course.getEnrolledStudents().contains(this)) {
                Enrollment enrollment = new Enrollment(this, course, "Active");
                enrolledCourses.add(enrollment);
            } else {
                System.out.println("Failed to register for course " + course.getCourseID() + ": Unable to add student to course.");
            }
        } catch (Exception e) {
            System.out.println("Error registering for course " + course.getCourseID() + ": " + e.getMessage());
        }
    }

    private boolean isScheduleConflict(Course newCourse) {
        String newSchedule = newCourse.getSchedule();
        if (newSchedule == null || newSchedule.isEmpty()) {
            System.out.println("No schedule provided for course " + newCourse.getCourseID());
            return false;
        }

        for (Enrollment enrollment : enrolledCourses) {
            String existingSchedule = enrollment.getCourse().getSchedule();
            if (existingSchedule != null && !existingSchedule.isEmpty()) {
                if (schedulesOverlap(existingSchedule, newSchedule)) {
                    return true;
                }
            }
        }
        return false;
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

    public void dropCourse(Course course) {
        if (course == null) {
            System.out.println("Error: Course cannot be null.");
            return;
        }
        try {
            for (Iterator<Enrollment> iterator = enrolledCourses.iterator(); iterator.hasNext();) {
                Enrollment enrollment = iterator.next();
                if (enrollment.getCourse().getCourseID().equals(course.getCourseID())) {
                    enrollment.withdraw();
                    course.removeStudent(this);
                    if (!course.getEnrolledStudents().contains(this)) {
                        iterator.remove();
                        University.getInstance().saveAllEnrollments();
                        System.out.println("Successfully dropped course: " + course.getCourseID());
                    } else {
                        System.out.println("Failed to drop course: Unable to remove from course roster.");
                        enrollment.setStatus("Active"); // Roll back status change
                    }
                    return;
                }
            }
            System.out.println("Course " + course.getCourseID() + " not found in enrolled courses.");
        } catch (Exception e) {
            System.out.println("Error dropping course " + course.getCourseID() + ": " + e.getMessage());
        }
    }

    public void viewGrades() {
        if (enrolledCourses.isEmpty()) {
            System.out.println("No grades available.");
            return;
        }
        System.out.println("Grades for " + name + ":");
        boolean hasGrades = false;
        for (Enrollment enrollment : enrolledCourses) {
            String grade = enrollment.getGrade();
            if (grade != null) {
                String gradingType = enrollment.getCourse().getGradingSystem() != null
                        ? enrollment.getCourse().getGradingSystem().getGradingType()
                        : "Unknown";
                System.out.println("Course: " + enrollment.getCourse().getCourseID()
                        + ", Grade: " + grade + " (" + gradingType + ")");
                hasGrades = true;
            }
        }
        if (!hasGrades) {
            System.out.println("No grades assigned yet.");
        }
    }

    public double calculateGPA() {
        Map<String, Double> gradePoints = new HashMap<>();
        gradePoints.put("A", 4.0);
        gradePoints.put("A-", 3.7);
        gradePoints.put("B+", 3.3);
        gradePoints.put("B", 3.0);
        gradePoints.put("B-", 2.7);
        gradePoints.put("C+", 2.3);
        gradePoints.put("C", 2.0);
        gradePoints.put("C-", 1.7);
        gradePoints.put("D+", 1.3);
        gradePoints.put("D", 1.0);
        gradePoints.put("D-", 0.7);
        gradePoints.put("F", 0.0);

        double totalPoints = 0.0;
        int totalCredits = 0;
        for (Enrollment enrollment : enrolledCourses) {
            String grade = enrollment.getGrade();
            Course course = enrollment.getCourse();
            if (grade != null && course.getGradingSystem() instanceof LetterGradingStrategy
                    && gradePoints.containsKey(grade)) {
                int credits = course.getCreditHours();
                totalPoints += gradePoints.get(grade) * credits;
                totalCredits += credits;
            } else if (grade != null) {
                System.out.println("Warning: Invalid grading system or grade for course "
                    + course.getCourseID());
            }
        }
        return totalCredits > 0 ? totalPoints / totalCredits : 0.0;
    }

    @Override
    public boolean login(String username, String password) {
        return super.login(username, password);
    }

    @Override
    public void logout() {
        System.out.println("Student " + this.name + " logged out.");
    }

    @Override
    public void updateProfile(String name, String email, String contactInfo) {
        setName(name);
        setEmail(email);
        setContactInfo(contactInfo);
        System.out.println("Profile updated successfully for Student " + name + ".");
    }

    @Override
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("Student Report for ").append(name).append(" (").append(studentID).append("):\n");
        report.append("Academic Status: ").append(academicStatus).append("\n");
        report.append("GPA: ").append(String.format("%.2f", calculateGPA())).append("\n");
        report.append("Enrolled Courses:\n");
        if (enrolledCourses.isEmpty()) {
            report.append("  No courses enrolled.\n");
        } else {
            for (Enrollment enrollment : enrolledCourses) {
                Course course = enrollment.getCourse();
                String grade = enrollment.getGrade() != null ? enrollment.getGrade() : "Not graded";
                String gradingType = course.getGradingSystem() != null
                        ? course.getGradingSystem().getGradingType()
                        : "Unknown";
                report.append("  - ").append(course.getCourseID()).append(" (").append(course.getTitle())
                        .append("): Grade = ").append(grade).append(" (").append(gradingType).append(")\n");
            }
        }
        return report.toString();
    }

    @Override
    public double calculatePayroll() {
        return 0.0;
    }
}
