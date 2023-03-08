package org.example;

import java.time.LocalDateTime;
import java.util.*;

public class ManagementPrimarie {

    /* I used a hash map to store Users based on the String name key, so this would be O(1) complexity when
    retrieving an element from the hash map . But this method produces hash collisions when a type of user
    is of multiple types. Ex : Student and Employee. */
    private final Map<String, User> userDataBase;
    /* Also using a hash map to keep track of the applications made by every user by storing all the applications
    in a array list, because they don`t need to be sorted */
    private final Map< User, List<Application> > applicationDataBase;
    private final Office<Person> personOffice;
    private final Office<Employee> employeeOffice;
    private final Office<Pensioner> pensionerOffice;
    private final Office<Student> studentOffice;
    private final Office<LegalEntity> legalEntityOffice;

    public ManagementPrimarie() {
        this.personOffice = new Office<>();
        this.employeeOffice = new Office<>();
        this.pensionerOffice = new Office<>();
        this.studentOffice = new Office<>();
        this.legalEntityOffice = new Office<>();
        this.userDataBase = new HashMap<>();
        this.applicationDataBase = new HashMap<>();
    }

    public void addPublicOfficial(String fullCommand) {

        String[] commands = fullCommand.split("; ");
        User.type userType =
                User.type.valueOf(commands[1].toUpperCase()
                                             .replace(" ", "_"));
        String name = commands[2];
        switch (userType) {
            case PERSOANA : {
                PublicOfficial<Person> publicOfficial = new PublicOfficial<>(name);
                personOffice.addPublicOfficial(publicOfficial);
                break;
            }
            case ANGAJAT : {
                PublicOfficial<Employee> publicOfficial = new PublicOfficial<>(name);
                employeeOffice.addPublicOfficial(publicOfficial);
                break;
            }
            case PENSIONAR : {
                PublicOfficial<Pensioner> publicOfficial = new PublicOfficial<>(name);
                pensionerOffice.addPublicOfficial(publicOfficial);
                break;
            }
            case ELEV : {
                PublicOfficial<Student> publicOfficial = new PublicOfficial<>(name);
                studentOffice.addPublicOfficial(publicOfficial);
                break;
            }
            case ENTITATE_JURIDICA : {
                PublicOfficial<LegalEntity> publicOfficial = new PublicOfficial<>(name);
                legalEntityOffice.addPublicOfficial(publicOfficial);
                break;
            }
            default : throw new IllegalArgumentException("The type of user is not valid!");
        }
    }

    public void addUser(String fullCommand) {

        String[] commands = fullCommand.split("; ");
        User.type userType =
                User.type.valueOf(commands[1].toUpperCase()
                                             .replace(" ", "_"));
        String name = commands[2];
        //This can be either comapany name, school or representative name
        String secondName  = null;
        if (commands.length > 3) {
            secondName = commands[3];
        }
        User user = User.returnUser(userType, name, secondName);
        /* Handling hash collisions , so that the user is not added twice . This removes the possibility of
        having a user that can be of multiple types */
        try {
            if (user != null) {
                if (userDataBase.containsKey(name)) {
                    throw new IllegalStateException("The user already exists in the database!");
                }
                else {
                    userDataBase.put(name, user);
                }
            }
        }
       catch (IllegalStateException ex) {
            ex.printStackTrace();
       }
    }

    public void addApplicationToDataBase(User user, Application app) {

        if (app == null || user == null)
           return;

        if (applicationDataBase.containsKey(user)) {
            applicationDataBase.get(user).add(app);
        }
        else {
            List<Application> appList = new ArrayList<>();
            appList.add(app);
            applicationDataBase.put(user, appList);
        }

        user.addApplication(app);
    }

    public String newApplication(String fullCommand) {

        String[] commands = fullCommand.split("; ");
        String name = commands[1];
        Application.type applicationType =
                Application.type.valueOf(commands[2].toUpperCase()
                                                    .replace(" ", "_"));
        String dateString = commands[3];
        LocalDateTime date = FileOperations.getInstance().parseDateFromFile(dateString);
        int priority = Integer.parseInt(commands[4]);

        User user = userDataBase.get(name);

        if (user == null)
            return null;

        Optional<Object> result = user.createApplication(applicationType, date, priority);

        Application application = (Application) result.filter(obj -> obj instanceof Application)
                                                      .orElse(null);


        String errorString = (String) result.filter(obj -> obj instanceof String)
                                            .orElse(null);

        addApplicationToDataBase(user, application);

        switch (user.getType()) {
            case PERSOANA : {
                personOffice.addApplication(application);
                return errorString;
            }
            case ANGAJAT : {
                employeeOffice.addApplication(application);
                return errorString;
            }
            case PENSIONAR : {
                pensionerOffice.addApplication(application);
                return errorString;
            }
            case ELEV : {
                studentOffice.addApplication(application);
                return errorString;
            }
            case ENTITATE_JURIDICA : {
                legalEntityOffice.addApplication(application);
                return errorString;
            }
            default : throw new IllegalArgumentException("The type of user is not valid!");
        }
    }

    public void withdrawApplication(String fullCommand) {

        String[] commands = fullCommand.split("; ");
        String name = commands[1];
        String dateString = commands[2];
        LocalDateTime date = FileOperations.getInstance().parseDateFromFile(dateString);

        User user = userDataBase.get(name);

        if (user == null)
            return;

        user.withdrawApplication(date);

        switch (user.getType()) {
            case PERSOANA : {
                personOffice.withdrawApplication(date);
                break;
            }
            case ANGAJAT : {
                employeeOffice.withdrawApplication(date);
                break;
            }
            case PENSIONAR : {
                pensionerOffice.withdrawApplication(date);
                break;
            }
            case ELEV : {
                studentOffice.withdrawApplication(date);
                break;
            }
            case ENTITATE_JURIDICA : {
                legalEntityOffice.withdrawApplication(date);
                break;
            }
            default : throw new IllegalArgumentException("The type of user is not valid!");
        }
    }

