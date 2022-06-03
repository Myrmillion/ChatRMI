package ch.hesso.chat_rmi.jvmuser.db;

import ch.hesso.chat_rmi.jvmuser.moo.Message;
import ch.hesso.chat_rmi.jvmuser.moo.User;
import io.objectbox.annotation.*;
import io.objectbox.converter.PropertyConverter;

import java.util.Date;

@Entity
public class MessageEntity
{
    /*------------------------------------------------------------------*\
    |*							Public Attributes						*|
    \*------------------------------------------------------------------*/

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

    @Index(type = IndexType.HASH)
    @Unique(onConflict = ConflictStrategy.REPLACE)
    public String uniqueMessageID;

    /*------------------------------------------------------------------*\
    |*							Constructors							*|
    \*------------------------------------------------------------------*/

    public MessageEntity(long id, User sender, User receiver, Message message, Date date, String uniqueMessageID)
    {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.date = date;

        this.uniqueMessageID = String.join(" ", new String[]{sender.getUsername(), receiver.getUsername(), message.getText(), date.toString()});
    }

    public MessageEntity(User sender, User receiver, Message message)
    {
        this(0, sender, receiver, message, new Date(), "");
    }

    /*------------------------------------------------------------------*\
    |*					    	  Converters			    			*|
    \*------------------------------------------------------------------*/

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