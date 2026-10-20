package universitymanagementsystem;

public class Enrollment {

    private Student student;
    private Course course;
    private String enrollmentDate;
    private String grade;
    private String status;

    public Enrollment() {
    }

    public Enrollment(Student student, Course course, String status) {
        this.student = student;
        this.course = course;
        this.enrollmentDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
        this.status = status;
    }

    public Student getStudent() {
        return student;
    }

    public Course getCourse() {
        return course;
    }

    public String getEnrollmentDate() {
        return enrollmentDate;
    }

    public String getGrade() {
        return grade;
    }

    public String getStatus() {
        return status;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setEnrollmentDate(String enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void assignGrade(String grade) {
        this.grade = grade;
    }

    public void withdraw() {
        this.status = "Withdrawn";
    }
}
