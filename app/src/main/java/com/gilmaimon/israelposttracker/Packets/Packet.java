package com.gilmaimon.israelposttracker.Packets;

public class Packet {
    private final String postId;

    public Packet(String postId) {
        this.postId = postId;
    }

    public String getPostId() {
        return this.postId;
    }

    @Override
    public int hashCode() {
        return postId.hashCode();
    }

    @Override
    public boolean equals(Object otherObj) {
        if (otherObj instanceof Packet) {
            Packet other = (Packet) otherObj;
            return this.getPostId().equals(other.getPostId());
        } else return false;
    }
}
