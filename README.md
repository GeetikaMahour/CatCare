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

---

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

No external APIs, no AI services, no Firebase, no internet permission required.
All data stays on-device.

---

##  Architecture

com.catcare.app/
├── MainActivity.java          → Hosts bottom navigation, swaps fragments
├── activities/
│   └── AddCatActivity.java    → Add/edit/delete a cat profile
├── fragments/
│   ├── HomeFragment.java      → Dashboard: streak, cat count, cat list
│   ├── FeedingFragment.java   → Meal scheduler + reminder scheduling
│   ├── HealthFragment.java    → Weight/health entry log
│   ├── RemindersFragment.java → Vet/vaccine reminders
│   └── BadgesFragment.java    → Streak + badge collection display
├── models/
│   ├── Cat.java
│   ├── MealEntry.java
│   ├── HealthEntry.java
│   └── Reminder.java
├── adapters/
│   └── CatAdapter.java        → RecyclerView adapter for cat list
└── utils/
├── PrefsHelper.java       → All data persistence (SharedPreferences + JSON)
└── NotificationReceiver.java → Fires local notifications via AlarmManager

**Data flow:** All screens read/write through a single `PrefsHelper` class,
which serializes each data type (cats, meals, health entries, reminders,
streak, badges) as a JSON array inside SharedPreferences. No SQL, no schema
migrations needed — kept intentionally simple for an offline-first hackathon
build.

**Gamification logic:** `PrefsHelper.updateStreak()` runs whenever a health
entry is logged, comparing today's date to the last logged date to increment
or reset the streak. Badge unlocks are checked immediately after relevant
actions (adding a cat, meal, health entry, reminder, or hitting streak
milestones).

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

_Aikido security scan report: see `/security/aikido-report.pdf` (or link)._

---

##  Setup & Run Instructions

1. Clone this repository
2. Open in **Android Studio** (Hedgehog or later recommended)
3. Let Gradle sync automatically (no manual config needed — no API keys required)
4. Run on an emulator or physical device with **API 26+**
5. Grant notification permission when prompted (for reminders to function)

No `.env` files, API keys, or external service setup needed — the app works
fully offline out of the box.

---

##  Demo Video

[Link to demo video — under 5 minutes]

---

##  Built By

GEETIKA MAHOUR (geetikamahour777@gmail.com)— built during the #hackthekitty submission period
(June 24 – July 7, 2026). All code written from scratch during the event.

---

##  License

Built for #hackthekitty 2026. All rights retained by the author per hackathon
Official Rules Section 12 (Intellectual Property).