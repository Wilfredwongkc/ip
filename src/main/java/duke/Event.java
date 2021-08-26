package duke;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Event extends Task {
    protected LocalDate date;

    public Event(String description, String by) {
        super(description, "Event", by);
        if (by.contains("/")) {
            date = LocalDate.parse(by.split("/")[2] + "-" + by.split("/")[1] + "-" +
                    (Integer.parseInt(by.split("/")[0]) < 10 ? "0" + by.split("/")[0] :
                            by.split("/")[0]));
        } else {
            date = LocalDate.parse(by);
        }
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (by: " + date.format(DateTimeFormatter.ofPattern("MMM d yyyy")) + ")";
    }
}
