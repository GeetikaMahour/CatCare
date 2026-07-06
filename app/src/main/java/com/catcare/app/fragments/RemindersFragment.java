package com.catcare.app.fragments;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.catcare.app.R;
import com.catcare.app.models.Cat;
import com.catcare.app.models.Reminder;
import com.catcare.app.utils.NotificationReceiver;
import com.catcare.app.utils.PrefsHelper;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class RemindersFragment extends Fragment {

    private PrefsHelper prefs;
    private LinearLayout llReminders;
    private Spinner spinnerCat;
    private List<Cat> cats;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reminders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = new PrefsHelper(requireContext());

        spinnerCat  = view.findViewById(R.id.spinner_cat_reminders);
        llReminders = view.findViewById(R.id.ll_reminders);
        MaterialButton btnAdd = view.findViewById(R.id.btn_add_reminder);

        cats = prefs.getAllCats();
        setupCatSpinner();

        btnAdd.setOnClickListener(v -> showAddReminderSheet());
    }

    @Override
    public void onResume() {
        super.onResume();
        cats = prefs.getAllCats();
        setupCatSpinner();
    }

    private void setupCatSpinner() {
        if (cats.isEmpty()) {
            llReminders.removeAllViews();
            TextView empty = new TextView(getContext());
            empty.setText("Add a cat first to set reminders! 🐱");
            empty.setTextColor(getResources().getColor(R.color.text_secondary, null));
            empty.setPadding(0, 24, 0, 0);
            llReminders.addView(empty);
            return;
        }
        String[] catNames = new String[cats.size()];
        for (int i = 0; i < cats.size(); i++) catNames[i] = cats.get(i).getName();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                R.layout.spinner_item, catNames);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerCat.setAdapter(adapter);
        spinnerCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                loadReminders(cats.get(pos).getId());
            }
            @Override
            public void onNothingSelected(AdapterView<?> p) {}
        });
        loadReminders(cats.get(0).getId());
    }

    private void loadReminders(String catId) {
        llReminders.removeAllViews();
        List<Reminder> reminders = prefs.getRemindersForCat(catId);

        Collections.sort(reminders, (a, b) -> a.getDate().compareTo(b.getDate()));

        if (reminders.isEmpty()) {
            TextView empty = new TextView(getContext());
            empty.setText("No reminders yet. Tap + Add Reminder! 💉");
            empty.setTextColor(getResources().getColor(R.color.text_secondary, null));
            empty.setPadding(0, 24, 0, 0);
            llReminders.addView(empty);
            return;
        }

        for (Reminder reminder : reminders) {
            View card = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_reminder, llReminders, false);

            TextView tvTitle = card.findViewById(R.id.tv_reminder_title);
            TextView tvDate  = card.findViewById(R.id.tv_reminder_date);
            TextView tvNotes = card.findViewById(R.id.tv_reminder_notes);
            CheckBox cbDone  = card.findViewById(R.id.cb_reminder_done);

            tvTitle.setText(reminder.getTitle());
            tvDate.setText("📅 " + reminder.getDate());

            if (reminder.getNotes() != null && !reminder.getNotes().isEmpty()) {
                tvNotes.setText(reminder.getNotes());
                tvNotes.setVisibility(View.VISIBLE);
            } else {
                tvNotes.setVisibility(View.GONE);
            }

            cbDone.setChecked(reminder.isDone());
            if (reminder.isDone()) {
                tvTitle.setPaintFlags(
                        tvTitle.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            }

            cbDone.setOnCheckedChangeListener((btn, checked) -> {
                prefs.markReminderDone(reminder.getId(), checked);
                loadReminders(catId);
            });

            card.findViewById(R.id.btn_delete_reminder).setOnClickListener(v -> {
                prefs.deleteReminder(reminder.getId());
                loadReminders(catId);
            });

            llReminders.addView(card);
        }
    }

    private void showAddReminderSheet() {
        if (cats.isEmpty()) {
            Toast.makeText(getContext(), "Please add a cat first! 🐾",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        BottomSheetDialog sheet = new BottomSheetDialog(requireContext());
        View sheetView = LayoutInflater.from(getContext())
                .inflate(R.layout.sheet_add_reminder, null);
        sheet.setContentView(sheetView);

        TextInputEditText etTitle = sheetView.findViewById(R.id.et_reminder_title);
        TextInputEditText etNotes = sheetView.findViewById(R.id.et_reminder_notes);
        TextView tvDate           = sheetView.findViewById(R.id.tv_selected_date);
        TextView tvReminderTime   = sheetView.findViewById(R.id.tv_reminder_time);
        androidx.appcompat.widget.SwitchCompat swNotify =
                sheetView.findViewById(R.id.sw_notify);
        MaterialButton btnPickDate = sheetView.findViewById(R.id.btn_pick_date);
        MaterialButton btnPickTime = sheetView.findViewById(R.id.btn_pick_reminder_time);
        MaterialButton btnSave     = sheetView.findViewById(R.id.btn_save_reminder);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        final String[] selectedDate = {""};
        final int[] selectedHour    = {9};
        final int[] selectedMin     = {0};

        // ── Date Picker ──
        btnPickDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            DatePickerDialog dpd = new DatePickerDialog(
                    requireContext(),
                    R.style.CatCare_DatePickerDialog,
                    (dp, y, m, d) -> {
                        Calendar picked = Calendar.getInstance();
                        picked.set(y, m, d, 9, 0);
                        selectedDate[0] = sdf.format(picked.getTime());
                        tvDate.setText("📅 " + selectedDate[0]);
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH));
            dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            dpd.show();
        });

        // ── Time Picker ──
        btnPickTime.setOnClickListener(v -> {
            TimePickerDialog tpd = new TimePickerDialog(
                    getContext(),
                    R.style.CatCare_DatePickerDialog,
                    (tp, h, mm) -> {
                        selectedHour[0] = h;
                        selectedMin[0]  = mm;
                        tvReminderTime.setText(
                                String.format(" %02d:%02d", h, mm));
                    }, 9, 0, true);
            tpd.show();
        });

        // ── Save ──
        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText() != null
                    ? etTitle.getText().toString().trim() : "";
            String notes = etNotes.getText() != null
                    ? etNotes.getText().toString().trim() : "";

            if (title.isEmpty()) { etTitle.setError("Enter a title"); return; }
            if (selectedDate[0].isEmpty()) {
                Toast.makeText(getContext(), "Please pick a date",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            int pos = spinnerCat.getSelectedItemPosition();
            if (pos < 0 || pos >= cats.size()) {
                Toast.makeText(getContext(), "Please select a cat",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            String catId = cats.get(pos).getId();

            Reminder reminder = new Reminder(
                    UUID.randomUUID().toString(), catId,
                    title, selectedDate[0], notes);
            prefs.saveReminder(reminder);

            if (swNotify.isChecked()) {
                scheduleReminderNotification(
                        reminder, selectedHour[0], selectedMin[0]);
            }

            loadReminders(catId);
            sheet.dismiss();
            Toast.makeText(getContext(), "Reminder set!",
                    Toast.LENGTH_SHORT).show();
        });

        sheet.show();
    }

    private void scheduleReminderNotification(Reminder reminder,
                                              int hour, int min) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(
                    "yyyy-MM-dd", Locale.getDefault());
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(reminder.getDate()));
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, min);
            cal.set(Calendar.SECOND, 0);

            Intent intent = new Intent(getContext(), NotificationReceiver.class);
            intent.putExtra("title",
                    "Reminder: " + reminder.getTitle() + " 💉");
            intent.putExtra("message", "Don't forget today!");

            PendingIntent pi = PendingIntent.getBroadcast(getContext(),
                    reminder.getId().hashCode(), intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            AlarmManager am = (AlarmManager) requireContext()
                    .getSystemService(Context.ALARM_SERVICE);
            if (am == null) return;

            if (android.os.Build.VERSION.SDK_INT
                    >= android.os.Build.VERSION_CODES.S) {
                if (am.canScheduleExactAlarms()) {
                    am.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            cal.getTimeInMillis(), pi);
                } else {
                    am.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            cal.getTimeInMillis(), pi);
                    Toast.makeText(getContext(),
                            "Enable exact alarms in Settings for precise timing.",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                am.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        cal.getTimeInMillis(), pi);
            }
        } catch (Exception e) {
            android.util.Log.e("CatCare", "Scheduler error: " + e.getMessage());
        }
    }
}