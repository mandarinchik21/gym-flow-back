package com.example.Model;

import java.time.LocalTime;

public class ScheduleTemplate {

    private int id;             
    private int trainerId;       
    private String dayOfWeek;  
    private LocalTime time;      

    public ScheduleTemplate() {}

    public ScheduleTemplate(int id, int trainerId, String dayOfWeek, LocalTime time) {
        this.id = id;
        this.trainerId = trainerId;
        this.dayOfWeek = dayOfWeek;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(int trainerId) {
        this.trainerId = trainerId;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "ScheduleTemplate{" +
                "id=" + id +
                ", trainerId=" + trainerId +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", time=" + time +
                '}';
    }
}
