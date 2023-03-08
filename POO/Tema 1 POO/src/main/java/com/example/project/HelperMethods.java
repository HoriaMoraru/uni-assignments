package com.example.project;

public abstract class HelperMethods {

    public static String removeUselessCharacters(String s) {
        if(s == null) {
            return null;
        }
        s = s.substring(1);
        s = s.substring(0, s.length() - 1);
        return s;
    }

    public static String findCommandInArgs(String[] args, String stringToFind) {
        for(int i = 0; i < args.length; i++) {
            String[] command = args[i].split(" ");
            if(command[0].compareTo(stringToFind) == 0) {
                return command[0];
            }
        }
        return null;
    }

    public static boolean turnStringIntoBoolean(String string) {
        return string.compareTo("1") == 0;
    }

    public static String turnBooleanStringValueIntoCapital(String string) {
        if(string.compareTo("true") == 0) {
            return "True";
        }
        return "False";
    }
}
