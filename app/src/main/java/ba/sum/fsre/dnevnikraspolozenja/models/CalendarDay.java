package ba.sum.fsre.dnevnikraspolozenja.models;

import java.time.LocalDate;

public class CalendarDay {

    private LocalDate date;
    private Integer moodScore; // null ako nema mood taj dan

    public CalendarDay(LocalDate date, Integer moodScore) {
        this.date = date;
        this.moodScore = moodScore;
    }

    public LocalDate getDate() {
        return date;
    }

    public Integer getMoodScore() {
        return moodScore;
    }

    public boolean hasMood() {
        return moodScore != null;
    }
}
