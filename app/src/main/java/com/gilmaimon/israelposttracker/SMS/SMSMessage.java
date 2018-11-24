package com.gilmaimon.israelposttracker.SMS;

import java.util.Date;

public class SMSMessage {

    private String uniqueId;
    private String sender;
    private String message;
    private Date date;

    SMSMessage(String uniqueId, String sender, String message, Date date) {
        this.uniqueId = uniqueId;
        this.sender = sender;
        this.message = message;
        this.date = date;
    }

    public SMSMessage(String uniqueId, String sender, String message, long date) {
        this(uniqueId, sender, message, new Date(date));
    }

    public String getUniqueId() {
        return uniqueId;
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
            return ((SMSMessage) obj).uniqueId.equals(this.uniqueId);
        }
        else return false;
    }
}
