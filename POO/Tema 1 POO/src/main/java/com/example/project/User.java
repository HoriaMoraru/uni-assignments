package com.example.project;

import com.opencsv.CSVWriter;

import java.io.*;

public class User {

    private final String username;
    private final String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public static int readUsersFromFile(String path, User user){
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

                String username = read.substring(0, read.indexOf(","));
                if(user.getUsername().compareTo(username) == 0 ) {
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

    public static String writeUsersToFile(String path, User user) {
        try {
            File file = new File(path);
            FileWriter fileWriter = new FileWriter(file, true);
            char[] csvFlags = {CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.NO_ESCAPE_CHARACTER};
            CSVWriter csvWriter = new CSVWriter(fileWriter, ',',csvFlags[0], csvFlags[1], "\n");

            int readUsersReturn = User.readUsersFromFile(path, user);
            if(readUsersReturn == -1) {
                String[] header = {"username", "password"};
                csvWriter.writeNext(header);
            }
            if(readUsersReturn == -2) {
                csvWriter.close();
                fileWriter.close();
                return "{ 'status' : 'error', 'message' : 'User already exists'}";
            }
            String[] userInfo = {user.getUsername(),user.getPassword()};
            csvWriter.writeNext(userInfo);

            csvWriter.close();
            fileWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean checkCredentials(String path, User user) {
        try {
            File file = new File(path);
            FileReader fr = new FileReader(file);
            BufferedReader br_up = new BufferedReader(fr);
            String read = br_up.readLine();
            if(read == null) {
                br_up.close();
                fr.close();
                return false;
            }
            read = br_up.readLine();
            while (read != null) {

                String username = read.substring(0, read.indexOf(","));
                String password = read.substring(read.indexOf(",") + 1);
                if(user.getUsername().compareTo(username) == 0 && user.getPassword().compareTo(password) == 0) {
                    br_up.close();
                    fr.close();
                    return true;
                }
                read = br_up.readLine();
            }
            br_up.close();
            fr.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String checkLogin(String[] args) {
        if(MetodeTema.findCommandInArgs(args, "-u") == null || MetodeTema.findCommandInArgs(args, "-p") == null) {
            return "{ 'status' : 'error', 'message' : 'You need to be authenticated'}";
        }
        String[] username = args[1].split(" ");
        String[] password = args[2].split(" ");

        String path_to_users = "src/main/java/com/example/project/users.csv";

        username[1] = MetodeTema.removeUselessCharacters(username[1]);
        password[1] = MetodeTema.removeUselessCharacters(password[1]);
        User user = new User(username[1], password[1]);
        if(!User.checkCredentials(path_to_users, user)) {
            return  "{ 'status' : 'error', 'message' : 'Login failed'}";
        }
        return null;
    }

}
