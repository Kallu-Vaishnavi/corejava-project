package com.java.quizapp;



public class Question {

    private int id;
    private String text;
    private String[] options;
    private int correctOption;

    public Question(int id, String text, String[] options, int correctOption) {
        this.id = id;
        this.text = text;
        this.options = options.clone();
        this.correctOption = correctOption;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String[] getOptions() {
        return options.clone();
    }

    public int getCorrectOption() {
        return correctOption;
    }

    public void showQuestion() {
        System.out.println(text);

        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
    }
}