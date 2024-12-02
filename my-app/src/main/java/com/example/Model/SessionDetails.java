package com.example.Model;

import java.time.LocalDate;
import java.time.LocalTime;

public class SessionDetails {
    private String sessionType;
    private LocalDate date;
    private LocalTime time;
    private int trainerId;

    public SessionDetails(String sessionType, LocalDate date, LocalTime time, int trainerId) {
        this.sessionType = sessionType;
        this.date = date;
        this.time = time;
        this.trainerId = trainerId;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public int getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(int trainerId) {
        this.trainerId = trainerId;
    }
}
