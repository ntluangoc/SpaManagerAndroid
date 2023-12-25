package com.example.demoappspa.Entity;

import java.io.Serializable;
import java.util.Date;

public class Appointment implements Serializable {
    private String id;
    private String name;
    private String time;
    private String date;
    private String note;
    private boolean isCheck;
    private String author;
    public Appointment() {
    }

    public Appointment(String id, String name, String time, String date, String note, boolean isCheck, String author) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.date = date;
        this.note = note;
        this.isCheck = isCheck;
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", time='" + time + '\'' +
                ", date='" + date + '\'' +
                ", note='" + note + '\'' +
                ", isCheck=" + isCheck +
                ", author='" + author + '\'' +
                '}';
    }
}
