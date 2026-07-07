# CatCare — Smart Cat Companion App

**#hackthekitty submission** · World Cat Domination Day theme

CatCare helps cat owners manage their cat's daily life in one place — feeding
schedules, health logs, vet reminders, and gamified streaks — built fully
offline with no third-party APIs or accounts required.

---

##  Theme Connection

CatCare directly serves **two** hackathon categories:
- **For the Cats** — feeding schedules and health tracking improve a cat's daily wellbeing
- **For Cat Owners** — vet reminders, weight tracking, and gamified streaks make it easier and more rewarding to be a consistent, attentive cat parent

---

##  Features

| Feature | Description |
|---|---|
|  Cat Profiles | Add multiple cats with name, breed, age, weight, gender, photo |
|  Feeding Schedule | Set meal times with optional daily notification reminders |
|  Health Log | Track weight over time, log checkups/symptoms/vaccines with notes |
|  Vet Reminders | Schedule vet visits/vaccinations with date-based notifications |
|  Badges & Streaks | 8 unlockable badges, daily activity streak tracking |
|  Symptom Guide | 30+ common cat symptoms with causes, actions and urgency levels — fully offline, searchable |
|  Weight Chart | Visual line graph of weight history per cat with trend analysis and summary stats |---

##  Tech Stack

- **Language:** Java
- **Min SDK:** 26 (Android 8.0)
- **Target SDK:** 34
- **Storage:** SharedPreferences (JSON-serialized) — fully offline, no backend
- **UI:** Material Design 3 Components
- **Notifications:** AlarmManager + BroadcastReceiver
- **Libraries:**
  - `com.google.android.material:material:1.11.0`
  - `de.hdodenhof:circleimageview:3.1.0`
  - `androidx.recyclerview:recyclerview:1.3.2`
  - `com.github.PhilJay:MPAndroidChart:v3.1.0` (weight chart visualisation)

No external APIs, no AI services, no Firebase, no internet permission required.
All data stays on-device.

---

##  Architecture

com.catcare.app/
├── MainActivity.java              → Bottom nav host, swaps fragments
├── activities/
│   ├── AddCatActivity.java        → Add/edit/delete cat profile
│   ├── SymptomGuideActivity.java  → Offline symptom guide + search
│   └── WeightChartActivity.java   → Weight history chart + trend
├── fragments/
│   ├── HomeFragment.java          → Dashboard: streak, cat count, list
│   ├── FeedingFragment.java       → Meal scheduler + notifications
│   ├── HealthFragment.java        → Health log + weight tracking
│   ├── RemindersFragment.java     → Vet/vaccine reminders
│   └── BadgesFragment.java        → Streaks + badge collection
├── models/
│   ├── Cat.java
│   ├── MealEntry.java
│   ├── HealthEntry.java
│   ├── Reminder.java
│   └── Symptom.java
├── adapters/
│   └── CatAdapter.java            → RecyclerView for cat list
└── utils/
├── PrefsHelper.java           → All data persistence (JSON + SharedPrefs)
└── NotificationReceiver.java  → Fires notifications via AlarmManager

**Data flow:** Every screen reads/writes through `PrefsHelper`, which stores
each data type as a JSON array in Android SharedPreferences. No SQL, no network.

**Gamification:** `updateStreak()` runs on every health log — compares today
vs last logged date to increment or reset. Badges unlock immediately after
qualifying actions.

---

##  Security & Privacy

- **No internet permission requested** — the app cannot transmit any data
  off-device.
- **No third-party analytics, ad SDKs, or tracking.**
- **No login/account system** — there is no PII transmitted or stored remotely.
- **Local photo URIs** are accessed via `ACTION_OPEN_DOCUMENT`-style persistable
  URI permissions, not file copying — least-privilege approach.
- **All data stored in app-private SharedPreferences**, sandboxed by Android
  and inaccessible to other apps without root.
- Notification permission (`POST_NOTIFICATIONS`) is requested at runtime per
  Android 13+ guidelines, not assumed.

_Aikido security scan report: https://drive.google.com/file/d/1D642VMTwsP9fiHW9mcU1SWQMmQKjmkm1/view?usp=sharing


---

## 📖 Symptom Guide

CatCare includes a built-in offline symptom reference covering 30+ common
cat health symptoms across 8 categories:

- Eating & Digestion
- Urinary
- Breathing & Nose
- Eyes
- Skin & Coat
- Behaviour
- Weight & Appetite
- Ears & Mouth

Each symptom shows possible causes, recommended action, and a colour-coded
urgency level (🟢 Monitor / 🟡 Watch closely / 🔴 See vet soon).
Fully searchable — no internet required.

> ⚠️ The symptom guide is for reference only. Always consult a vet for diagnosis.

---

## 📈 Weight Chart

The Weight Tracker visualises a cat's weight history as a smooth line graph
built with MPAndroidChart. Features include:

- Current / lowest / highest weight summary cards
- Smooth cubic bezier pink line graph with fill
- Date labels on X axis
- Pinch to zoom and drag to scroll
- Automatic trend analysis:
  - ✅ Stable weight
  - 📈 Slight gain
  - ⚠️ Notable gain — review diet
  - 📉 Slight loss
  - 🚨 Notable loss — see vet

All data comes from existing health log entries — no extra input needed.

---

##  Setup & Run Instructions

1. Clone this repository
2. Open in **Android Studio** (Hedgehog or later recommended)
3. Let Gradle sync automatically (no manual config needed — no API keys required)
4. Run on an emulator or physical device with **API 26+**
5. Grant notification permission when prompted (for reminders to function)

No `.env` files, API keys, or external service setup needed — the app works
fully offline out of the box.

## 📱 How to Use CatCare

### Step 1 — Add Your Cat
Tap **+ Add a Cat** on the Home screen → fill in name, breed, age, weight →
pick a photo → tap **Save Kitty!**

### Step 2 — Set Up Feeding
Go to **Feeding** tab → tap **+ Add Meal** → enter meal name, food type,
amount, pick a time → toggle **Remind me** ON for daily notifications

### Step 3 — Log Health
Go to **Health** tab → tap **+ Add Entry** → choose entry type (checkup,
symptom, vaccine, general) → enter weight and notes → tap **Save**

### Step 4 — Set Vet Reminders
Go to **Reminders** tab → tap **+ Add Reminder** → enter title, pick date
and time → toggle notify ON → tap **Save**

### Step 5 — Check Symptom Guide
Go to **Health** tab → tap **📖 Symptom Guide** → scroll or search symptoms
by name → each card shows causes, action, and urgency level

### Step 6 — Track Weight
Go to **Health** tab → tap **📈 Weight Chart** → select your cat →
view line graph and automatic trend analysis

### Step 7 — Earn Badges
Every action (adding cats, meals, health entries, reminders) and
maintaining daily streaks unlocks badges in the **Badges** tab

---

##  Demo Video

[Link to demo video — https://drive.google.com/file/d/1spJj1lmDzSHuoETf2hZ4O-7uVja2Dex5/view?usp=sharing]

---

##  Built By

GEETIKA MAHOUR (geetikamahour777@gmail.com)— built during the #hackthekitty submission period
(June 24 – July 7, 2026). All code written from scratch during the event.

---

##  License

Built for #hackthekitty 2026. All rights retained by the author per hackathon
Official Rules Section 12 (Intellectual Property).