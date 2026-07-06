package com.catcare.app.models;

public class Reminder {
    private String id;
    private String catId;
    private String title;
    private String date;
    private String notes;
    private boolean isDone;

    public Reminder() {}

    public Reminder(String id, String catId, String title,
                    String date, String notes) {
        this.id = id;
        this.catId = catId;
        this.title = title;
        this.date = date;
        this.notes = notes;
        this.isDone = false;
    }

    public String getId() { return id; }
    public String getCatId() { return catId; }
    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getNotes() { return notes; }
    public boolean isDone() { return isDone; }

    public void setId(String id) { this.id = id; }
    public void setCatId(String catId) { this.catId = catId; }
    public void setTitle(String title) { this.title = title; }
    public void setDate(String date) { this.date = date; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setDone(boolean done) { isDone = done; }
}