package com.example.project;

import java.io.File;

public abstract class MetodeTema extends HelperMethods {

    public static void createUserCommand(String[] args) {
        if(findCommandInArgs(args, "-u") == null) {
            System.out.println("{ 'status' : 'error', 'message' : 'Please provide username'}");
            return;
        }
        if(findCommandInArgs(args, "-p") == null) {
            System.out.println("{ 'status' : 'error', 'message' : 'Please provide password'}");
            return;
        }

        String[] username = args[1].split(" ");
        String[] password = args[2].split(" ");

        String path = "src/main/java/com/example/project/users.csv";
        username[1] = MetodeTema.removeUselessCharacters(username[1]);
        password[1] = MetodeTema.removeUselessCharacters(password[1]);
        User user = new User(username[1], password[1]);

        String writeUsersError = User.writeUsersToFile(path, user);
        if(writeUsersError != null) {
            System.out.println(writeUsersError);
            return;
        }
        System.out.println("{ 'status' : 'ok', 'message' : 'User created successfully'}");
    }

    public static void createQuestionCommand(String[] args){
        String user_login_failed_error = User.checkLogin(args);
        if(user_login_failed_error != null) {
            System.out.println(user_login_failed_error);
            return;
        }
        if(findCommandInArgs(args, "-text") == null) {
            System.out.println("{ 'status' : 'error', 'message' : 'No question text provided'}");
            return;
        }
        String[] text_info = args[3].split(" ", 2);
        String text = MetodeTema.removeUselessCharacters(text_info[1]);
        String[] type_info = args[4].split(" ");
        String type = MetodeTema.removeUselessCharacters(type_info[1]);

        Question question = new Question(text, type);

        String addAnswersError = Question.addAnswers(args, question);
        if(addAnswersError != null) {
            System.out.println(addAnswersError);
            return;
        }
        if(question.answers.size() == 0) {
            System.out.println("{ 'status' : 'error', 'message' : 'No answer provided'}");
            return;
        }
        if(question.answers.size() == 1) {
            System.out.println("{ 'status' : 'error', 'message' : 'Only one answer provided'}");
            return;
        }
        if(question.answers.size() > 5) {
            System.out.println("{ 'status' : 'error', 'message' : 'More than 5 answers were submited'}");
            return;
        }
        String path_to_question = "src/main/java/com/example/project/questions.csv";
        String writeQuestionsError = Question.writeQuestionsToFile(path_to_question, question);
        if(writeQuestionsError != null) {
            System.out.println(writeQuestionsError);
            return;
        }
        if(Question.Answer.numberOfCorectAnswers(question.answers) > 1 && question.getType().compareTo("single") == 0) {
            System.out.println("{ 'status' : 'error', 'message' : 'Single correct answer question has more than one correct answer'}");
            return;
        }
        if(Question.Answer.numberOfCorectAnswers(question.answers) == 0) {
            System.out.println("{ 'status' : 'error', 'message' : 'No correct answer provided'}");
            return;
        }
        if(Question.Answer.checkDuplicateAnswers(question.answers)) {
            System.out.println("{ 'status' : 'error', 'message' : 'Same answer provided more than once'}");
            return;
        }

        String path_to_answers = "src/main/java/com/example/project/answers.csv";

        Question.Answer.writeAnswersToFile(path_to_answers, question);

        System.out.println("{ 'status' : 'ok', 'message' : 'Question added successfully'}");
    }

    public static void getQuestionIDByTextCommand(String[] args) {
        String user_login_failed_error = User.checkLogin(args);
        if(user_login_failed_error != null) {
            System.out.println(user_login_failed_error);
            return;
        }
        String[] text_info = args[3].split(" ", 2);
        String text = MetodeTema.removeUselessCharacters(text_info[1]);

        Question question = new Question(text);
        String path_to_question = "src/main/java/com/example/project/questions.csv";
        if(Question.readQuestionsFromFile(path_to_question, question) == 0) {
            System.out.println("{ 'status' : 'error', 'message' : 'Question does not exist'}");
            return;
        }
        int question_id = Question.getQuestionIdentifier(path_to_question, question);
        System.out.println("{ 'status' : 'ok', 'message' :" + "'" + question_id + "'}");
    }

    public static void getAllQuestionsCommand(String[] args) {
        String user_login_failed_error = User.checkLogin(args);
        if(user_login_failed_error != null) {
            System.out.println(user_login_failed_error);
            return;
        }
        String path_to_question = "src/main/java/com/example/project/questions.csv";
        String questions = Question.getAllQuestions(path_to_question);
        if(questions == null) {
            System.out.println("{ 'status' : 'error', 'message' : 'No questions found'}");
            return;
        }
        System.out.println("{ 'status' : 'ok', 'message' :'[" + questions + "]'}");
    }

