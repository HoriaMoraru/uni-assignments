package org.example;

import java.time.LocalDateTime;
import java.util.*;

public interface User {

    enum type {
        PERSOANA,
        ANGAJAT,
        PENSIONAR,
        ELEV,
        ENTITATE_JURIDICA
    }

    class UserApplicationComparator implements Comparator<Application> {

        @Override
        public int compare(Application app1, Application app2) {
            return app1.getDate().compareTo(app2.getDate());
        }
    }

     static User returnUser(User.type type, String name, String secondName) {
        try {
            switch (type) {
                case PERSOANA : {
                    return new Person(name);
                }
                case ANGAJAT : {
                    return new Employee(name, secondName);
                }
                case PENSIONAR : {
                    return new Pensioner(name);
                }
                case ELEV : {
                    return new Student(name, secondName);
                }
                case ENTITATE_JURIDICA : {
                    return new LegalEntity(name, secondName);
                }
                default : throw new IllegalArgumentException("The type of user is not valid!");
            }
        }
        catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    String getName();
    User.type getType();
    /* Function that creates an application , it returns an Optional because it might not be possible to
    create an application so then , it would return an error String . */
    <G> Optional<G> createApplication(Application.type type, LocalDateTime date, int priority);
    void addApplication(Application application);
    void addResolvedApplication(Application application);
    void withdrawApplication(LocalDateTime date);
    String printWaitingApplications();
    String printResolvedApplications();
}
