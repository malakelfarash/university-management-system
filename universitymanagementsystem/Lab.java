package universitymanagementsystem;

public class Lab extends Course {

    public Lab(String courseId, String title, String description, int creditHours, String departmentId) {
        super(courseId, title, description, creditHours, departmentId);
        setGradingSystem(new PassFailGradingStrategy());
    }

    @Override
    public String getCourseType() {
        return "Lab";
    }

    @Override
    public void assignGrade(Enrollment enrollment, String grade) {
        getGradingSystem().applyGrade(enrollment, grade);
    }
}
