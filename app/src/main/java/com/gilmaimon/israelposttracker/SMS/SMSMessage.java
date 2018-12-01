package com.gilmaimon.israelposttracker.SMS;

import java.util.Date;

public class SMSMessage {

    private final String sender;
    private final String message;
    private final Date date;

    SMSMessage(String sender, String message, Date date) {
        this.sender = sender;
        this.message = message;
        this.date = date;
    }

    public SMSMessage(String sender, String message, long date) {
        this(sender, message, new Date(date));
    }

    public int getUniqueId() {
        return hashCode();
    }

    public Date getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof SMSMessage) {
            return hashCode() == obj.hashCode();
        }
        else return false;
    }

    @Override
    public int hashCode() {
        return (getDate() + getMessage() + getSender()).hashCode();
    }
}
