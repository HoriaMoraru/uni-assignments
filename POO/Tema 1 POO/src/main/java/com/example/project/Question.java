package com.example.project;

import com.opencsv.CSVWriter;

import java.io.*;
import java.util.ArrayList;

public class Question implements Comparable<String> {

    private final int identifier;

    private final String text;

    protected ArrayList<Answer> answers;

    private final String type;

    private static int number_of_questions = 0;

    static class Answer implements Comparable<Answer> {

        private final int identifier;

        private final String text;

        private final boolean correct;

        private static int number_of_answers = 0;

        public Answer(String text, boolean correct) {
            number_of_answers++;
            this.identifier = number_of_answers;
            this.text = text;
            this.correct = correct;
        }

        public Answer(int identifier) {
            this.identifier = identifier;
            this.text = null;
            this.correct = true;
        }

        public Answer(int identifier, boolean correct) {
            this.identifier = identifier;
            this.text = null;
            this.correct = correct;
        }

        public int getIdentifier() {
            return this.identifier;
        }

        public String getText() {
            return this.text;
        }

        public boolean checkCorrect() {
            return this.correct;
        }

        public static void resetNumber_of_answers() {
            number_of_answers = 0;
        }

        public int compareTo(Answer answer) {
            if(this.identifier == answer.identifier && this.correct == answer.correct) {
                return 0;
            }
            return -1;
        }

        public static int numberOfCorectAnswers(ArrayList<Answer> answers) {
            int correct_answers = 0;
            for (Answer answer : answers) {
                if (answer.correct) {
                    correct_answers++;
                }
            }
            return correct_answers;
        }

        public static boolean checkDuplicateAnswers(ArrayList<Answer> answers) {
            for (int i = 0; i < answers.size(); i++) {
                for (int j = i + 1; j < answers.size(); j++) {
                    if (answers.get(i).getText().compareTo(answers.get(j).getText()) == 0) {
                        return true;
                    }
                }
            }
            return false;
        }

