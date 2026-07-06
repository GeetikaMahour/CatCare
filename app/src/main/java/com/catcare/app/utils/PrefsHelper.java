package com.catcare.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.catcare.app.models.Cat;
import com.catcare.app.models.MealEntry;
import com.catcare.app.models.HealthEntry;
import com.catcare.app.models.Reminder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class PrefsHelper {

    private static final String PREFS_NAME    = "catcare_prefs";
    private static final String KEY_CATS      = "cats";
    private static final String KEY_MEALS     = "meals";
    private static final String KEY_HEALTH    = "health";
    private static final String KEY_REMINDERS = "reminders";
    private static final String KEY_STREAK    = "streak";
    private static final String KEY_LAST_LOG  = "last_log_date";
    private static final String KEY_BADGES    = "badges";

    private final SharedPreferences prefs;

    public PrefsHelper(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // ──────────────── CATS ────────────────

    public void saveCat(Cat cat) {
        if (cat.getId() == null) cat.setId(UUID.randomUUID().toString());
        List<Cat> cats = getAllCats();
        cats.removeIf(c -> c.getId().equals(cat.getId()));
        cats.add(cat);
        saveCatList(cats);
    }

    public void deleteCat(String catId) {
        List<Cat> cats = getAllCats();
        cats.removeIf(c -> c.getId().equals(catId));
        saveCatList(cats);
    }

    public List<Cat> getAllCats() {
        List<Cat> list = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(prefs.getString(KEY_CATS, "[]"));
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                Cat c = new Cat();
                c.setId(o.optString("id"));
                c.setName(o.optString("name"));
                c.setBreed(o.optString("breed"));
                c.setAge((float) o.optDouble("age"));
                c.setWeight((float) o.optDouble("weight"));
                c.setGender(o.optString("gender"));
                c.setPhotoUri(o.optString("photoUri"));
                c.setCreatedAt(o.optLong("createdAt"));
                list.add(c);
            }
        } catch (JSONException e) { e.printStackTrace(); }
        return list;
    }

    private void saveCatList(List<Cat> cats) {
        try {
            JSONArray arr = new JSONArray();
            for (Cat c : cats) {
                JSONObject o = new JSONObject();
                o.put("id", c.getId());
                o.put("name", c.getName());
                o.put("breed", c.getBreed());
                o.put("age", c.getAge());
                o.put("weight", c.getWeight());
                o.put("gender", c.getGender());
                o.put("photoUri", c.getPhotoUri() != null ? c.getPhotoUri() : "");
                o.put("createdAt", c.getCreatedAt());
                arr.put(o);
            }
            prefs.edit().putString(KEY_CATS, arr.toString()).apply();
        } catch (JSONException e) { e.printStackTrace(); }
    }

    // ──────────────── MEALS ────────────────

    public void saveMeal(MealEntry meal) {
        if (meal.getId() == null) meal.setId(UUID.randomUUID().toString());
        List<MealEntry> meals = getAllMeals();
        meals.removeIf(m -> m.getId().equals(meal.getId()));
        meals.add(meal);
        saveMealList(meals);
    }

    public void deleteMeal(String mealId) {
        List<MealEntry> meals = getAllMeals();
        meals.removeIf(m -> m.getId().equals(mealId));
        saveMealList(meals);
    }

    public List<MealEntry> getMealsForCat(String catId) {
        List<MealEntry> result = new ArrayList<>();
        for (MealEntry m : getAllMeals())
            if (m.getCatId().equals(catId)) result.add(m);
        return result;
    }

    private List<MealEntry> getAllMeals() {
        List<MealEntry> list = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(prefs.getString(KEY_MEALS, "[]"));
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                MealEntry m = new MealEntry();
                m.setId(o.optString("id"));
                m.setCatId(o.optString("catId"));
                m.setMealName(o.optString("mealName"));
                m.setFoodType(o.optString("foodType"));
                m.setAmountGrams((float) o.optDouble("amountGrams"));
                m.setTimeHHMM(o.optString("timeHHMM"));
                m.setReminderEnabled(o.optBoolean("reminderEnabled"));
                list.add(m);
            }
        } catch (JSONException e) { e.printStackTrace(); }
        return list;
    }

    private void saveMealList(List<MealEntry> meals) {
        try {
            JSONArray arr = new JSONArray();
            for (MealEntry m : meals) {
                JSONObject o = new JSONObject();
                o.put("id", m.getId());
                o.put("catId", m.getCatId());
                o.put("mealName", m.getMealName());
                o.put("foodType", m.getFoodType());
                o.put("amountGrams", m.getAmountGrams());
                o.put("timeHHMM", m.getTimeHHMM());
                o.put("reminderEnabled", m.isReminderEnabled());
                arr.put(o);
            }
            prefs.edit().putString(KEY_MEALS, arr.toString()).apply();
        } catch (JSONException e) { e.printStackTrace(); }
    }

    // ──────────────── HEALTH ────────────────

    public void saveHealthEntry(HealthEntry entry) {
        if (entry.getId() == null) entry.setId(UUID.randomUUID().toString());
        List<HealthEntry> entries = getAllHealthEntries();
        entries.removeIf(h -> h.getId().equals(entry.getId()));
        entries.add(entry);
        saveHealthList(entries);
        updateStreak();
        checkHealthBadge();
    }

    public void deleteHealthEntry(String entryId) {
        List<HealthEntry> entries = getAllHealthEntries();
        entries.removeIf(h -> h.getId().equals(entryId));
        saveHealthList(entries);
    }

    public List<HealthEntry> getHealthForCat(String catId) {
        List<HealthEntry> result = new ArrayList<>();
        for (HealthEntry h : getAllHealthEntries())
            if (h.getCatId().equals(catId)) result.add(h);
        return result;
    }

    private List<HealthEntry> getAllHealthEntries() {
        List<HealthEntry> list = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(prefs.getString(KEY_HEALTH, "[]"));
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                HealthEntry h = new HealthEntry();
                h.setId(o.optString("id"));
                h.setCatId(o.optString("catId"));
                h.setDate(o.optString("date"));
                h.setWeight((float) o.optDouble("weight"));
                h.setEntryType(o.optString("entryType"));
                h.setNotes(o.optString("notes"));
                list.add(h);
            }
        } catch (JSONException e) { e.printStackTrace(); }
        return list;
    }

    private void saveHealthList(List<HealthEntry> entries) {
        try {
            JSONArray arr = new JSONArray();
            for (HealthEntry h : entries) {
                JSONObject o = new JSONObject();
                o.put("id", h.getId());
                o.put("catId", h.getCatId());
                o.put("date", h.getDate());
                o.put("weight", h.getWeight());
                o.put("entryType", h.getEntryType());
                o.put("notes", h.getNotes());
                arr.put(o);
            }
            prefs.edit().putString(KEY_HEALTH, arr.toString()).apply();
        } catch (JSONException e) { e.printStackTrace(); }
    }

    // ──────────────── REMINDERS ────────────────

    public void saveReminder(Reminder r) {
        if (r.getId() == null) r.setId(UUID.randomUUID().toString());
        List<Reminder> reminders = getAllReminders();
        reminders.removeIf(x -> x.getId().equals(r.getId()));
        reminders.add(r);
        saveReminderList(reminders);
        checkVetBadge();
    }

    public void deleteReminder(String reminderId) {
        List<Reminder> list = getAllReminders();
        list.removeIf(r -> r.getId().equals(reminderId));
        saveReminderList(list);
    }

    public void markReminderDone(String reminderId, boolean done) {
        List<Reminder> list = getAllReminders();
        for (Reminder r : list) {
            if (r.getId().equals(reminderId)) {
                r.setDone(done);
                break;
            }
        }
        saveReminderList(list);
    }

    public List<Reminder> getRemindersForCat(String catId) {
        List<Reminder> result = new ArrayList<>();
        for (Reminder r : getAllReminders())
            if (r.getCatId().equals(catId)) result.add(r);
        return result;
    }

    public List<Reminder> getAllReminders() {
        List<Reminder> list = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(prefs.getString(KEY_REMINDERS, "[]"));
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                Reminder r = new Reminder();
                r.setId(o.optString("id"));
                r.setCatId(o.optString("catId"));
                r.setTitle(o.optString("title"));
                r.setDate(o.optString("date"));
                r.setNotes(o.optString("notes"));
                r.setDone(o.optBoolean("isDone"));
                list.add(r);
            }
        } catch (JSONException e) { e.printStackTrace(); }
        return list;
    }

    private void saveReminderList(List<Reminder> reminders) {
        try {
            JSONArray arr = new JSONArray();
            for (Reminder r : reminders) {
                JSONObject o = new JSONObject();
                o.put("id", r.getId());
                o.put("catId", r.getCatId());
                o.put("title", r.getTitle());
                o.put("date", r.getDate());
                o.put("notes", r.getNotes());
                o.put("isDone", r.isDone());
                arr.put(o);
            }
            prefs.edit().putString(KEY_REMINDERS, arr.toString()).apply();
        } catch (JSONException e) { e.printStackTrace(); }
    }

    // ──────────────── STREAK ────────────────

    private void updateStreak() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());
        String lastLog = prefs.getString(KEY_LAST_LOG, "");
        int streak = prefs.getInt(KEY_STREAK, 0);

        if (today.equals(lastLog)) return;

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        String yesterdayStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(yesterday.getTime());

        streak = lastLog.equals(yesterdayStr) ? streak + 1 : 1;

        prefs.edit()
                .putInt(KEY_STREAK, streak)
                .putString(KEY_LAST_LOG, today)
                .apply();

        checkStreakBadges(streak);
    }

    public int getStreak() {
        return prefs.getInt(KEY_STREAK, 0);
    }

    // ──────────────── BADGES ────────────────

    public List<String> getBadges() {
        List<String> list = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(prefs.getString(KEY_BADGES, "[]"));
            for (int i = 0; i < arr.length(); i++) list.add(arr.getString(i));
        } catch (JSONException e) { e.printStackTrace(); }
        return list;
    }

    private void awardBadge(String badgeId) {
        List<String> badges = getBadges();
        if (!badges.contains(badgeId)) {
            badges.add(badgeId);
            saveBadgeList(badges);
        }
    }

    private void saveBadgeList(List<String> badges) {
        try {
            JSONArray arr = new JSONArray();
            for (String b : badges) arr.put(b);
            prefs.edit().putString(KEY_BADGES, arr.toString()).apply();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void checkProfileBadge() {
        awardBadge("first_cat");
        if (getAllCats().size() >= 3) awardBadge("cat_family");
    }

    private void checkStreakBadges(int streak) {
        if (streak >= 3)  awardBadge("streak_3");
        if (streak >= 7)  awardBadge("streak_7");
        if (streak >= 30) awardBadge("streak_30");
    }

    private void checkHealthBadge() {
        awardBadge("health_log");
    }

    private void checkVetBadge() {
        awardBadge("vet_ready");
    }

    public void checkMealBadge() {
        awardBadge("meal_plan");
    }
}