package Models;

public class GroupMessage {
    String message;
    String date;
    long seconds;
    String from;
    String id;
    String imageUrl;

    public GroupMessage() {
    }

    public GroupMessage(String message, String date, long seconds, String from, String id, String imageUrl) {
        this.message = message;
        this.date = date;
        this.seconds = seconds;
        this.from = from;
        this.id = id;
        this.imageUrl = imageUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getSeconds() {
        return seconds;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
