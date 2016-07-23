package org.samcrow.antchat;

import org.joda.time.DateTime;

/**
 * Stores a message
 */
public class Message {

    /**
     * The text in the message
     */
    private final String text;
    /**
     * The time the message was sent or received
     */
    private final DateTime time;
    private final Direction direction;

    public Message(String text, DateTime time, Direction direction) {
        this.text = text;
        this.time = time;
        this.direction = direction;
    }

    public String getText() {
        return text;
    }

    public DateTime getTime() {
        return time;
    }

    public Direction getDirection() {
        return direction;
    }

    public static enum Direction {
        Received,
        Sent,
    }
}
