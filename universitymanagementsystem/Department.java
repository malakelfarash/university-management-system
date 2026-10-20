package universitymanagementsystem;

import java.util.*;

public class Department {

    private String departmentID;
    private String name;
    private String description;
    private List<Faculty> faculty;
    private List<Course> offeredCourses;

    public Department() {
        this.faculty = new ArrayList<>();
        this.offeredCourses = new ArrayList<>();
    }

    public Department(String departmentID, String name, String description) {
        this.departmentID = departmentID;
        this.name = name;
        this.description = description;
        this.faculty = new ArrayList<>();
        this.offeredCourses = new ArrayList<>();
    }

    public String getDepartmentID() {
        return departmentID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Faculty> getFaculty() {
        return faculty;
    }

    public List<Course> getOfferedCourses() {
        return offeredCourses;
    }

    public void setDepartmentID(String departmentID) {
        this.departmentID = departmentID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFaculty(List<Faculty> faculty) {
        this.faculty = faculty;
    }

    public void setOfferedCourses(List<Course> offeredCourses) {
        this.offeredCourses = offeredCourses;
    }

    public void addFaculty(Faculty faculty) {
        if (!this.faculty.contains(faculty)) {
            this.faculty.add(faculty);
            faculty.setDepartment(departmentID);
            System.out.println("Faculty " + faculty.getName() + " added to department " + name);
        } else {
            System.out.println("Faculty " + faculty.getName() + " already in department.");
        }
    }

    public void removeFaculty(Faculty faculty) {
        if (this.faculty.remove(faculty)) {
            faculty.setDepartment("");
            System.out.println("Faculty " + faculty.getName() + " removed from department " + name);
        } else {
            System.out.println("Faculty " + faculty.getName() + " not found in department.");
        }
    }

    public void addCourse(Course course) {
        if (!offeredCourses.contains(course)) {
            offeredCourses.add(course);
            System.out.println("Course " + course.getCourseID() + " added to department " + name);
        } else {
            System.out.println("Course " + course.getCourseID() + " already offered.");
        }
    }

    public List<Faculty> getFacultyList() {
        return new ArrayList<>(faculty);
    }
}