    public static void createQuizzCommand(String[] args) {
        String user_login_failed_error = User.checkLogin(args);
        if (user_login_failed_error != null) {
            System.out.println(user_login_failed_error);
            return;
        }
        if (findCommandInArgs(args, "-name") == null) {
            System.out.println("{ 'status' : 'error', 'message' : 'No quizz name provided'}");
            return;
        }
        String[] name_info = args[3].split(" ", 2);
        String name = MetodeTema.removeUselessCharacters(name_info[1]);

        String path_to_question = "src/main/java/com/example/project/questions.csv";
        String[] username = args[1].split(" ");
        String[] password = args[2].split(" ");

        username[1] = MetodeTema.removeUselessCharacters(username[1]);
        password[1] = MetodeTema.removeUselessCharacters(password[1]);
        User user = new User(username[1], password[1]);

        Quizz quizz = new Quizz(name, user);

        int total_number_of_questions = (args.length - 4) / 2;
        int current_question_index = Quizz.readQuestionsForQuizz(args,path_to_question, quizz);
        if(current_question_index  <= total_number_of_questions) {
            current_question_index++;
            System.out.println("{ 'status' : 'error', 'message' : 'Question ID for question " + current_question_index + " does not exist'}");
            return;
        }

        if(quizz.questions.size() == 0) {
            System.out.println("{ 'status' : 'error', 'message' : 'No questions provided'}");
            return;
        }
        if(quizz.questions.size() > 10) {
            System.out.println("{ 'status' : 'error', 'message' : 'Quizz has more than 10 questions'}");
            return;
        }
        String path_to_quizz = "src/main/java/com/example/project/quizzes.csv";

        String writeQuizzError = Quizz.writeQuizzesToFile(args, path_to_quizz, quizz);
            if(writeQuizzError != null) {
            System.out.println(writeQuizzError);
            return;
        }
        System.out.println("{ 'status' : 'ok', 'message' : 'Quizz added succesfully'}");
    }

    public static void getQuizzByName(String[] args) {
        String user_login_failed_error = User.checkLogin(args);
        if (user_login_failed_error != null) {
            System.out.println(user_login_failed_error);
            return;
        }
        if (findCommandInArgs(args, "-name") == null) {
            System.out.println("{ 'status' : 'error', 'message' : 'No quizz name provided'}");
            return;
        }
        String[] name_info = args[3].split(" ", 2);
        String name = MetodeTema.removeUselessCharacters(name_info[1]);

        String path_to_quizz = "src/main/java/com/example/project/quizzes.csv";
        Quizz quizz = new Quizz(name);
        if(Quizz.readQuizzesFromFile(path_to_quizz, quizz) == 0) {
            System.out.println("{ 'status' : 'error', 'message' : 'Quizz does not exist'}");
            return;
        }
        System.out.println("{ 'status' : 'ok', 'message' : '" + Quizz.getQuizzIdentifier(path_to_quizz, quizz) + "'}");
    }

    public static void getAllQuizzesCommand(String[] args) {
        String user_login_failed_error = User.checkLogin(args);
        if (user_login_failed_error != null) {
            System.out.println(user_login_failed_error);
            return;
        }
        String path_to_quizz = "src/main/java/com/example/project/quizzes.csv";
        String quizzes = Quizz.getAllQuizzes(path_to_quizz);
        if(quizzes == null) {
            System.out.println("{ 'status' : 'error', 'message' : 'No quizzes found'}");
            return;
        }
        System.out.println("{ 'status' : 'ok', 'message' : '[" + quizzes + "]'}");
    }

    public static void getQuizzDetailsByIdCommand(String[] args) {
        String user_login_failed_error = User.checkLogin(args);
        if (user_login_failed_error != null) {
            System.out.println(user_login_failed_error);
            return;
        }
        if (findCommandInArgs(args, "-id") == null) {
            System.out.println("{ 'status' : 'error', 'message' : 'No quizz id provided'}");
            return;
        }

        String path_to_quizz = "src/main/java/com/example/project/quizzes.csv";
        if(Quizz.getAllQuizzes(path_to_quizz) == null) {
            System.out.println("{ 'status' : 'error', 'message' : 'Quizz does not exist'}");
            return;
        }
        System.out.println("{ 'status' : 'ok', 'message' : '" + Quizz.getQuizzDetailsById(args) + "'}");
    }

