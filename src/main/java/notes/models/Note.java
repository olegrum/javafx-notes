package notes.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Note {
    private StringProperty date_time;
    private StringProperty text;

    public Note() {
        this.date_time = new SimpleStringProperty();
        this.text = new SimpleStringProperty();
    }

    public String getDateTime() {
        return date_time.get();
    }

    public void setDateTime(String date_time) {
        this.date_time.set(date_time);
    }

    public StringProperty DateTimeProperty() {
        return date_time;
    }

    public String getText() {
        return text.get();
    }

    public void setText(String text) {
        this.text.set(text);
    }

    public StringProperty TextProperty() {
        return text;
    }
}
