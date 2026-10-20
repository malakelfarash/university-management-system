package universitymanagementsystem;

import java.util.Arrays;
import java.util.List;

public class PassFailGradingStrategy implements GradingSystem {

    private static final List<String> VALID_GRADES = Arrays.asList("Pass", "Fail");

    @Override
    public boolean isValidGrade(String grade) {
        return grade != null && VALID_GRADES.contains(grade);
    }

    @Override
    public void applyGrade(Enrollment enrollment, String grade) {
        if (isValidGrade(grade)) {
            enrollment.setGrade(grade);
        } else {
            throw new IllegalArgumentException("Invalid grade: " + grade + ". Must be Pass or Fail");
        }
    }

    @Override
    public String getGradingType() {
        return "PassFail";
    }
}
