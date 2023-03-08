package org.example;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PublicOfficial<G extends User> {

    private final String name;
    private final String path;

    public PublicOfficial(String name) {
        this.name = name;
        this.path = "src/main/resources/output/"
                    + "functionar_" + this.name + ".txt";
    }

    public String getName() {
        return this.name;
    }

    public String getPath() {
        return this.path;
    }

    public String resolveApplication(Office<G> office, Map< User, List<Application> > dataBase) {

        /* I am only removing the first element, being a tree set, the first element is the one with the highest priority
        and the earliest date . The first element return by the pollLast method is then added to that specific user
        resolved applications set */
        Application resovledApp =  office.waitingPollFirst();
        if (resovledApp == null)
            return null;

        LocalDateTime resovledAppDate = resovledApp.getDate();
        /* Retrieve the user from the database by filtering the map and searching through all the applications
        and returning the user that has the application with the same date as the resolved application */

        User user = dataBase.entrySet().stream()
                            .filter(entry -> entry.getValue().stream()
                                                  .filter(Objects::nonNull) //Check if the application is not null
                                                  .anyMatch(app -> app.getDate().equals(resovledAppDate)
                                                                && app.getContent().equals(resovledApp.getContent())))
                            .map(Map.Entry::getKey)
                            .findFirst()
                            .orElse(null);
        if (user == null)
            return null;

        user.addResolvedApplication(resovledApp);
        //Using the withdraw method instead of pollFirst because the user application set is ordered by date not priority
        user.withdrawApplication(resovledAppDate);
        return resovledApp.toStringDate() + " - " + user.getName() + "\n";
    }
}
