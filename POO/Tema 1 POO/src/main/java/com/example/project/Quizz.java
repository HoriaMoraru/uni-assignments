package com.example.project;

import com.opencsv.CSVWriter;

import java.io.*;
import java.util.ArrayList;

public class Quizz implements Comparable<String> {

    private final int identifier;

    private final String name;

    protected final ArrayList<Question> questions;

    private final User creator;

    private final boolean is_completed;

    private static int number_of_quizzes = 0;

    public Quizz(String name, User creator) {
        this.creator = creator;
        this.name = name;
        this.questions = new ArrayList<>();
        number_of_quizzes++;
        this.identifier = number_of_quizzes;
        this.is_completed = false;
    }

    public Quizz(String name) {
        this.creator = null;
        this.name = name;
        this.questions = null;
        this.identifier = -1;
        this.is_completed = false;
    }

    public int getIdentifier() {
        return this.identifier;
    }

    public User getCreator() {
        return this.creator;
    }

    public String getName() {
        return this.name;
    }

    public static void lowerNumberOfQuizzes() {
        number_of_quizzes--;
    }

    public boolean checkCompleted() {
        return this.is_completed;
    }

    public static void resetNumber_of_quizzes() {
        number_of_quizzes = 0;
    }

    public int compareTo(String name) {
        return this.name.compareTo(name);
    }

