# University Management System (Java)

A comprehensive, console and GUI-based University Management System built in Java. This project simulates core university operations, demonstrating advanced Object-Oriented Programming (OOP) concepts, data persistence via text files, and a Java Swing graphical interface.

## Features

- **Role-Based Access Control:** Different interfaces and permissions for Students, Faculty, Admin Staff, and System Admins.
- **User Authentication:** Secure login system with password hashing (PBKDF2) and password reset functionality.
- **Student Management:** Course registration (with prerequisite checks, schedule conflict detection, and credit limits), course dropping, viewing grades, and GPA calculation.
- **Faculty Management:** Assign grades (supporting different grading strategies), manage course details, view student rosters, and set office hours.
- **Admin Staff Management:** Register new students, create new courses, and assign faculty to courses (with workload and schedule validation).
- **System Admin Management:** Create users, modify system settings, manage permissions, and backup data.
- **Data Persistence:** Automatically saves and loads users, students, courses, departments, and enrollments using `.txt` files (`FileManager`).
- **Java Swing GUI:** A user-friendly graphical interface (optional; console interface also available).

## Tech Stack

- **Language:** Java (JDK 8+)
- **GUI Framework:** Java Swing
- **Storage:** Local Text Files (`.txt`)
- **Security:** PBKDF2 with HMAC-SHA256 for password hashing

## Object-Oriented Design

This project heavily utilizes core OOP principles:
- **Inheritance:** `User` is an abstract base class extended by `Student`, `Faculty`, `AdminStaff`, and `SystemAdmin`.
- **Polymorphism:** Methods like `calculatePayroll()` and `generateReport()` behave differently depending on the user subclass.
- **Abstraction:** The `GradingSystem` interface is implemented by `LetterGradingStrategy` and `PassFailGradingStrategy`. `Course` is an abstract class extended by `Lecture` and `Lab`.
- **Encapsulation:** All class attributes are private with public getters/setters, and validation logic is hidden within the classes.
- **Design Patterns:** Utilizes the **Singleton** pattern for the `University` class to manage the central state.

## System Architecture (UML)

Below is the UML Class Diagram showing the relationships between the core classes, inheritance hierarchies, and interfaces.

![UML Diagram](UML%20Diagram.png)

## Getting Started

### Prerequisites
- Java JDK 8 or higher installed (`java -version` to check).

### Compile & Run

All source files, including `Main.java`, are contained within the `universitymanagementsystem` package folder. To compile and run the application, open your terminal in the root directory of this repository and execute:

```bash
# 1. Compile all Java files inside the package folder
javac universitymanagementsystem/*.java

# 2. Run the application
java universitymanagementsystem.Main
