package com.java.quizapp;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class FileManager {

    private static final Path DATA_DIR = Paths.get("data");

    private static final Path STUDENTS_FILE =
            DATA_DIR.resolve("students.txt");

    private static final Path QUESTIONS_FILE =
            DATA_DIR.resolve("questions.txt");

    private static final Path ATTEMPTS_FILE =
            DATA_DIR.resolve("attempts.txt");

    private static final Path REPORT_FILE =
            DATA_DIR.resolve("report.txt");

    public FileManager() {
        createDataFolder();
    }

    private void createDataFolder() {
        try {
            Files.createDirectories(DATA_DIR);
        } catch (IOException e) {
            System.out.println("Unable to create data folder.");
        }
    }

    public ArrayList<Student> loadStudents() {
        ArrayList<Student> students = new ArrayList<>();

        if (!Files.exists(STUDENTS_FILE)) {
            return students;
        }

        try (BufferedReader reader = Files.newBufferedReader(
                STUDENTS_FILE, StandardCharsets.UTF_8)) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] fields = line.split("\\|", -1);

                if (fields.length != 2) {
                    continue;
                }

                int id = Integer.parseInt(fields[0]);
                String name = fields[1];

                students.add(new Student(id, name));
            }

        } catch (Exception e) {
            System.out.println("Error reading students.txt");
        }

        return students;
    }

    public ArrayList<Question> loadQuestions() {
        ArrayList<Question> questions = new ArrayList<>();

        if (!Files.exists(QUESTIONS_FILE)) {
            return questions;
        }

        try (BufferedReader reader = Files.newBufferedReader(
                QUESTIONS_FILE, StandardCharsets.UTF_8)) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] fields = line.split("\\|", -1);

                if (fields.length != 7) {
                    continue;
                }

                int id = Integer.parseInt(fields[0]);

                String text = fields[1];

                String[] options = {
                    fields[2],
                    fields[3],
                    fields[4],
                    fields[5]
                };

                int correctOption = Integer.parseInt(fields[6]);

                questions.add(
                    new Question(id, text, options, correctOption)
                );
            }

        } catch (Exception e) {
            System.out.println("Error reading questions.txt");
        }

        return questions;
    }

    public ArrayList<QuizAttempt> loadAttempts() {
        ArrayList<QuizAttempt> attempts = new ArrayList<>();

        if (!Files.exists(ATTEMPTS_FILE)) {
            return attempts;
        }

        try (BufferedReader reader = Files.newBufferedReader(
                ATTEMPTS_FILE, StandardCharsets.UTF_8)) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] fields = line.split("\\|", -1);

                if (fields.length != 5) {
                    continue;
                }

                int id = Integer.parseInt(fields[0]);
                int studentId = Integer.parseInt(fields[1]);

                int[] questionIds = parseArray(fields[2]);
                int[] answers = parseArray(fields[3]);

                int score = Integer.parseInt(fields[4]);

                attempts.add(
                    new QuizAttempt(
                        id,
                        studentId,
                        questionIds,
                        answers,
                        score
                    )
                );
            }

        } catch (Exception e) {
            System.out.println("Error reading attempts.txt");
        }

        return attempts;
    }

    private int[] parseArray(String value) {

        String[] parts = value.split(",");

        int[] result = new int[parts.length];

        for (int i = 0; i < parts.length; i++) {
            result[i] = Integer.parseInt(parts[i].trim());
        }

        return result;
    }

    public boolean saveStudents(ArrayList<Student> students) {

        Path temp = DATA_DIR.resolve("students.tmp");

        try (BufferedWriter writer = Files.newBufferedWriter(
                temp,
                StandardCharsets.UTF_8)) {

            for (Student student : students) {

                writer.write(
                    student.getId() + "|" +
                    student.getName()
                );

                writer.newLine();
            }

        } catch (IOException e) {
            return false;
        }

        return replaceFile(temp, STUDENTS_FILE);
    }

    public boolean saveQuestions(ArrayList<Question> questions) {

        Path temp = DATA_DIR.resolve("questions.tmp");

        try (BufferedWriter writer = Files.newBufferedWriter(
                temp,
                StandardCharsets.UTF_8)) {

            for (Question question : questions) {

                String[] options = question.getOptions();

                writer.write(
                    question.getId() + "|" +
                    question.getText() + "|" +
                    options[0] + "|" +
                    options[1] + "|" +
                    options[2] + "|" +
                    options[3] + "|" +
                    question.getCorrectOption()
                );

                writer.newLine();
            }

        } catch (IOException e) {
            return false;
        }

        return replaceFile(temp, QUESTIONS_FILE);
    }

    public boolean saveAttempts(ArrayList<QuizAttempt> attempts) {

        Path temp = DATA_DIR.resolve("attempts.tmp");

        try (BufferedWriter writer = Files.newBufferedWriter(
                temp,
                StandardCharsets.UTF_8)) {

            for (QuizAttempt attempt : attempts) {

                writer.write(
                    attempt.getId() + "|" +
                    attempt.getStudentId() + "|" +
                    arrayToString(attempt.getQuestionIds()) + "|" +
                    arrayToString(attempt.getAnswers()) + "|" +
                    attempt.getScore()
                );

                writer.newLine();
            }

        } catch (IOException e) {
            return false;
        }

        return replaceFile(temp, ATTEMPTS_FILE);
    }

    private String arrayToString(int[] values) {

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < values.length; i++) {

            if (i > 0) {
                result.append(",");
            }

            result.append(values[i]);
        }

        return result.toString();
    }

    private boolean replaceFile(Path temp, Path original) {

        try {

            Files.move(
                temp,
                original,
                StandardCopyOption.REPLACE_EXISTING
            );

            return true;

        } catch (IOException e) {

            System.out.println(
                "Error saving file: " + original
            );

            return false;
        }
    }

    public boolean saveReport(String report) {

        try (BufferedWriter writer = Files.newBufferedWriter(
                REPORT_FILE,
                StandardCharsets.UTF_8)) {

            writer.write(report);

            return true;

        } catch (IOException e) {

            System.out.println("Unable to save report.");

            return false;
        }
    }
}