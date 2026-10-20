package universitymanagementsystem;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileManager {

    public static final String USERS_FILE = "users.txt";
    public static final String STUDENTS_FILE = "students.txt";
    public static final String COURSES_FILE = "courses.txt";
    public static final String DEPARTMENTS_FILE = "departments.txt";
    public static final String ENROLLMENTS_FILE = "enrollments.txt";

    public void saveUsers(List<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User user : users) {
                String role = user instanceof Student ? "Student"
                        : user instanceof Faculty ? "Faculty"
                                : user instanceof AdminStaff ? "AdminStaff"
                                        : user instanceof SystemAdmin ? "SystemAdmin" : "Unknown";
                String extraData = "";
                if (user instanceof Student) {
                    Student s = (Student) user;
                    extraData = String.format(",%s,%s", s.getStudentID(), s.getAcademicStatus());
                } else if (user instanceof Faculty) {
                    Faculty f = (Faculty) user;
                    extraData = String.format(",%s,%s,%s,%s",
                            f.getFacultyID(), f.getDepartment(), f.getExpertise(),
                            String.join(";", f.getCoursesTeaching()));
                } else if (user instanceof AdminStaff) {
                    AdminStaff a = (AdminStaff) user;
                    extraData = String.format(",%s,%s,%s", a.getStaffID(), a.getDepartment(), a.getRole());
                } else if (user instanceof SystemAdmin) {
                    SystemAdmin sa = (SystemAdmin) user;
                    extraData = String.format(",%s,%s", sa.getAdminID(), sa.getSecurityLevel());
                }
                writer.write(String.format("%s,%s,%s,%s,%s,%s,%s%s",
                        user.getUserID(), user.getUsername(), user.getPassword(),
                        role, user.getName(), user.getEmail(), user.getContactInfo(), extraData));
                writer.newLine();
            }
            System.out.println("Successfully saved to: " + USERS_FILE);
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    public List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    String userID = parts[0];
                    String username = parts[1];
                    String password = parts[2];
                    String role = parts[3];
                    String name = parts[4];
                    String email = parts[5];
                    String contactInfo = parts[6];
                    try {
                        switch (role) {
                            case "Student":
                                if (parts.length >= 9) {
                                    String studentID = parts[7];
                                    String academicStatus = parts[8];
                                    users.add(new Student(userID, username, password, name, email, contactInfo,
                                            studentID, "", academicStatus));
                                }
                                break;
                            case "Faculty":
                                if (parts.length >= 11) {
                                    String facultyId = parts[7];
                                    String department = parts[8];
                                    String expertise = parts[9];
                                    List<String> coursesTeaching = parts[10].isEmpty() ? new ArrayList<>()
                                            : Arrays.asList(parts[10].split(";"));
                                    Faculty faculty = new Faculty(userID, username, password, name, email, contactInfo,
                                            facultyId, department, expertise);
                                    faculty.getCoursesTeaching().addAll(coursesTeaching);
                                    users.add(faculty);
                                }
                                break;
                            case "AdminStaff":
                                if (parts.length >= 10) {
                                    String staffId = parts[7];
                                    String department = parts[8];
                                    String adminRole = parts[9];
                                    users.add(new AdminStaff(userID, username, password, name, email, contactInfo,
                                            staffId, department, adminRole));
                                }
                                break;
                            case "SystemAdmin":
                                if (parts.length >= 9) {
                                    String adminId = parts[7];
                                    String securityLevel = parts[8];
                                    users.add(new SystemAdmin(userID, username, password, name, email, contactInfo,
                                            adminId, securityLevel));
                                }
                                break;
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid user data: " + line);
                    }
                }
            }
            System.out.println("Successfully loaded from: " + USERS_FILE);
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
        return users;
    }

    public void saveStudents(List<Student> students) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STUDENTS_FILE))) {
            for (Student student : students) {
                writer.write(String.format("%s,%s,%s,%s",
                        student.getStudentID(), student.getName(),
                        student.getAdmissionDate(), student.getAcademicStatus()));
                writer.newLine();
            }
            System.out.println("Successfully saved to: " + STUDENTS_FILE);
        } catch (IOException e) {
            System.out.println("Error saving students: " + e.getMessage());
        }
    }

    public List<Student> loadStudents() {
        List<Student> students = new ArrayList<>();
        List<User> users = loadUsers();
        Map<String, User> userMap = new HashMap<>();
        for (User user : users) {
            userMap.put(user.getUserID(), user);
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(STUDENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String studentId = parts[0];
                    User user = userMap.get(studentId);
                    if (user instanceof Student) {
                        Student student = (Student) user;
                        student.setName(parts[1]);
                        student.setAdmissionDate(parts[2]);
                        student.setAcademicStatus(parts[3]);
                        students.add(student);
                    }
                }
            }
            System.out.println("Successfully loaded from: " + STUDENTS_FILE);
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            System.out.println("Error loading students: " + e.getMessage());
        }
        return students;
    }

    public void saveCourses(List<Course> courses) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(COURSES_FILE))) {
            for (Course course : courses) {
                String prereqs = course.getPrerequisites().stream()
                        .map(Course::getCourseID)
                        .collect(Collectors.joining(","));
                String instructorId = course.getInstructor() != null ? course.getInstructor().getFacultyID() : "";
                String courseType = course.getCourseType();
                String gradingType = course.getGradingSystem() != null ? course.getGradingSystem().getGradingType() : "Letter";
                writer.write(String.format("%s,%s,%s,%d,%s,%s,%s,%s,%s,%s",
                        course.getCourseID(), course.getTitle(), course.getDescription(),
                        course.getCreditHours(), course.getDepartmentID(), prereqs,
                        course.getSchedule(), instructorId, courseType, gradingType));
                writer.newLine();
            }
            System.out.println("Successfully saved to: " + COURSES_FILE);
        } catch (IOException e) {
            System.out.println("Error saving courses: " + e.getMessage());
        }
    }

    public List<Course> loadCourses() {
        List<Course> courses = new ArrayList<>();
        Map<String, Course> courseMap = new HashMap<>();
        Map<String, List<String>> prereqMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(COURSES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String courseId = parts[0];
                    String title = parts[1];
                    String description = parts[2];
                    int creditHours;
                    try {
                        creditHours = Integer.parseInt(parts[3]);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid credit hours for course " + courseId);
                        continue;
                    }
                    String departmentId = parts[4];
                    String prereqs = parts.length > 5 ? parts[5] : "";
                    String schedule = parts.length > 6 ? parts[6] : "";
                    String instructorId = parts.length > 7 ? parts[7] : "";
                    String courseType = parts.length > 8 ? parts[8] : "Lecture";
                    String gradingType = parts.length > 9 ? parts[9] : "Letter";
                    Course course;
                    GradingSystem gradingSystem;
                    if ("Letter".equals(gradingType)) {
                        gradingSystem = new LetterGradingStrategy();
                    } else if ("PassFail".equals(gradingType)) {
                        gradingSystem = new PassFailGradingStrategy();
                    } else {
                        System.out.println("Unknown grading type for course " + courseId);
                        continue;
                    }
                    if ("Lecture".equals(courseType)) {
                        course = new Lecture(courseId, title, description, creditHours, departmentId);
                    } else if ("Lab".equals(courseType)) {
                        course = new Lab(courseId, title, description, creditHours, departmentId);
                    } else {
                        System.out.println("Unknown course type for course " + courseId);
                        continue;
                    }
                    course.setGradingSystem(gradingSystem);
                    course.setSchedule(schedule);
                    System.out.println("Loaded course: " + courseId + ", title: " + title + ", type: " + courseType + ", grading: " + gradingType);
                    courses.add(course);
                    courseMap.put(courseId, course);
                    prereqMap.put(courseId, prereqs.isEmpty() ? new ArrayList<>() : Arrays.asList(prereqs.split(",")));
                }
            }
            for (Course course : courses) {
                List<Course> resolvedPrereqs = new ArrayList<>();
                for (String prereqId : prereqMap.getOrDefault(course.getCourseID(), new ArrayList<>())) {
                    Course prereq = courseMap.get(prereqId);
                    if (prereq != null) {
                        resolvedPrereqs.add(prereq);
                    }
                }
                course.setPrerequisites(resolvedPrereqs);
            }
            List<User> users = loadUsers();
            Map<String, Faculty> facultyMap = new HashMap<>();
            for (User user : users) {
                if (user instanceof Faculty) {
                    facultyMap.put(((Faculty) user).getFacultyID(), (Faculty) user);
                }
            }
            for (Course course : courses) {
                String instructorId = readInstructorId(course.getCourseID());
                if (!instructorId.isEmpty()) {
                    Faculty instructor = facultyMap.get(instructorId);
                    if (instructor != null) {
                        course.setInstructor(instructor);
                    }
                }
            }
            System.out.println("Successfully loaded from: " + COURSES_FILE);
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            System.out.println("Error loading courses: " + e.getMessage());
        }
        return courses;
    }

    private String readInstructorId(String courseId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(COURSES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 8 && parts[0].equals(courseId)) {
                    return parts[7];
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading instructor ID: " + e.getMessage());
        }
        return "";
    }

    public void saveDepartments(List<Department> departments) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DEPARTMENTS_FILE))) {
            for (Department dept : departments) {
                String facultyIds = dept.getFaculty().stream()
                        .map(Faculty::getFacultyID)
                        .collect(Collectors.joining(","));
                String courseIds = dept.getOfferedCourses().stream()
                        .map(Course::getCourseID)
                        .collect(Collectors.joining(","));
                writer.write(String.format("%s,%s,%s,%s,%s",
                        dept.getDepartmentID(), dept.getName(), dept.getDescription(),
                        facultyIds, courseIds));
                writer.newLine();
            }
            System.out.println("Successfully saved to: " + DEPARTMENTS_FILE);
        } catch (IOException e) {
            System.out.println("Error saving departments: " + e.getMessage());
        }
    }

    public List<Department> loadDepartments() {
        List<Department> departments = new ArrayList<>();
        List<User> users = loadUsers();
        List<Course> courses = loadCourses();
        Map<String, Faculty> facultyMap = new HashMap<>();
        for (User user : users) {
            if (user instanceof Faculty) {
                facultyMap.put(((Faculty) user).getFacultyID(), (Faculty) user);
            }
        }
        Map<String, Course> courseMap = new HashMap<>();
        for (Course c : courses) {
            courseMap.put(c.getCourseID(), c);
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(DEPARTMENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    Department dept = new Department(parts[0], parts[1], parts[2]);
                    if (parts.length > 3 && !parts[3].isEmpty()) {
                        for (String facultyId : parts[3].split(",")) {
                            Faculty faculty = facultyMap.get(facultyId);
                            if (faculty != null) {
                                dept.addFaculty(faculty);
                            }
                        }
                    }
                    if (parts.length > 4 && !parts[4].isEmpty()) {
                        for (String courseId : parts[4].split(",")) {
                            Course course = courseMap.get(courseId);
                            if (course != null) {
                                dept.addCourse(course);
                            }
                        }
                    }
                    departments.add(dept);
                }
            }
            System.out.println("Successfully loaded from: " + DEPARTMENTS_FILE);
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            System.out.println("Error loading departments: " + e.getMessage());
        }
        return departments;
    }

    public void saveEnrollments(List<Student> students) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ENROLLMENTS_FILE))) {
            for (Student student : students) {
                System.out.println("Saving student to enrollments: " + student.getStudentID());
                writer.write("StudentID: " + student.getStudentID());
                writer.newLine();
                List<Enrollment> enrollments = student.getEnrolledCourses();
                if (enrollments != null && !enrollments.isEmpty()) {
                    for (Enrollment enrollment : enrollments) {
                        Course course = enrollment.getCourse();
                        String grade = enrollment.getGrade() != null ? enrollment.getGrade() : "";
                        String status = enrollment.getStatus();
                        System.out.println("Saving enrollment: Student=" + student.getStudentID()
                                + ", Course=" + course.getCourseID() + ", Title=" + course.getTitle()
                                + ", Grade=" + grade);
                        writer.write(String.format("%s,%s,%s,%s",
                                course.getCourseID(), course.getTitle(), grade, status));
                        writer.newLine();
                    }
                }
                writer.newLine();
            }
            System.out.println("Successfully saved to: " + ENROLLMENTS_FILE);
        } catch (IOException e) {
            System.err.println("Error saving enrollments: " + e.getMessage());
        }
    }

    public void loadEnrollments(List<Student> students, List<Course> courses) {
        try (BufferedReader reader = new BufferedReader(new FileReader(ENROLLMENTS_FILE))) {
            String line;
            Student currentStudent = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("StudentID: ")) {
                    String studentId = line.substring("StudentID: ".length());
                    currentStudent = students.stream()
                            .filter(s -> s.getStudentID().equals(studentId))
                            .findFirst()
                            .orElse(null);
                    if (currentStudent != null) {
                        currentStudent.getEnrolledCourses().clear();
                    }
                } else if (!line.trim().isEmpty() && currentStudent != null) {
                    String[] parts = line.split(",", -1);
                    if (parts.length >= 4) {
                        String courseId = parts[0];
                        String grade = parts[2].isEmpty() ? null : parts[2];
                        String status = parts[3];
                        Course course = courses.stream()
                                .filter(c -> c.getCourseID().equals(courseId))
                                .findFirst()
                                .orElse(null);
                        if (course != null) {
                            Enrollment enrollment = new Enrollment(currentStudent, course, status);
                            if (grade != null) {
                                enrollment.setGrade(grade);
                            }
                            currentStudent.getEnrolledCourses().add(enrollment);
                            course.addStudent(currentStudent);
                            System.out.println("Loaded enrollment: Student=" + currentStudent.getStudentID()
                                    + ", Course=" + courseId + ", Grade=" + (grade != null ? grade : "null"));
                        }
                    }
                }
            }
            System.out.println("Successfully loaded from: " + ENROLLMENTS_FILE);
        } catch (IOException e) {
            System.err.println("Error loading enrollments: " + e.getMessage());
        }
    }

    public void loadAllData(University university) {
        List<User> users = loadUsers();
        university.setUsers(users);

        List<Student> students = loadStudents();
        Map<String, Student> studentMap = new HashMap<>();
        for (Student s : students) {
            studentMap.put(s.getUserID(), s);
        }
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i) instanceof Student) {
                Student updatedStudent = studentMap.get(users.get(i).getUserID());
                if (updatedStudent != null) {
                    users.set(i, updatedStudent);
                }
            }
        }

        List<Course> courses = loadCourses();
        university.setCourses(courses);

        List<Department> departments = loadDepartments();
        university.setDepartments(departments);

        loadEnrollments(students, courses);
    }
}