    public static void submitQuizzCommand(String[] args) {
        String user_login_failed_error = User.checkLogin(args);
        if (user_login_failed_error != null) {
            System.out.println(user_login_failed_error);
            return;
        }
        if (findCommandInArgs(args, "-quiz-id") == null) {
            System.out.println("{ 'status' : 'error', 'message' : 'No quizz identifier was provided'}");
            return;
        }

        String[] username = args[1].split(" ");
        String[] password = args[2].split(" ");

        username[1] = MetodeTema.removeUselessCharacters(username[1]);
        password[1] = MetodeTema.removeUselessCharacters(password[1]);
        User user = new User(username[1], password[1]);

        String[] id_info = args[3].split(" ", 2);
        String id = MetodeTema.removeUselessCharacters(id_info[1]);

        if(Quizz.getQuizzDetailsById(args) == null || Quizz.getQuizzDetailsById(args).compareTo("[]") == 0) {
            System.out.println("{ 'status' : 'error', 'message' : 'No quiz was found'}");
            return;
        }

        String quizz_name = Quizz.getQuizzNameById(id);
        int score = Quizz.calculateQuizzScore(args);

        Solution solution = new Solution(score, id, user, quizz_name);

        String path_to_solution = "src/main/java/com/example/project/solutions.csv";

        String markCompleteError = Quizz.markQuizzAsCompleted(args, user);
        if(markCompleteError != null) {
            System.out.println(markCompleteError);
            return;
        }

        String writeSolutionError = Solution.writeSolutionsToFile(path_to_solution, solution);
        if(writeSolutionError != null) {
            System.out.println(writeSolutionError);
            return;
        }

        System.out.println("{ 'status' : 'ok', 'message' : '" + score + " points' }");
    }

    public static void deleteQuizzByIdCommand(String[] args) {
        String user_login_failed_error = User.checkLogin(args);
        if (user_login_failed_error != null) {
            System.out.println(user_login_failed_error);
            return;
        }
        if (findCommandInArgs(args, "-id") == null) {
            System.out.println("{ 'status' : 'error', 'message' : 'No quizz identifier was provided'}");
            return;
        }

        String[] username = args[1].split(" ");
        String[] password = args[2].split(" ");

        username[1] = MetodeTema.removeUselessCharacters(username[1]);
        password[1] = MetodeTema.removeUselessCharacters(password[1]);
        User user = new User(username[1], password[1]);

        if(Quizz.getQuizzDetailsById(args).compareTo("[]") == 0 || Quizz.getQuizzDetailsById(args) == null) {
            System.out.println("{ 'status' : 'error', 'message' : 'No quiz was found'}");
            return;
        }
        Quizz.deleteQuizzById(args, user);
        Quizz.lowerNumberOfQuizzes();
        System.out.println("{ 'status' : 'ok', 'message' : 'Quizz deleted successfully'}");
    }

    public static void getMySolutionsCommand(String[] args) {
        String user_login_failed_error = User.checkLogin(args);
        if (user_login_failed_error != null) {
            System.out.println(user_login_failed_error);
            return;
        }

        String[] username = args[1].split(" ");
        String[] password = args[2].split(" ");

        username[1] = MetodeTema.removeUselessCharacters(username[1]);
        password[1] = MetodeTema.removeUselessCharacters(password[1]);
        User user = new User(username[1], password[1]);

        String path_to_solutions = "src/main/java/com/example/project/solutions.csv";

        String mySolutions = Solution.getMySolutions(path_to_solutions, user);
        //Am acoperit si cazul in care nu exista solutii pentru userul curent
        if(mySolutions == null) {
            System.out.println("{ 'status' : 'error', 'message' : 'No solutions found'}");
            return;
        }
        System.out.println("{ 'status' : 'ok', 'message' : '" + mySolutions +  "'}");
    }

    public static void cleanUpCommand() {
        String path_to_users = "src/main/java/com/example/project/users.csv";
        File file_u = new File(path_to_users);
        if(!file_u.delete()) {
            System.out.println("{ 'status' : 'error', 'message' : 'Could not delete users.csv'}");
        }
        String path_to_questions = "src/main/java/com/example/project/questions.csv";
        File file_q = new File(path_to_questions);
        if(!file_q.delete()) {
            System.out.println("{ 'status' : 'error', 'message' : 'Could not delete questions.csv'}");
        }
        Question.resetNumber_of_questions();
        String path_to_quizzes = "src/main/java/com/example/project/quizzes.csv";
        File file_qu = new File(path_to_quizzes);
        if(!file_qu.delete()) {
            System.out.println("{ 'status' : 'error', 'message' : 'Could not delete quizzes.csv'}");
        }
        Quizz.resetNumber_of_quizzes();
        String path_to_answers = "src/main/java/com/example/project/answers.csv";
        File file_a = new File(path_to_answers);
        if(!file_a.delete()) {
            System.out.println("{ 'status' : 'error', 'message' : 'Could not delete answers.csv'}");
        }
        Question.Answer.resetNumber_of_answers();
        String path_to_solutions = "src/main/java/com/example/project/solutions.csv";
        File file_s = new File(path_to_solutions);
        if(!file_s.delete()) {
            System.out.println("{ 'status' : 'error', 'message' : 'Could not delete solutions.csv'}");
        }
        System.out.println("{ 'status' : 'ok', 'message' : 'Cleanup finished successfully'}");
    }
}