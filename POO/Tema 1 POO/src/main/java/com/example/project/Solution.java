package com.example.project;

import com.opencsv.CSVWriter;

import java.io.*;

public class Solution {

    private final int score;

    private final String quizzId;

    private final String quizzName;

    private final User submitter;

    public Solution(int score, String quizzId, User submiter, String quizzName) {
        this.score = score;
        this.quizzId = quizzId;
        this.submitter = submiter;
        this.quizzName = quizzName;
    }

    public int getScore() {
        return score;
    }

    public String getQuizzId() {
        return quizzId;
    }

    public User getSubmitter() {
        return submitter;
    }

    public String getQuizzName() {
        return quizzName;
    }

    public static int checkSolutions(String path, Solution solution) {
        try {
            File file = new File(path);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String read = br.readLine();
            if(read == null) {
                br.close();
                fr.close();
                return -1;
            }
            read = br.readLine();
            while (read != null) {

                String[] solution_info = read.split(",");
                String submiter = solution_info[0];
                String quizId = solution_info[2];
                if(solution.getSubmitter().getUsername().compareTo(submiter) == 0
                   && solution.getQuizzId().compareTo(quizId) == 0) {
                    br.close();
                    fr.close();
                    return -2;
                }
                read = br.readLine();
            }
            br.close();
            fr.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getMySolutions(String path, User user) {
        try {
            String mySolutions = "[";
            File file = new File(path);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String read = br.readLine();
            if(read == null) {
                br.close();
                fr.close();
                return null;
            }
            int index_in_list = 1;
            read = br.readLine();
            while (read != null) {

                String[] solution_info = read.split(",");
                String submitter = solution_info[0];
                String score = solution_info[1];
                String quizId = solution_info[2];
                String quizName = solution_info[3];
                if(user.getUsername().compareTo(submitter) == 0) {
                    mySolutions += "{\"quiz-id\""+ " : " + "\"" + quizId + "\""
                                   + ", " + "\"" + "quiz-name" + "\"" + " : "
                                   + "\"" + quizName + "\""
                                   + ", " + "\"" + "score" + "\"" + " : "
                                   + "\"" + score + "\"" +
                                   ", " + "\"" + "index_in_list" + "\"" + " : "
                                   + "\"" + index_in_list + "\"" + "}";
                    index_in_list++;
                }
                read = br.readLine();
            }
            br.close();
            fr.close();
            mySolutions += "]";
            return mySolutions;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String writeSolutionsToFile(String path, Solution solution) {
        try {
            File file = new File(path);
            FileWriter fileWriter = new FileWriter(file, true);
            char[] csvFlags = {CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.NO_ESCAPE_CHARACTER};
            CSVWriter csvWriter = new CSVWriter(fileWriter, ',',csvFlags[0], csvFlags[1], "\n");

            int readQuestionsReturn = Solution.checkSolutions(path, solution);
            if(checkSolutions(path, solution) == -1) {
                String[] header = {"submitter", "score", "quizz_id", "quizz_name"};
                csvWriter.writeNext(header);
            }
            if(readQuestionsReturn == -2) {
                csvWriter.close();
                fileWriter.close();
                return "{ 'status' : 'error', 'message' : 'You already submitted this quizz' }";
            }
            String[] questionInfo = {solution.getSubmitter().getUsername(), String.valueOf(solution.getScore()),
                                     solution.getQuizzId(), solution.getQuizzName()};

            csvWriter.writeNext(questionInfo);

            csvWriter.close();
            fileWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
