package com.catcare.app.models;

public class HealthEntry {
    private String id;
    private String catId;
    private String date;
    private float weight;
    private String entryType;
    private String notes;

    public HealthEntry() {}

    public HealthEntry(String id, String catId, String date,
                       float weight, String entryType, String notes) {
        this.id = id;
        this.catId = catId;
        this.date = date;
        this.weight = weight;
        this.entryType = entryType;
        this.notes = notes;
    }

    public String getId() { return id; }
    public String getCatId() { return catId; }
    public String getDate() { return date; }
    public float getWeight() { return weight; }
    public String getEntryType() { return entryType; }
    public String getNotes() { return notes; }

    public void setId(String id) { this.id = id; }
    public void setCatId(String catId) { this.catId = catId; }
    public void setDate(String date) { this.date = date; }
    public void setWeight(float weight) { this.weight = weight; }
    public void setEntryType(String entryType) { this.entryType = entryType; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getTypeEmoji() {
        switch (entryType) {
            case "checkup":  return "🩺";
            case "symptom":  return "🤒";
            case "vaccine":  return "💉";
            default:         return "📋";
        }
    }
}