    public static int readQuestionsForQuizz(String[] args, String path_to_question, Quizz quizz) {
        int[] identifiers = new int[args.length - 4];
        for (int j = 4; j < args.length; j++) {
            String[] id_info = args[j].split(" ");
            String id = MetodeTema.removeUselessCharacters(id_info[1]);
            identifiers[j - 4] = Integer.parseInt(id);
        }
        try {
            File file = new File(path_to_question);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String read = br.readLine();
            if (read == null) {
                br.close();
                fr.close();
                return -1;
            }
            read = br.readLine();
            int i = 0;
            while (read != null) {
                if (i >= identifiers.length) {
                    break;
                }
                String file_identifier = read.substring(0, read.indexOf(","));
                int identifier = Integer.parseInt(file_identifier);
                String text = read.substring(read.indexOf(",") + 1, read.lastIndexOf(","));
                if (identifier == identifiers[i]) {
                    quizz.questions.add(new Question(text, identifier));
                    i++;
                }
                read = br.readLine();
            }
            br.close();
            fr.close();
            return i;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int readQuizzesFromFile(String path, Quizz quizz) {
        try {
            File file = new File(path);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String read = br.readLine();
            if (read == null) {
                br.close();
                fr.close();
                return -1;
            }
            read = br.readLine();
            while (read != null) {
                String quizzName = read.substring(read.indexOf(",") + 1, read.indexOf(",", read.indexOf(",") + 1));
                if (quizz.compareTo(quizzName) == 0) {
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

    public static String createQuizzQuestionsForFile(String[] args) {
        String quizz_questions = "";
        for (int i = 4; i < args.length; i++) {
            String[] id_info = args[i].split(" ");
            String id = MetodeTema.removeUselessCharacters(id_info[1]);
            quizz_questions += id + ";";
            if (i == args.length - 1) {
                quizz_questions = quizz_questions.substring(0, quizz_questions.length() - 1);
            }
        }
        return quizz_questions;
    }

    public static String writeQuizzesToFile(String[] args, String path, Quizz quizz) {

        try {
            File file = new File(path);
            FileWriter fileWriter = new FileWriter(file, true);
            char[] csvFlags = {CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.NO_ESCAPE_CHARACTER};
            CSVWriter csvWriter = new CSVWriter(fileWriter, ',', csvFlags[0], csvFlags[1], "\n");

            int readQuestionsReturn = Quizz.readQuizzesFromFile(path, quizz);
            if (readQuestionsReturn == -1) {
                String[] header = {"identifier", "name", "questions", "creator", "completed"};
                csvWriter.writeNext(header);
            }
            if (readQuestionsReturn == -2) {
                csvWriter.close();
                fileWriter.close();
                return "{ 'status' : 'error', 'message' : 'Quizz name already exists'}";
            }
            String quizz_questions = Quizz.createQuizzQuestionsForFile(args);
            String[] questionInfo = {String.valueOf(quizz.getIdentifier()), quizz.getName(),
                                     quizz_questions, quizz.getCreator().getUsername(),
                                     Boolean.toString(quizz.checkCompleted())};

            csvWriter.writeNext(questionInfo);

            csvWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getQuizzIdentifier(String path, Quizz quizz) {
        try {
            File file = new File(path);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String read = br.readLine();
            if (read == null) {
                br.close();
                fr.close();
                return -1;
            }
            read = br.readLine();
            while (read != null) {

                String quizzName = read.substring(read.indexOf(",") + 1, read.indexOf(",", read.indexOf(",") + 1));
                if (quizz.compareTo(quizzName) == 0) {
                    int identifier = Integer.parseInt(read.substring(0, read.indexOf(",")));
                    br.close();
                    fr.close();
                    return identifier;
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

    public static String getAllQuizzes(String path) {
        String all_quizzes = "";
        try {
            File file = new File(path);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String read = br.readLine();
            if (read == null) {
                br.close();
                fr.close();
                return null;
            }
            read = br.readLine();
            while (read != null) {
                String[] quizz_info = read.split(",");
                String identifier = quizz_info[0];
                String name = quizz_info[1];
                String completed = MetodeTema.turnBooleanStringValueIntoCapital(quizz_info[4]);
                all_quizzes += "{\"quizz_id\"" + " : " + "\"" + identifier + "\"" +
                               ", " + "\"" + "quizz_name" + "\"" + " : "
                               + "\"" + name + "\"" + ", " + "\"" + "is_completed" + "\"" +
                               " : " + "\"" + completed + "\"}" + ", ";
                read = br.readLine();
            }
            all_quizzes = all_quizzes.substring(0, all_quizzes.length() - 2);
            br.close();
            fr.close();
            return all_quizzes;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getQuizzDetailsById(String[] args) {
        String quizz_details = "[";
        String path_to_quizzes = "src/main/java/com/example/project/quizzes.csv";
        String path_to_questions = "src/main/java/com/example/project/questions.csv";
        try {
            File file = new File(path_to_quizzes);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String read = br.readLine();
            if (read == null) {
                br.close();
                fr.close();
                return null;
            }
            read = br.readLine();
            while (read != null) {
                String[] quizz_info = read.split(",");
                String identifier = quizz_info[0];
                if (identifier.equals(MetodeTema.removeUselessCharacters(args[3].split(" ")[1]))) {
                    String[] question_ids = quizz_info[2].split(";");
                    for(int i = 0 ;i < question_ids.length; i++) {
                        String question_details = Question.getQuestionDetailsById(path_to_questions, question_ids[i]);
                        quizz_details += question_details + ", ";
                    }
                }
                read = br.readLine();
            }
            br.close();
            fr.close();
            if(quizz_details.length() > 1) {
                quizz_details = quizz_details.substring(0, quizz_details.length() - 2);
            }
            quizz_details += "]";
            return quizz_details;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int calculateQuizzScore(String[] args) {
        String path_to_quizzes = "src/main/java/com/example/project/quizzes.csv";
        try {
            File file = new File(path_to_quizzes);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String read = br.readLine();
            if (read == null) {
                br.close();
                fr.close();
                return -1;
            }
            read = br.readLine();
            double score = 0.00;
            while (read != null) {
                String[] quizz_info = read.split(",");
                String quizz_identifier = quizz_info[0];
                String[] question_ids = quizz_info[2].split(";");
                double scoreIncrement = 100.00 / question_ids.length;
                if(quizz_identifier.equals(MetodeTema.removeUselessCharacters(args[3].split(" ")[1]))) {
                    ArrayList<Question.Answer> answersProvided = new ArrayList<>();
                    for(int i = 4; i < args.length; i++) {
                        String answer = MetodeTema.removeUselessCharacters(args[i].split(" ")[1]);
                        Question.Answer givenAnswer = new Question.Answer(Integer.parseInt(answer));
                        answersProvided.add(givenAnswer);
                    }
                    for(int j = 0; j < question_ids.length; j++) {
                        ArrayList<Question.Answer> allAnswers = Question.getAnswers(question_ids[j]);
                        assert allAnswers != null;
                        double score_increment_per_question = scoreIncrement / allAnswers.size();
                        int[] results = Question.Answer.compareAnswers(answersProvided, allAnswers);
                        if(results[2] == allAnswers.size()) {
                            continue;
                        }
                        if(results[0] == allAnswers.size()) {
                            score += scoreIncrement;
                            continue;
                        }
                        if(results[1] == allAnswers.size()){
                            score -= scoreIncrement;
                            continue;
                        }
                        score += score_increment_per_question * results[0];
                        score -= score_increment_per_question * results[1];
                    }
                }
                read = br.readLine();
                br.close();
                fr.close();
            }
            if(score < 0.00) {
                score = 0.00;
            }
            return (int)Math.round(score);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void deleteQuizzById(String[] args, User user) {
        String path_to_quizzes = "src/main/java/com/example/project/quizzes.csv";
        try {
            File file = new File(path_to_quizzes);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String read = br.readLine();
            if (read == null) {
                br.close();
                fr.close();
                return;
            }
            read = br.readLine();
            String new_file = "";
            while (read != null) {
                String[] quizz_info = read.split(",");
                String identifier = quizz_info[0];
                String creator = quizz_info[3];
                if (identifier.equals(MetodeTema.removeUselessCharacters(args[3].split(" ")[1]))
                        && user.getUsername().equals(creator)) {

                    read = br.readLine();
                    continue;
                }
                new_file += read + "\n";
                read = br.readLine();
            }
            br.close();
            fr.close();
            FileWriter fw = new FileWriter(file);
            fw.write(new_file);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String markQuizzAsCompleted(String[] args, User user) {
        String path_to_quizzes = "src/main/java/com/example/project/quizzes.csv";
        String new_content = "";
        try {
            File file = new File(path_to_quizzes);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String read = br.readLine();
            if (read == null) {
                br.close();
                fr.close();
                return null;
            }
            read = br.readLine();
            while (read != null) {
                String[] quizz_info = read.split(",");
                String identifier = quizz_info[0];
                String creator = quizz_info[3];
                new_content += read + "\n";
                if (identifier.equals(MetodeTema.removeUselessCharacters(args[3].split(" ")[1]))
                        && user.getUsername().compareTo(creator) == 0) {
                    br.close();
                    fr.close();
                    return "{ 'status' : 'error', 'message' : 'You cannot answer your own quizz' }";
                }
                if (identifier.equals(MetodeTema.removeUselessCharacters(args[3].split(" ")[1]))
                        && user.getUsername().compareTo(creator) != 0) {
                    new_content = new_content.replace("false", "true");
                }
                read = br.readLine();
            }
            FileWriter fw = new FileWriter(file);
            fw.write(new_content);
            fw.close();
            br.close();
            fr.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getQuizzNameById(String id) {
        String path_to_quizzes = "src/main/java/com/example/project/quizzes.csv";
        try {
            File file = new File(path_to_quizzes);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String read = br.readLine();
            if (read == null) {
                br.close();
                fr.close();
                return null;
            }
            read = br.readLine();
            while (read != null) {
                String[] quizz_info = read.split(",");
                String identifier = quizz_info[0];
                String name = quizz_info[1];
                if (identifier.equals(id)) {
                    br.close();
                    fr.close();
                    return name;
                }
                read = br.readLine();
            }
            br.close();
            fr.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
