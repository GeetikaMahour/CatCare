package com.catcare.app.models;

public class Symptom {
    private String emoji;
    private String symptom;
    private String possibleCauses;
    private String action;
    private String urgency; // "low", "medium", "high"

    public Symptom(String emoji, String symptom,
                   String possibleCauses, String action, String urgency) {
        this.emoji = emoji;
        this.symptom = symptom;
        this.possibleCauses = possibleCauses;
        this.action = action;
        this.urgency = urgency;
    }

    public String getEmoji() { return emoji; }
    public String getSymptom() { return symptom; }
    public String getPossibleCauses() { return possibleCauses; }
    public String getAction() { return action; }
    public String getUrgency() { return urgency; }
}