package ch.hesso.chat_rmi.jvmuser.db;

import javax.persistence.*;
import java.io.Serializable;


@Entity
public class MessageEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private long id;

    private int userId;
    private String message;
    private String date;
    private String time;

    public MessageEntity() {
    }

    public MessageEntity(int userId, String message, String date, String time) {
        this.userId = userId;
        this.message = message;
        this.date = date;
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return String.format("(%d, %s)", this.userId, this.message);
    }
}

