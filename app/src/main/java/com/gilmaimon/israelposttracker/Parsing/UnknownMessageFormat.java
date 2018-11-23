package com.gilmaimon.israelposttracker.Parsing;

public class UnknownMessageFormat extends Exception {
    public UnknownMessageFormat (String content){
        super("Message of unknown format, " +
                "could not parse or sort the message:\n"
                + content
        );
    }
}