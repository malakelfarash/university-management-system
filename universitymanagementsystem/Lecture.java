package universitymanagementsystem;

public class Lecture extends Course {

    public Lecture(String courseId, String title, String description, int creditHours, String departmentId) {
        super(courseId, title, description, creditHours, departmentId);
        setGradingSystem(new LetterGradingStrategy());
    }

    @Override
    public String getCourseType() {
        return "Lecture";
    }

    @Override
    public void assignGrade(Enrollment enrollment, String grade) {
        getGradingSystem().applyGrade(enrollment, grade);
    }
}
