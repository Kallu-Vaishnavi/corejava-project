package com.java.quizapp;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

public class QuizService {

    private ArrayList<Student> students;
    private ArrayList<Question> questions;
    private ArrayList<QuizAttempt> attempts;

    private HashSet<String> questionKeys;

    private FileManager fileManager;

    public QuizService() {

        fileManager = new FileManager();

        students = fileManager.loadStudents();
        questions = fileManager.loadQuestions();
        attempts = fileManager.loadAttempts();

        questionKeys = new HashSet<>();

        rebuildQuestionKeys();
    }

    private void rebuildQuestionKeys() {

        questionKeys.clear();

        for (Question question : questions) {

            String key = question.getText()
                    .trim()
                    .toLowerCase(Locale.ROOT);

            questionKeys.add(key);
        }
    }

    public int getNextStudentId() {

        int max = 0;

        for (Student student : students) {

            if (student.getId() > max) {
                max = student.getId();
            }
        }

        return max + 1;
    }

    public int getNextQuestionId() {

        int max = 0;

        for (Question question : questions) {

            if (question.getId() > max) {
                max = question.getId();
            }
        }

        return max + 1;
    }

    public int getNextAttemptId() {

        int max = 0;

        for (QuizAttempt attempt : attempts) {

            if (attempt.getId() > max) {
                max = attempt.getId();
            }
        }

        return max + 1;
    }

    public boolean addStudent(String name) {

        int id = getNextStudentId();

        Student student = new Student(id, name);

        students.add(student);

        if (!fileManager.saveStudents(students)) {

            students.remove(students.size() - 1);

            return false;
        }

        System.out.println("Student registered successfully.");
        System.out.println("Student ID: " + id);

        return true;
    }

    public boolean addQuestion(
            String text,
            String[] options,
            int correctOption) {

        String key = text.trim()
                .toLowerCase(Locale.ROOT);

        if (questionKeys.contains(key)) {

            System.out.println(
                "Question already exists."
            );

            return false;
        }

        int id = getNextQuestionId();

        Question question =
                new Question(
                    id,
                    text.trim(),
                    options,
                    correctOption
                );

        questions.add(question);

        if (!fileManager.saveQuestions(questions)) {

            questions.remove(questions.size() - 1);

            return false;
        }

        questionKeys.add(key);

        System.out.println(
            "Question added successfully."
        );

        return true;
    }

    public Student findStudent(int id) {

        for (Student student : students) {

            if (student.getId() == id) {
                return student;
            }
        }

        return null;
    }

    public void showQuestionBank() {

        if (questions.isEmpty()) {

            System.out.println("No records found.");

            return;
        }

        System.out.println("\n===== QUESTION BANK =====");

        for (Question question : questions) {

            System.out.println(
                "Question ID: " + question.getId()
            );

            question.showQuestion();

            System.out.println(
                "Correct option: " +
                question.getCorrectOption()
            );

            System.out.println("-------------------------");
        }
    }

    public boolean hasFiveQuestions() {
        return questions.size() >= 5;
    }

    public void startQuiz(Student student) {

        if (questions.size() < 5) {

            System.out.println(
                "Add at least five questions first."
            );

            return;
        }

        int[] questionIds = new int[5];

        int[] answers = new int[5];

        for (int i = 0; i < 5; i++) {

            Question question = questions.get(i);

            questionIds[i] = question.getId();

            System.out.println("\nQuestion " + (i + 1));

            question.showQuestion();

            int answer =
                Main.readIntegerInRange(
                    1,
                    4,
                    "Your answer: "
                );

            answers[i] = answer;
        }

        int score = calculateScore(
            questionIds,
            answers
        );

        QuizAttempt attempt =
            new QuizAttempt(
                getNextAttemptId(),
                student.getId(),
                questionIds,
                answers,
                score
            );

        attempts.add(attempt);

        if (!fileManager.saveAttempts(attempts)) {

            attempts.remove(attempts.size() - 1);

            System.out.println(
                "Attempt could not be saved."
            );

            return;
        }

        System.out.println(
            "\nSelected answers: " +
            java.util.Arrays.toString(answers)
        );

        System.out.println(
            "Score: " + score + " / 5"
        );

        System.out.println(
            "Attempt " + attempt.getId() +
            " saved."
        );

        System.out.println(
            "\nCorrect answers:"
        );

        for (int i = 0; i < 5; i++) {

            Question question = questions.get(i);

            System.out.println(
                "Question " + (i + 1) +
                ": Option " +
                question.getCorrectOption()
            );
        }
    }

    public int calculateScore(
            int[] questionIds,
            int[] answers) {

        int score = 0;

        for (int i = 0; i < questionIds.length; i++) {

            Question question =
                findQuestion(questionIds[i]);

            if (question != null &&
                answers[i] ==
                question.getCorrectOption()) {

                score++;
            }
        }

        return score;
    }

    private Question findQuestion(int id) {

        for (Question question : questions) {

            if (question.getId() == id) {
                return question;
            }
        }

        return null;
    }

    public void showStudentAttempts(int studentId) {

        boolean found = false;

        for (QuizAttempt attempt : attempts) {

            if (attempt.getStudentId() == studentId) {

                found = true;

                System.out.println(
                    "Attempt " +
                    attempt.getId() +
                    " | Score " +
                    attempt.getScore() +
                    " / 5"
                );
            }
        }

        if (!found) {
            System.out.println("No attempts yet.");
        }
    }

    public void showAllAttempts() {

        if (attempts.isEmpty()) {

            System.out.println("No attempts yet.");

            return;
        }

        for (QuizAttempt attempt : attempts) {

            System.out.println(
                "Attempt " +
                attempt.getId() +
                " | Student " +
                attempt.getStudentId() +
                " | Score " +
                attempt.getScore() +
                " / 5"
            );
        }
    }

    public String buildReport() {

        StringBuilder report =
            new StringBuilder();

        report.append("===== QUIZ PRACTICE REPORT =====\n\n");

        report.append(
            "Student count: "
        ).append(students.size()).append("\n");

        report.append(
            "Question count: "
        ).append(questions.size()).append("\n");

        report.append(
            "Completed attempt count: "
        ).append(attempts.size()).append("\n");

        if (!attempts.isEmpty()) {

            int highest = 0;

            int total = 0;

            for (QuizAttempt attempt : attempts) {

                if (attempt.getScore() > highest) {
                    highest = attempt.getScore();
                }

                total += attempt.getScore();
            }

            double average =
                (double) total / attempts.size();

            report.append(
                "Highest score: "
            ).append(highest).append(" / 5\n");

            report.append(
                "Average score: "
            ).append(String.format("%.2f", average))
            .append(" / 5\n");
        }

        report.append("\nStudent Best Scores:\n");

        for (Student student : students) {

            int best = 0;

            for (QuizAttempt attempt : attempts) {

                if (attempt.getStudentId() ==
                    student.getId()) {

                    if (attempt.getScore() > best) {
                        best = attempt.getScore();
                    }
                }
            }

            report.append(
                student.getId() +
                " - " +
                student.getName() +
                " : " +
                best +
                " / 5\n"
            );
        }

        return report.toString();
    }

    public void showReport() {

        System.out.println();
        System.out.println(buildReport());
    }

    public void saveReport() {

        String report = buildReport();

        if (fileManager.saveReport(report)) {

            System.out.println(
                "Report saved successfully."
            );
        }
    }
}