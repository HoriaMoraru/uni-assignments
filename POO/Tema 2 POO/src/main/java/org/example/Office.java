package org.example;

import java.time.LocalDateTime;
import java.util.*;

public class Office<G extends User> {

    /* The reason why i chose a tree set over a priority queue is because after the user withdraws an application
    i want the collection to remain sorted for printing , which for a priority queue might not always be the case */
    private final NavigableSet<Application> waitingApplications;
    /* Using a hash map for PublicOfficials because and i want to be able to access them in O(1) time by
    hashing them by their name . This produces the same hash collisions as in ManagementPrimarie  */
    private final Map< String, PublicOfficial<G> > publicOfficials;

    public Office() {
        this.waitingApplications = new TreeSet<>(new ApplicationComparator());
        this.publicOfficials = new HashMap<>();
    }

    static class ApplicationComparator implements Comparator<Application> {

        @Override
        public int compare(Application app1, Application app2) {
            if (app1.getPriority() == app2.getPriority()) {
                return app1.getDate().compareTo(app2.getDate());
            }
            return app2.getPriority() - app1.getPriority();
        }
    }

    public PublicOfficial<G> getPublicOfficialFromDataBase(String name) {
        return publicOfficials.get(name);
    }

    public void addPublicOfficial(PublicOfficial<G> publicOfficial) {
        try {
            if (publicOfficial != null) {
                if (publicOfficials.containsKey(publicOfficial.getName())) {
                    throw new IllegalStateException("The public official already exists in the database!");
                } else {
                    publicOfficials.put(publicOfficial.getName(), publicOfficial);
                }
            }
        }
        catch (IllegalStateException ex) {
            ex.printStackTrace();
        }
    }

    public void addApplication(Application application) {
        if (application == null)
            return;

        waitingApplications.add(application);
    }

    public void withdrawApplication(LocalDateTime date) {
        if (date == null || waitingApplications.isEmpty())
            return;

        waitingApplications.removeIf(app -> app.getDate().equals(date));
    }

    public Application waitingPollFirst() {
        return waitingApplications.pollFirst();
    }

    public String printApplications() {

        final StringBuilder sb = new StringBuilder();
        for (Application application : waitingApplications) {
            //Also add priority when printing applications in Office Class
            sb.append(application.getPriority()).append(" - ");
            sb.append(application).append("\n");
            }
        return sb.toString();
    }
}