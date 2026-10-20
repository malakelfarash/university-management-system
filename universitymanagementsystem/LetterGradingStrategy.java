package universitymanagementsystem;

import java.util.Arrays;
import java.util.List;

public class LetterGradingStrategy implements GradingSystem {

    private static final List<String> VALID_GRADES = Arrays.asList(
            "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "D-", "F");

    @Override
    public boolean isValidGrade(String grade) {
        return grade != null && VALID_GRADES.contains(grade.toUpperCase());
    }

    @Override
    public void applyGrade(Enrollment enrollment, String grade) {
        if (isValidGrade(grade)) {
            enrollment.setGrade(grade.toUpperCase());
        } else {
            throw new IllegalArgumentException("Invalid grade: " + grade + ". Must be one of " + VALID_GRADES);
        }
    }

    @Override
    public String getGradingType() {
        return "Letter";
    }
}