    public void resolveApplication(String fullCommand) {

        String[] commands = fullCommand.split("; ");
        User.type userType =
                User.type.valueOf(commands[1].toUpperCase()
                         .replace(" ", "_"));
        String poName = commands[2];
        FileOperations fileOperations = FileOperations.getInstance();

        switch (userType) {
            case PERSOANA : {
                PublicOfficial<Person> publicOfficial = personOffice.getPublicOfficialFromDataBase(poName);
                fileOperations.writeToFile(
                        publicOfficial.getPath(), publicOfficial.resolveApplication(personOffice, applicationDataBase));
                break;
            }
            case ANGAJAT : {
                PublicOfficial<Employee> publicOfficial = employeeOffice.getPublicOfficialFromDataBase(poName);
                fileOperations.writeToFile(
                        publicOfficial.getPath(), publicOfficial.resolveApplication(employeeOffice, applicationDataBase));
                break;
            }
            case PENSIONAR : {
                PublicOfficial<Pensioner> publicOfficial = pensionerOffice.getPublicOfficialFromDataBase(poName);
                fileOperations.writeToFile(
                        publicOfficial.getPath(), publicOfficial.resolveApplication(pensionerOffice, applicationDataBase));
                break;
            }
            case ELEV : {
                PublicOfficial<Student> publicOfficial = studentOffice.getPublicOfficialFromDataBase(poName);
                fileOperations.writeToFile(
                        publicOfficial.getPath(), publicOfficial.resolveApplication(studentOffice, applicationDataBase));
                break;
            }
            case ENTITATE_JURIDICA : {
                PublicOfficial<LegalEntity> publicOfficial = legalEntityOffice.getPublicOfficialFromDataBase(poName);
                fileOperations.writeToFile(
                        publicOfficial.getPath(), publicOfficial.resolveApplication(legalEntityOffice, applicationDataBase));
                break;
            }
            default : throw new IllegalArgumentException("The type of user is not valid!");
        }
    }

    public String printOfficeApplications(String fullCommand) {

            String[] commands = fullCommand.split("; ");
            String type = commands[1];
            User.type userType =
                User.type.valueOf(commands[1].toUpperCase()
                                             .replace(" ", "_"));
            final String header = type + " - cereri in birou:" + "\n";
            try {
                switch (userType) {
                    case PERSOANA : {
                        return header + personOffice.printApplications();
                    }
                    case ANGAJAT : {
                        return header + employeeOffice.printApplications();
                    }
                    case PENSIONAR : {
                        return header + pensionerOffice.printApplications();
                    }
                    case ELEV : {
                        return header + studentOffice.printApplications();
                    }
                    case ENTITATE_JURIDICA : {
                        return header + legalEntityOffice.printApplications();
                    }
                    default : throw new IllegalArgumentException("The type of user is not valid!");
                }
            }
            catch (IllegalArgumentException ex) {
                ex.printStackTrace();
                return null;
            }
    }

    public String printUserWaitingApplication(String fullCommand) {

        String[] commands = fullCommand.split("; ");
        String name = commands[1];
        User user = userDataBase.get(name);

        if (user == null)
            return null;

        final String header = user.getName() + " - cereri in asteptare:" + "\n";
        return header + user.printWaitingApplications();
    }

    public String printUserResolvedApplication(String fullCommand) {

        String[] commands = fullCommand.split("; ");
        String name = commands[1];
        User user = userDataBase.get(name);

        if (user == null)
            return null;

        final String header = user.getName() + " - cereri in finalizate:" + "\n";
        return header + user.printResolvedApplications();
    }

    public static void main(String[] args) {

        ManagementPrimarie managementPrimarie = new ManagementPrimarie();
        FileOperations fileOperations = FileOperations.getInstance();
        if (args == null)
            return;

        final String directory = "src/main/resources/output/";
        final String inputFileName = "src/main/resources/input/" + args[0];
        final String outputFileName = "src/main/resources/output/" + args[0];

        fileOperations.cleanDirectory(directory);

        List<String> fullCommands = fileOperations.readCommandsFromFile(inputFileName);

        for (String fullCommand : fullCommands) {
            String command = fullCommand.split("; ")[0];
            try {
                switch (command) {
                    case "adauga_functionar" : {
                        managementPrimarie.addPublicOfficial(fullCommand);
                        break;
                    }
                    case "adauga_utilizator" : {
                        managementPrimarie.addUser(fullCommand);
                        break;
                    }
                    case "cerere_noua" : {
                        fileOperations.writeToFile(outputFileName, managementPrimarie.newApplication(fullCommand));
                        break;
                    }
                    case "retrage_cerere" : {
                        managementPrimarie.withdrawApplication(fullCommand);
                        break;
                    }
                    case "rezolva_cerere" : {
                        managementPrimarie.resolveApplication(fullCommand);
                        break;
                    }
                    case "afiseaza_cereri_in_asteptare" : {
                        fileOperations.writeToFile(outputFileName, managementPrimarie.printUserWaitingApplication(fullCommand));
                        break;
                    }
                    case "afiseaza_cereri_finalizate" : {
                        fileOperations.writeToFile(outputFileName, managementPrimarie.printUserResolvedApplication(fullCommand));
                        break;
                    }
                    case "afiseaza_cereri" : {
                        fileOperations.writeToFile(outputFileName, managementPrimarie.printOfficeApplications(fullCommand));
                        break;
                    }
                    default : throw new IllegalArgumentException("The command is not valid!");
                }
            }
            catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        }
    }
}