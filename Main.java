package com.java.quizapp;



import java.util.Scanner;

public class Main {

    private static Scanner scanner =
            new Scanner(System.in);

    private static QuizService service =
            new QuizService();

    public static void main(String[] args) {

        System.out.println(
            "================================"
        );

        System.out.println(
            "     QUIZ PRACTICE APPLICATION"
        );

        System.out.println(
            "================================"
        );

        while (true) {

            System.out.println("\n===== MAIN MENU =====");

            System.out.println(
                "1. Register student"
            );

            System.out.println(
                "2. Admin menu"
            );

            System.out.println(
                "3. Student menu"
            );

            System.out.println(
                "0. Exit"
            );

            int choice =
                readIntegerInRange(
                    0,
                    3,
                    "Enter choice: "
                );

            switch (choice) {

                case 1:
                    registerStudent();
                    break;

                case 2:
                    showAdminMenu();
                    break;

                case 3:
                    showStudentMenu();
                    break;

                case 0:

                    System.out.println(
                        "Thank you for using Quiz Practice Application."
                    );

                    scanner.close();

                    return;
            }
        }
    }

    private static void registerStudent() {

        System.out.print(
            "Enter student name: "
        );

        String name =
            scanner.nextLine().trim();

        while (name.isEmpty()) {

            System.out.println(
                "Name cannot be blank."
            );

            System.out.print(
                "Enter student name: "
            );

            name =
                scanner.nextLine().trim();
        }

        if (name.contains("|") ||
            name.contains("\n")) {

            System.out.println(
                "Invalid character in name."
            );

            return;
        }

        service.addStudent(name);
    }

    private static void showAdminMenu() {

        Admin admin = new Admin();

        while (true) {

            System.out.println("\n===== ADMIN MENU =====");

            System.out.println(
                "1. Add question"
            );

            System.out.println(
                "2. View question bank"
            );

            System.out.println(
                "3. View attempts"
            );

            System.out.println(
                "4. View reports"
            );

            System.out.println(
                "0. Back"
            );

            int choice =
                readIntegerInRange(
                    0,
                    4,
                    "Enter choice: "
                );

            switch (choice) {

                case 1:
                    addQuestion();
                    break;

                case 2:
                    service.showQuestionBank();
                    break;

                case 3:
                    service.showAllAttempts();
                    break;

                case 4:
                    service.showReport();

                    System.out.print(
                        "Save report? (y/n): "
                    );

                    String answer =
                        scanner.nextLine().trim();

                    if (answer.equalsIgnoreCase("y")) {
                        service.saveReport();
                    }

                    break;

                case 0:
                    return;
            }
        }
    }

    private static void addQuestion() {

        System.out.println(
            "\n===== ADD QUESTION ====="
        );

        System.out.print(
            "Question text: "
        );

        String text =
            scanner.nextLine().trim();

        if (text.isEmpty()) {

            System.out.println(
                "Question cannot be blank."
            );

            return;
        }

        if (text.contains("|") ||
            text.contains("\n")) {

            System.out.println(
                "Question cannot contain |."
            );

            return;
        }

        String[] options = new String[4];

        for (int i = 0; i < 4; i++) {

            System.out.print(
                "Option " + (i + 1) + ": "
            );

            options[i] =
                scanner.nextLine().trim();

            if (options[i].isEmpty()) {

                System.out.println(
                    "Option cannot be blank."
                );

                return;
            }

            if (options[i].contains("|")) {

                System.out.println(
                    "Option cannot contain |."
                );

                return;
            }
        }

        int correctOption =
            readIntegerInRange(
                1,
                4,
                "Correct option number 1 to 4: "
            );

        service.addQuestion(
            text,
            options,
            correctOption
        );
    }

    private static void showStudentMenu() {

        System.out.println("\n===== STUDENT LOGIN =====");

        int studentId =
            readPositiveInteger(
                "Enter student ID: "
            );

        Student student =
            service.findStudent(studentId);

        if (student == null) {

            System.out.println(
                "Unknown student ID."
            );

            return;
        }

        student.showProfile();

        while (true) {

            System.out.println(
                "\n===== STUDENT MENU ====="
            );

            System.out.println(
                "1. Take quiz"
            );

            System.out.println(
                "2. View my attempts"
            );

            System.out.println(
                "0. Back"
            );

            int choice =
                readIntegerInRange(
                    0,
                    2,
                    "Enter choice: "
                );

            switch (choice) {

                case 1:
                    service.startQuiz(student);
                    break;

                case 2:
                    service.showStudentAttempts(
                        student.getId()
                    );
                    break;

                case 0:
                    return;
            }
        }
    }

    public static int readIntegerInRange(
            int min,
            int max,
            String message) {

        while (true) {

            System.out.print(message);

            String input =
                scanner.nextLine().trim();

            try {

                int value =
                    Integer.parseInt(input);

                if (value >= min &&
                    value <= max) {

                    return value;
                }

            } catch (NumberFormatException e) {

                // Invalid input handled below
            }

            System.out.println(
                "Invalid input. Enter a number from " +
                min + " to " + max + "."
            );
        }
    }

    private static int readPositiveInteger(
            String message) {

        while (true) {

            System.out.print(message);

            String input =
                scanner.nextLine().trim();

            try {

                int value =
                    Integer.parseInt(input);

                if (value > 0) {
                    return value;
                }

            } catch (NumberFormatException e) {

                // Invalid input
            }

            System.out.println(
                "Please enter a valid positive number."
            );
        }
    }
}
