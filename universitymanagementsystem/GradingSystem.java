package universitymanagementsystem;

public interface GradingSystem {

    boolean isValidGrade(String grade);

    void applyGrade(Enrollment enrollment, String grade);

    String getGradingType();
}
