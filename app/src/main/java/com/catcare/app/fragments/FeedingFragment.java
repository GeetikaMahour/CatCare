package com.catcare.app.fragments;

import android.app.AlarmManager;
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
import com.catcare.app.models.MealEntry;
import com.catcare.app.utils.NotificationReceiver;
import com.catcare.app.utils.PrefsHelper;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class FeedingFragment extends Fragment {

    private PrefsHelper prefs;
    private LinearLayout llMeals;
    private Spinner spinnerCat;
    private List<Cat> cats;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feeding, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = new PrefsHelper(requireContext());

        spinnerCat = view.findViewById(R.id.spinner_cat);
        llMeals    = view.findViewById(R.id.ll_meals);
        MaterialButton btnAddMeal = view.findViewById(R.id.btn_add_meal);

        cats = prefs.getAllCats();
        setupCatSpinner();
        btnAddMeal.setOnClickListener(v -> showAddMealSheet());
    }

    @Override
    public void onResume() {
        super.onResume();
        cats = prefs.getAllCats();
        setupCatSpinner();
    }

    private void setupCatSpinner() {
        if (cats.isEmpty()) {
            llMeals.removeAllViews();
            TextView empty = new TextView(getContext());
            empty.setText("Add a cat first to set up meals! 🐱");
            empty.setTextColor(getResources().getColor(R.color.text_secondary, null));
            empty.setPadding(0, 24, 0, 0);
            llMeals.addView(empty);
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
                loadMeals(cats.get(pos).getId());
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
        loadMeals(cats.get(0).getId());
    }

    private void loadMeals(String catId) {
        llMeals.removeAllViews();
        List<MealEntry> meals = prefs.getMealsForCat(catId);
        if (meals.isEmpty()) {
            TextView empty = new TextView(getContext());
            empty.setText("No meals yet. Tap + Add Meal! 🍽️");
            empty.setTextColor(getResources().getColor(R.color.text_secondary, null));
            empty.setPadding(0, 24, 0, 0);
            llMeals.addView(empty);
            return;
        }
        for (MealEntry meal : meals) {
            View card = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_meal, llMeals, false);
            ((TextView) card.findViewById(R.id.tv_meal_name))
                    .setText("🍽️ " + meal.getMealName());
            ((TextView) card.findViewById(R.id.tv_meal_time))
                    .setText("⏰ " + meal.getTimeHHMM());
            ((TextView) card.findViewById(R.id.tv_meal_food))
                    .setText(meal.getFoodType() + " • " + meal.getAmountGrams() + "g");
            card.findViewById(R.id.btn_delete_meal).setOnClickListener(v -> {
                prefs.deleteMeal(meal.getId());
                loadMeals(catId);
            });
            llMeals.addView(card);
        }
    }

    private void showAddMealSheet() {
        if (cats.isEmpty()) {
            Toast.makeText(getContext(), "Please add a cat first! 🐾",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        BottomSheetDialog sheet = new BottomSheetDialog(requireContext());
        View sheetView = LayoutInflater.from(getContext())
                .inflate(R.layout.sheet_add_meal, null);
        sheet.setContentView(sheetView);

        TextInputEditText etMealName = sheetView.findViewById(R.id.et_meal_name);
        TextInputEditText etFoodType = sheetView.findViewById(R.id.et_food_type);
        TextInputEditText etAmount   = sheetView.findViewById(R.id.et_amount);
        TextView tvTime              = sheetView.findViewById(R.id.tv_selected_time);
        androidx.appcompat.widget.SwitchCompat swReminder =
                sheetView.findViewById(R.id.sw_reminder);
        MaterialButton btnPickTime   = sheetView.findViewById(R.id.btn_pick_time);
        MaterialButton btnSave       = sheetView.findViewById(R.id.btn_save_meal);

        final String[] selectedTime = {"08:00"};
        tvTime.setText("⏰ " + selectedTime[0]);

        btnPickTime.setOnClickListener(v -> {
            TimePickerDialog tpd = new TimePickerDialog(
                    getContext(),
                    android.R.style.Theme_Material_Light_Dialog,
                    (tp, h, m) -> {
                        selectedTime[0] = String.format("%02d:%02d", h, m);
                        tvTime.setText("⏰ " + selectedTime[0]);
                    }, 8, 0, true);
            tpd.show();
        });

        btnSave.setOnClickListener(v -> {
            String name = etMealName.getText() != null
                    ? etMealName.getText().toString().trim() : "";
            String food = etFoodType.getText() != null
                    ? etFoodType.getText().toString().trim() : "";
            String amountStr = etAmount.getText() != null
                    ? etAmount.getText().toString().trim() : "0";

            if (name.isEmpty()) { etMealName.setError("Enter meal name"); return; }

            int pos = spinnerCat.getSelectedItemPosition();
            if (pos < 0 || pos >= cats.size()) {
                Toast.makeText(getContext(), "Please select a cat",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            String catId = cats.get(pos).getId();

            float amount = 0;
            try { amount = Float.parseFloat(amountStr); }
            catch (NumberFormatException ignored) {}

            MealEntry meal = new MealEntry(UUID.randomUUID().toString(), catId,
                    name, food, amount, selectedTime[0], swReminder.isChecked());
            prefs.saveMeal(meal);
            prefs.checkMealBadge();

            if (swReminder.isChecked()) scheduleReminder(meal);
            loadMeals(catId);
            sheet.dismiss();
            Toast.makeText(getContext(), "Meal saved! 🍽️", Toast.LENGTH_SHORT).show();
        });

        sheet.show();
    }

    private void scheduleReminder(MealEntry meal) {
        String[] parts = meal.getTimeHHMM().split(":");
        int hour = Integer.parseInt(parts[0]);
        int min  = Integer.parseInt(parts[1]);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, min);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if (cal.getTimeInMillis() <= System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        int requestId = meal.getId().hashCode();

        // Use explicit Intent with component to ensure BroadcastReceiver is found
        Intent intent = new Intent(requireContext(), NotificationReceiver.class);
        intent.putExtra("title", "Feeding Time! 🍽️");
        intent.putExtra("message", "Time to feed " + meal.getMealName() + "!");
        intent.putExtra("request_id", requestId);
        intent.putExtra("hour", hour);
        intent.putExtra("minute", min);

        PendingIntent pi = PendingIntent.getBroadcast(
                requireContext(),
                requestId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager am = (AlarmManager) requireContext()
                .getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        try {
            if (android.os.Build.VERSION.SDK_INT
                    >= android.os.Build.VERSION_CODES.S) {
                if (am.canScheduleExactAlarms()) {
                    am.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
                    Toast.makeText(getContext(),
                            "⏰ Reminder set for " + meal.getTimeHHMM(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    am.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
                    Toast.makeText(getContext(),
                            "Reminder set! Go to Settings → Apps → CatCare → Alarms & Reminders → Allow",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                am.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
                Toast.makeText(getContext(),
                        "⏰ Reminder set for " + meal.getTimeHHMM(),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            android.util.Log.e("CatCare", "Schedule error: " + e.getMessage());
        }
    }
}