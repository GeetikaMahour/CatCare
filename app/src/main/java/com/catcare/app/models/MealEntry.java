package com.catcare.app.models;

public class MealEntry {
    private String id;
    private String catId;
    private String mealName;
    private String foodType;
    private float amountGrams;
    private String timeHHMM;
    private boolean reminderEnabled;

    public MealEntry() {}

    public MealEntry(String id, String catId, String mealName,
                     String foodType, float amountGrams,
                     String timeHHMM, boolean reminderEnabled) {
        this.id = id;
        this.catId = catId;
        this.mealName = mealName;
        this.foodType = foodType;
        this.amountGrams = amountGrams;
        this.timeHHMM = timeHHMM;
        this.reminderEnabled = reminderEnabled;
    }

    public String getId() { return id; }
    public String getCatId() { return catId; }
    public String getMealName() { return mealName; }
    public String getFoodType() { return foodType; }
    public float getAmountGrams() { return amountGrams; }
    public String getTimeHHMM() { return timeHHMM; }
    public boolean isReminderEnabled() { return reminderEnabled; }

    public void setId(String id) { this.id = id; }
    public void setCatId(String catId) { this.catId = catId; }
    public void setMealName(String mealName) { this.mealName = mealName; }
    public void setFoodType(String foodType) { this.foodType = foodType; }
    public void setAmountGrams(float amountGrams) { this.amountGrams = amountGrams; }
    public void setTimeHHMM(String timeHHMM) { this.timeHHMM = timeHHMM; }
    public void setReminderEnabled(boolean r) { this.reminderEnabled = r; }
}