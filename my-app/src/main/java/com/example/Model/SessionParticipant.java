package com.example.Model;

public class SessionParticipant {
    private int sessionId;      
    private int participantId;  

    public SessionParticipant() {
    }

    public SessionParticipant(int sessionId, int participantId) {
        this.sessionId = sessionId;
        this.participantId = participantId;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getParticipantId() {
        return participantId;
    }

    public void setParticipantId(int participantId) {
        this.participantId = participantId;
    }
}
