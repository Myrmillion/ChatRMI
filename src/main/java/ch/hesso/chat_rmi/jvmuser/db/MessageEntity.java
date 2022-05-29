package ch.hesso.chat_rmi.jvmuser.db;

import ch.hesso.chat_rmi.jvmuser.moo.Message;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

//@Entity
//public class MessageEntity {
//
//    @Id
//    public long id;
//
//    public Message message;
//    public String comment;
//    public Date date;
//
//    public Note(long id, String text, String comment, Date date) {
//        this.id = id;
//        this.text = text;
//        this.comment = comment;
//        this.date = date;
//    }
//
//    public Note() {
//    }
//}


//package ch.hesso.chat_rmi.jvmuser.db;
//
//import javax.persistence.*;
//import java.io.Serializable;
//
//@Entity
//public class MessageEntity implements Serializable
//{
//    private static final long serialVersionUID = 1L;
//
//    @Id
//    @GeneratedValue
//    private long id;
//    private int userFromId;
//    private int userToId;
//    private String message;
//    private String date;
//    private String time;
//
//    public MessageEntity()
//    {
//    }
//
//    public MessageEntity(int userFromId, int userToId, String message, String date, String time)
//    {
//        this.userFromId = userFromId;
//        this.userToId = userToId;
//        this.message = message;
//        this.date = date;
//        this.time = time;
//    }
//
//    public long getId()
//    {
//        return id;
//    }
//
//    public void setId(long id)
//    {
//        this.id = id;
//    }
//
//    public int getUserFromId()
//    {
//        return userFromId;
//    }
//
//    public void setUserFromId(int userFromId)
//    {
//        this.userFromId = userFromId;
//    }
//
//    public int getUserToId()
//    {
//        return userToId;
//    }
//
//    public void setUserToId(int userToId)
//    {
//        this.userToId = userToId;
//    }
//
//    public String getMessage()
//    {
//        return message;
//    }
//
//    public void setMessage(String message)
//    {
//        this.message = message;
//    }
//
//    public String getDate()
//    {
//        return date;
//    }
//
//    public void setDate(String date)
//    {
//        this.date = date;
//    }
//
//    public String getTime()
//    {
//        return time;
//    }
//
//    public void setTime(String time)
//    {
//        this.time = time;
//    }
//
//    @Override
//    public String toString()
//    {
//        return String.format("(%d, %d, %s)", this.userFromId, this.userToId, this.message);
//    }
//}
//
