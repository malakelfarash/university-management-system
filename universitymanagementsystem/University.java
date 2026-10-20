package universitymanagementsystem;

import java.util.*;
import java.util.stream.Collectors;

public class University {

    private static University instance;
    private List<User> users;
    private List<Course> courses;
    private List<Department> departments;
    private FileManager fileManager;

    private University() {
        this.users = new ArrayList<>();
        this.courses = new ArrayList<>();
        this.departments = new ArrayList<>();
        this.fileManager = new FileManager();
    }

    public static University getInstance() {
        if (instance == null) {
            instance = new University();
        }
        return instance;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public void createDepartment(Department department) {
        if (departments.stream().noneMatch(d -> d.getDepartmentID().equals(department.getDepartmentID()))) {
            departments.add(department);
            fileManager.saveDepartments(departments);
            System.out.println("Department " + department.getName() + " created.");
        } else {
            System.out.println("Department " + department.getName() + " already exists.");
        }
    }

    public void offerCourse(Course course, Department department) {
        if (courses.stream().noneMatch(c -> c.getCourseID().equals(course.getCourseID()))) {
            courses.add(course);
            department.addCourse(course);
            fileManager.saveCourses(courses);
            fileManager.saveDepartments(departments);
            System.out.println("Course " + course.getCourseID() + " offered by department " + department.getName());
        } else {
            System.out.println("Course " + course.getCourseID() + " already exists.");
        }
    }

    public void hireFaculty(Faculty faculty, Department department) {
        if (users.stream().noneMatch(u -> u.getUserID().equals(faculty.getUserID()))) {
            users.add(faculty);
            department.addFaculty(faculty);
            fileManager.saveUsers(users);
            fileManager.saveDepartments(departments);
            System.out.println("Faculty " + faculty.getName() + " hired in department " + department.getName());
        } else {
            System.out.println("Faculty " + faculty.getName() + " already exists.");
        }
    }

    public void registerStudent(Student student) {
        if (users.stream().noneMatch(u -> u.getUserID().equals(student.getUserID()))) {
            users.add(student);
            fileManager.saveUsers(users);
            System.out.println("Student registered: " + student.getName());
        } else {
            System.out.println("Student " + student.getName() + " already exists.");
        }
    }

    public void saveAllStudents() {
        fileManager.saveStudents(users.stream()
                .filter(u -> u instanceof Student)
                .map(u -> (Student) u)
                .collect(Collectors.toList()));
    }

    public void saveAllEnrollments() {
        List<Student> students = users.stream()
                .filter(u -> u instanceof Student)
                .map(u -> (Student) u)
                .collect(Collectors.toList());
        fileManager.saveEnrollments(students);
    }
}
