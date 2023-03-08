package com.example.project;

public class Tema1 {

	public static void main(final String[] args){

		if(args == null) {
			System.out.println("Hello world!");
			return;
		}

		if (args[0].compareTo("-create-user") == 0) {
			MetodeTema.createUserCommand(args);
		}

		if (args[0].compareTo("-create-question") == 0) {
			MetodeTema.createQuestionCommand(args);
		}

		if (args[0].compareTo("-get-question-id-by-text") == 0) {
			MetodeTema.getQuestionIDByTextCommand(args);
		}

		if (args[0].compareTo("-get-all-questions") == 0) {
			MetodeTema.getAllQuestionsCommand(args);
		}

		if (args[0].compareTo("-create-quizz") == 0) {
			MetodeTema.createQuizzCommand(args);
		}

		if (args[0].compareTo("-get-quizz-by-name") == 0) {
			MetodeTema.getQuizzByName(args);
		}

		if (args[0].compareTo("-get-all-quizzes") == 0) {
			MetodeTema.getAllQuizzesCommand(args);
		}

		if (args[0].compareTo("-get-quizz-details-by-id") == 0) {
			MetodeTema.getQuizzDetailsByIdCommand(args);
		}

		if (args[0].compareTo("-submit-quizz") == 0) {
			MetodeTema.submitQuizzCommand(args);
		}

		if (args[0].compareTo("-delete-quizz-by-id") == 0) {
			MetodeTema.deleteQuizzByIdCommand(args);
		}

		if (args[0].compareTo("-get-my-solutions") == 0) {
			MetodeTema.getMySolutionsCommand(args);
		}
		if (args[0].compareTo("-cleanup-all") == 0) {
			MetodeTema.cleanUpCommand();
		}

	}
}
