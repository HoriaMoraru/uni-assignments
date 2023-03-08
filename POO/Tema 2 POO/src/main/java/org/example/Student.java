package org.example;

import java.time.LocalDateTime;
import java.util.*;

public class Student implements User {

    private final String name;
    /* The reason for using tree sets instead of priority queues for both waiting and resolved applications
    is the same as in the Office class. */
    private final String schoolName;
    private final NavigableSet<Application> waitingApplications;
    private final NavigableSet<Application> resolvedApplications;

    public Student(String name, String schoolName) {
        this.name = name;
        this.schoolName = schoolName;
        this.waitingApplications = new TreeSet<>(new UserApplicationComparator());
        this.resolvedApplications = new TreeSet<>(new UserApplicationComparator());
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public User.type getType() {
        return User.type.ELEV;
    }

    @Override
    public <G> Optional<G> createApplication(Application.type type, LocalDateTime date, int priority) {

        final String typeString = String.valueOf(type).toLowerCase().replace("_", " ");
        try {
            if (priority < 1 || priority > 5) {
                throw new IllegalArgumentException("The priority must be between 1 and 5!");
            }
            if (EnumSet.of(Application.type.INLOCUIRE_BULETIN,
                           Application.type.INLOCUIRE_CARNET_DE_ELEV)
                        .contains(type)) {

                final String applicationContent = "Subsemnatul " + this.name + ", elev la scoala " + this.schoolName
                        + ", va rog sa-mi aprobati urmatoarea solicitare: "
                        + typeString;

                return Optional.of((G) new Application(applicationContent, date, priority));
            }
        }
        catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return Optional.of((G) ("Utilizatorul de tip " + getType().toString().toLowerCase()
                + " nu poate inainta o cerere de tip " + typeString + "\n"));
    }

    @Override
    public void addApplication(Application application) {
        if (application == null) {
            return;
        }
        waitingApplications.add(application);
    }

    @Override
    public void addResolvedApplication(Application application) {
        if (application == null) {
            return;
        }
        resolvedApplications.add(application);
    }

    @Override
    public void withdrawApplication(LocalDateTime date) {
        if (date == null || waitingApplications.isEmpty())
            return;

        waitingApplications.removeIf(application -> application.getDate().equals(date));
    }

    @Override
    public String printWaitingApplications() {

        final StringBuilder sb = new StringBuilder();
        for (Application application : waitingApplications) {
            sb.append(application.toString()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String printResolvedApplications() {

        final StringBuilder sb = new StringBuilder();
        for (Application application : resolvedApplications) {
            sb.append(application.toString()).append("\n");
        }
        return sb.toString();
    }
}
