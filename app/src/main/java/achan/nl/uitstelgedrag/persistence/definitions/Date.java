package achan.nl.uitstelgedrag.persistence.definitions;

/**
 * Created by Etienne on 15-8-2016.
 */
public class Date {
    String date;

    public Date(int year, int month, int day) {
        this.date = String.format("%d-%d-%d", year, month, day);
    }
}
