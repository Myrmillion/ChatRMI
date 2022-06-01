package ch.hesso.chat_rmi.jvmuser.db;

import ch.hesso.chat_rmi.jvmuser.moo.Message;
import ch.hesso.chat_rmi.jvmuser.moo.User;
import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.converter.PropertyConverter;

import java.util.Date;

@Entity
public class MessageEntity
{
    @Id
    public long id;

    @Index
    @Convert(converter = UserConverter.class, dbType = String.class)
    public User sender;

    @Index
    @Convert(converter = UserConverter.class, dbType = String.class)
    public User receiver;

    @Convert(converter = MessageConverter.class, dbType = String.class)
    public Message message;

    @Index
    public Date date;

    public MessageEntity(long id, User sender, User receiver, Message message, Date date)
    {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.date = date;
    }

    public MessageEntity(User sender, User receiver, Message message)
    {
        this(0, sender, receiver, message, new Date());
    }

    public static class UserConverter implements PropertyConverter<User, String>
    {
        @Override
        public User convertToEntityProperty(String string)
        {
            return User.getUser(string);
        }

        @Override
        public String convertToDatabaseValue(User user)
        {
            return User.getString(user);
        }
    }

    public static class MessageConverter implements PropertyConverter<Message, String>
    {
        @Override
        public Message convertToEntityProperty(String string)
        {
            return Message.getMessage(string);
        }

        @Override
        public String convertToDatabaseValue(Message message)
        {
            return Message.getString(message);
        }
    }

}