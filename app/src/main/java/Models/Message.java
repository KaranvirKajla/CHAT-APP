package Models;

import java.util.Date;

public class Message {
    String message;
    String date;
    long seconds;
    String to;
    String from;
    String id;
    String imageUrl;

    public long getSeconds() {
        return seconds;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }

    public Message() {
    }

    public Message(String message, String date, String to, String from,String id,long seconds,String imageUrl) {
        this.message = message;
        this.date = date;
        this.to = to;
        this.from = from;
        this.id = id;
        this.seconds = seconds;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
