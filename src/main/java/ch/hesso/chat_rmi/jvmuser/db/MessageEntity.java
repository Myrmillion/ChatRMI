package ch.hesso.chat_rmi.jvmuser.db;

import ch.hesso.chat_rmi.jvmuser.helper.CryptoHelper;
import ch.hesso.chat_rmi.jvmuser.moo.Message;
import ch.hesso.chat_rmi.jvmuser.moo.Sendable;
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

    @Convert(converter = SendableConverter.class, dbType = String.class)
    public Sendable<Message> sendable;

    @Index
    public Date date;

    @Index(type = IndexType.HASH)
    @Unique(onConflict = ConflictStrategy.REPLACE)
    public String uniqueMessageID;

    public MessageEntity(long id, User sender, User receiver, Sendable<Message> sendable, Date date, String uniqueMessageID)
    {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.sendable = sendable;
        this.date = date;

        try
        {
            this.uniqueMessageID = String.join(" ", new String[]{sender.getUsername(), receiver.getUsername(), Integer.toString(sendable.hashCode()), date.toString()});
        }
        catch (Exception e)
        {
            this.uniqueMessageID = "";
        }
    }

    public MessageEntity(User sender, User receiver, Sendable<Message> sendable)
    {

        this(0, sender, receiver, sendable, new Date(), "");
    }

    /*------------------------------------------------------------------*\
    |*					    	  Converters			    			*|
    \*------------------------------------------------------------------*/

    /*------------------------------*\
    |*		       User		    	*|
    \*------------------------------*/

    public static class UserConverter implements PropertyConverter<User, String>
    {
        @Override
        public User convertToEntityProperty(String string)
        {
            try
            {
                return User.getUser(string);
            }
            catch (Exception e)
            {
                return null;
            }
        }

        @Override
        public String convertToDatabaseValue(User user)
        {
            return User.getString(user);
        }
    }

    /*------------------------------*\
    |*		     Sendable	    	*|
    \*------------------------------*/

    public static class SendableConverter implements PropertyConverter<Sendable<Message>, String>
    {
        @Override
        public Sendable convertToEntityProperty(String s)
        {
            try
            {
                return (Sendable) CryptoHelper.StringToObject(s);
            }
            catch (Exception e)
            {
                return null;
            }
        }

        @Override
        public String convertToDatabaseValue(Sendable sendable)
        {
            try
            {
                return CryptoHelper.ObjectToString(sendable);
            }
            catch (Exception e)
            {
                return "";
            }
        }
    }

}