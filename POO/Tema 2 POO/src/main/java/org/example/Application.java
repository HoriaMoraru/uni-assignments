package org.example;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

public class Application {

    private final String content;
    private final LocalDateTime date;
    //Priority can have values between 1-5, 5 beeing the most urgent priority.
    private final int priority;
    public enum type {
        INLOCUIRE_BULETIN,
        INREGISTRARE_VENIT_SALARIAL,
        INLOCUIRE_CARNET_DE_SOFER,
        INLOCUIRE_CARNET_DE_ELEV,
        CREARE_ACT_CONSTITUTIV,
        REINNOIRE_AUTORIZATIE,
        INREGISTRARE_CUPOANE_DE_PENSIE
    }

    public Application(String content, LocalDateTime date, int priority) {
        this.content = content;
        this.date = date;
        this.priority = priority;
    }

    public LocalDateTime getDate() {
        return this.date;
    }

    public int getPriority() {
        return this.priority;
    }

    public String getContent() {
        return this.content;
    }

    //Formats the date to a string as shown in the reference output.
    public String toStringDate() {

        DecimalFormat df = new DecimalFormat("00");

        return  df.format(this.date.getDayOfMonth()) +
                "-" +
                this.date.getMonth().getDisplayName(TextStyle.SHORT, new Locale("Romanian")) +
                "-" +
                df.format(this.date.getYear()) +
                " " +
                df.format(this.date.getHour()) +
                ":" +
                df.format(this.date.getMinute()) +
                ":" +
                df.format(this.date.getSecond());
    }

    public String toString() {
        return toStringDate() + " - " + this.content;
    }
}