        public static void writeAnswersToFile(String path, Question question) {
            try {
                File file = new File(path);
                FileWriter fileWriter = new FileWriter(file, true);
                char[] csvFlags = {CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.NO_ESCAPE_CHARACTER};
                CSVWriter csvWriter = new CSVWriter(fileWriter, ',',csvFlags[0], csvFlags[1], "\n");

                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                String read = br.readLine();
                if(read == null) {
                    String[] header = {"question_identifier", "identifier", "answer", "correct"};
                    csvWriter.writeNext(header);
                }
                br.close();
                fr.close();
                for(int i = 0 ; i < question.answers.size(); i++) {
                    String[] data = {String.valueOf(question.getIdentifier()),
                                     String.valueOf(question.answers.get(i).getIdentifier()),
                                     question.answers.get(i).getText(),
                                     Boolean.toString(question.answers.get(i).checkCorrect())};

                    csvWriter.writeNext(data);
                }

                csvWriter.close();
                fileWriter.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static String getAnswersByQuestionId(String path, String id) {
            String answers = "";
            try {
                File file = new File(path);
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                String read = br.readLine();
                if(read == null) {
                    br.close();
                    fr.close();
                    return null;
                }
                read = br.readLine();
                while (read != null) {
                    String[] answers_info = read.split(",");
                    String question_identifier = answers_info[0];
                    String answer_identifier = answers_info[1];
                    String name = answers_info[2];
                    if(question_identifier.compareTo(id) == 0) {
                        answers += "{\"answer_name\":\"" + name +
                                   "\", \"answer_id\":\"" + answer_identifier + "\"}, ";
                    }
                    read = br.readLine();
                }
                answers = answers.substring(0, answers.length() - 2);
                br.close();
                fr.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return answers;
        }

        public static int[] compareAnswers(ArrayList<Answer> answersProvided, ArrayList<Answer> allAnswers) {
            int[] results = new int[3];
            int correct_answers = 0;
            int incorrect_answers = 0;
            int unanswered = 0;
            for(Question.Answer all_answer : allAnswers) {
                boolean found = false;
                for(Question.Answer answer_provided : answersProvided) {
                    if(all_answer.identifier == answer_provided.identifier) {
                        found = true;
                        if(all_answer.correct == answer_provided.correct) {
                            correct_answers++;
                            break;
                        }
                        else {
                            incorrect_answers++;
                            break;
                        }
                    }
                }
                if(!found) {
                    unanswered++;
                }
                if(all_answer.checkCorrect() && !found) {
                    incorrect_answers++;
                }
                if(!all_answer.checkCorrect() && !found) {
                    correct_answers++;
                }
            }
            results[0] = correct_answers;
            results[1] = incorrect_answers;
            results[2] = unanswered;
            return results;
        }
    }

    public Question(String text, String type) {
        this.text = text;
        this.type = type;
        number_of_questions++;
        this.identifier = number_of_questions;
        this.answers = new ArrayList<>();
    }

    public Question(String text) {
        this.text = text;
        this.identifier = -1;
        this.type = null;
    }

    public Question(String text, int identifier) {
        this.text = text;
        this.identifier = identifier;
        this.type = null;
    }

    public int getIdentifier() {
        return this.identifier;
    }

    public String getText() {
        return this.text;
    }

    public String getType() {
        return this.type;
    }

    public static int getNumber_of_questions() {
        return number_of_questions;
    }

    public void addAnswer(Answer answer) {
        this.answers.add(answer);
    }

    public static void resetNumber_of_questions() {
        number_of_questions = 0;
    }

    public int compareTo(String text) {
        return this.text.compareTo(text);
    }

    public static int readQuestionsFromFile(String path, Question question) {
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

                String questionText = read.substring(read.indexOf(",") + 1, read.lastIndexOf(","));
                if(question.compareTo(questionText) == 0) {
                    br.close();
                    fr.close();
                    return -2;
                }
                read = br.readLine();
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getQuestionIdentifier(String path, Question question) {
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

                String questionText = read.substring(read.indexOf(",") + 1, read.lastIndexOf(","));
                if(question.compareTo(questionText) == 0) {
                    br.close();
                    fr.close();
                    return Integer.parseInt(read.substring(0, read.indexOf(",")));
                }
                read = br.readLine();
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -2;
    }

    public static String writeQuestionsToFile(String path, Question question) {

        try {
            File file = new File(path);
            FileWriter fileWriter = new FileWriter(file, true);
            char[] csvFlags = {CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.NO_ESCAPE_CHARACTER};
            CSVWriter csvWriter = new CSVWriter(fileWriter, ',',csvFlags[0], csvFlags[1], "\n");

            int readQuestionsReturn = Question.readQuestionsFromFile(path, question);
            if(readQuestionsReturn == -1) {
                String[] header = {"identifier", "text", "type"};
                csvWriter.writeNext(header);
            }
            if(readQuestionsReturn == -2) {
                csvWriter.close();
                fileWriter.close();
                return "{ 'status' : 'error', 'message' : 'Question already exists'}";
            }
            String[] questionInfo = {String.valueOf(question.getIdentifier()),
                                     question.getText(), question.getType()};
            csvWriter.writeNext(questionInfo);

            csvWriter.close();
            fileWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String addAnswers(String[] args, Question question) {
        for(int i = 5; i < args.length - 1; i += 2) {
            int currentAnswerIndex = (i - 5) / 2 + 1;
            if(args[i].contains("-is-correct")) {
                return "{ 'status' : 'error', 'message' : 'Answer " + currentAnswerIndex + " has no answer description'}";
            }
            String[] answer_text = args[i].split(" ");
            String answerText = MetodeTema.removeUselessCharacters(answer_text[1]);
            if(!args[i+1].contains("is-correct")) {
                return "{ 'status' : 'error', 'message' : 'Answer " + currentAnswerIndex + " has no answer correct flag'}";
            }
            String[] is_correct = args[i + 1].split(" ");
            String isCorrect = MetodeTema.removeUselessCharacters(is_correct[1]);
            boolean isCorrectBoolean = MetodeTema.turnStringIntoBoolean(isCorrect);
            Answer answer = new Answer(answerText, isCorrectBoolean);
            question.addAnswer(answer);
        }
        return null;
    }

    public static String getAllQuestions(String path_to_question) {
        try {
            File file = new File(path_to_question);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String read = br.readLine();
            if(read == null) {
                br.close();
                fr.close();
                return null;
            }
            read = br.readLine();
            String questions = "";
            int i = 0;
            while (read != null) {
                String identifier = read.substring(0, read.indexOf(","));
                String text = read.substring(read.indexOf(",") + 1, read.lastIndexOf(","));
                questions += "{\"question_id\""+ " : " + "\"" + identifier + "\"" +
                             ", " + "\"" + "question_name" + "\"" + " : "
                             + "\"" + text + "\"" + "}";
                if(i != Question.getNumber_of_questions() - 1) {
                    questions += ", ";
                }
                i++;
                read = br.readLine();
            }
            br.close();
            fr.close();
            return questions;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getQuestionDetailsById(String path, String index) {
        try {
            String quizz_details = "";
            String path_to_answers = "src/main/java/com/example/project/answers.csv";
            File file = new File(path);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String read = br.readLine();
            if(read == null) {
                br.close();
                fr.close();
                return null;
            }
            read = br.readLine();
            while (read != null) {
                String identifier = read.substring(0, read.indexOf(","));
                if(identifier.equals(index)) {
                    String text = read.substring(read.indexOf(",") + 1, read.lastIndexOf(","));
                    String type = read.substring(read.lastIndexOf(",") + 1);
                    //Make quizz details string like [{"question-name":"Cerul este albastru", "question_index":"1", "question_type":"single", "answers":"[{"answer_name":"Yes", "answer_id":"1"}, {"answer_name":"No", "answer_id":"2"}]"}, {"question-name":"Temperatura se poate masura in", "question_index":"2", "question_type":"single", "answers":"[{"answer_name":"C", "answer_id":"3"}, {"answer_name":"L", "answer_id":"4"}]"}]'}
                    quizz_details = "{\"question-name\":\"" + text + "\", \"question_index\":\""
                                    + identifier + "\", \"question_type\":\"" + type + "\", \"answers\":\""
                                    + "[" + Answer.getAnswersByQuestionId(path_to_answers, index) + "]" + "\"}";
                }
                read = br.readLine();
            }
            br.close();
            fr.close();
            return quizz_details;
        }
        catch (IOException e) {
            e.printStackTrace();

        }
        return null;
    }

    public static ArrayList<Answer> getAnswers(String index) {
        String path_to_answers = "src/main/java/com/example/project/answers.csv";
        ArrayList<Answer> allAnswers = new ArrayList<>();
        try {
            File file = new File(path_to_answers);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String read = br.readLine();
            if(read == null) {
                br.close();
                fr.close();
                return null;
            }
            read = br.readLine();
            while (read != null) {
                String[] answer_info = read.split(",");
                String question_id = answer_info[0];
                if(question_id.equals(index)) {
                    String answer_id = answer_info[1];
                    String is_correct = answer_info[3];
                    Answer answer = new Answer(Integer.parseInt(answer_id), Boolean.parseBoolean(is_correct));
                    allAnswers.add(answer);
                }
                read = br.readLine();
            }
            br.close();
            fr.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return allAnswers;
    }

}