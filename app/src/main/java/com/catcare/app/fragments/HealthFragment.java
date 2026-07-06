package com.catcare.app.fragments;

import android.app.DatePickerDialog;
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
import com.catcare.app.models.HealthEntry;
import com.catcare.app.utils.PrefsHelper;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class HealthFragment extends Fragment {

    private PrefsHelper prefs;
    private LinearLayout llEntries;
    private Spinner spinnerCat;
    private MaterialCardView cardCurrentWeight;
    private TextView tvCurrentWeight;
    private List<Cat> cats;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_health, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = new PrefsHelper(requireContext());

        spinnerCat = view.findViewById(R.id.spinner_cat_health);
        llEntries = view.findViewById(R.id.ll_health_entries);
        cardCurrentWeight = view.findViewById(R.id.card_current_weight);
        tvCurrentWeight = view.findViewById(R.id.tv_current_weight);
        MaterialButton btnAdd = view.findViewById(R.id.btn_add_health);

        cats = prefs.getAllCats();
        setupCatSpinner();

        btnAdd.setOnClickListener(v -> showAddHealthSheet());
    }

    @Override
    public void onResume() {
        super.onResume();
        cats = prefs.getAllCats();
        setupCatSpinner();
    }

    private void setupCatSpinner() {
        if (cats.isEmpty()) {
            llEntries.removeAllViews();
            cardCurrentWeight.setVisibility(View.GONE);
            TextView empty = new TextView(getContext());
            empty.setText("Add a cat first to track health! 🐱");
            empty.setTextColor(getResources().getColor(R.color.text_secondary, null));
            empty.setPadding(0, 24, 0, 0);
            llEntries.addView(empty);
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
                loadHealthEntries(cats.get(pos).getId());
            }
            @Override
            public void onNothingSelected(AdapterView<?> p) {}
        });
        loadHealthEntries(cats.get(0).getId());
    }

    private void loadHealthEntries(String catId) {
        llEntries.removeAllViews();
        List<HealthEntry> entries = prefs.getHealthForCat(catId);

        // Sort newest first
        Collections.sort(entries, (a, b) -> b.getDate().compareTo(a.getDate()));

        if (entries.isEmpty()) {
            cardCurrentWeight.setVisibility(View.GONE);
            TextView empty = new TextView(getContext());
            empty.setText("No health entries yet. Tap + Add Entry! ");
            empty.setTextColor(getResources().getColor(R.color.text_secondary, null));
            empty.setPadding(0, 24, 0, 0);
            llEntries.addView(empty);
            return;
        }

        // Show latest weight
        cardCurrentWeight.setVisibility(View.VISIBLE);
        tvCurrentWeight.setText(entries.get(0).getWeight() + " kg");

        for (HealthEntry entry : entries) {
            View card = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_health, llEntries, false);
            ((TextView) card.findViewById(R.id.tv_health_emoji)).setText(entry.getTypeEmoji());
            ((TextView) card.findViewById(R.id.tv_health_date)).setText(entry.getDate());
            ((TextView) card.findViewById(R.id.tv_health_weight)).setText(
                    "⚖️ " + entry.getWeight() + " kg");
            TextView tvNotes = card.findViewById(R.id.tv_health_notes);
            if (entry.getNotes() != null && !entry.getNotes().isEmpty()) {
                tvNotes.setText(entry.getNotes());
                tvNotes.setVisibility(View.VISIBLE);
            } else {
                tvNotes.setVisibility(View.GONE);
            }
            card.findViewById(R.id.btn_delete_health).setOnClickListener(v -> {
                prefs.deleteHealthEntry(entry.getId());
                loadHealthEntries(catId);
            });
            llEntries.addView(card);
        }
    }

    private void showAddHealthSheet() {
        if (cats.isEmpty()) {
            Toast.makeText(getContext(), "Please add a cat first! 🐾", Toast.LENGTH_SHORT).show();
            return;
        }
        BottomSheetDialog sheet = new BottomSheetDialog(requireContext());
        View sheetView = LayoutInflater.from(getContext()).inflate(R.layout.sheet_add_health, null);
        sheet.setContentView(sheetView);

        RadioGroup rgType = sheetView.findViewById(R.id.rg_entry_type);
        TextView tvDate = sheetView.findViewById(R.id.tv_selected_date);
        TextInputEditText etWeight = sheetView.findViewById(R.id.et_health_weight);
        TextInputEditText etNotes  = sheetView.findViewById(R.id.et_health_notes);
        MaterialButton btnPickDate = sheetView.findViewById(R.id.btn_pick_date);
        MaterialButton btnSave     = sheetView.findViewById(R.id.btn_save_health);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        final String[] selectedDate = {sdf.format(Calendar.getInstance().getTime())};
        tvDate.setText("📅 " + selectedDate[0]);

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

        btnSave.setOnClickListener(v -> {
            String weightStr = etWeight.getText() != null ? etWeight.getText().toString().trim() : "";
            String notes = etNotes.getText() != null ? etNotes.getText().toString().trim() : "";

            if (weightStr.isEmpty()) { etWeight.setError("Enter weight"); return; }

            float weight;
            try { weight = Float.parseFloat(weightStr); }
            catch (NumberFormatException e) {
                etWeight.setError("Invalid number");
                return;
            }

            String type;
            int checkedId = rgType.getCheckedRadioButtonId();
            if (checkedId == R.id.rb_symptom) type = "symptom";
            else if (checkedId == R.id.rb_vaccine) type = "vaccine";
            else if (checkedId == R.id.rb_general) type = "general";
            else type = "checkup";

            int pos = spinnerCat.getSelectedItemPosition();
            if (pos < 0 || pos >= cats.size()) {
                Toast.makeText(getContext(), "Please select a cat", Toast.LENGTH_SHORT).show();
                return;
            }
            String catId = cats.get(pos).getId();

            HealthEntry entry = new HealthEntry(UUID.randomUUID().toString(), catId,
                    selectedDate[0], weight, type, notes);
            prefs.saveHealthEntry(entry);

            loadHealthEntries(catId);
            sheet.dismiss();
            Toast.makeText(getContext(), "Health entry saved!", Toast.LENGTH_SHORT).show();
        });

        sheet.show();
    }
}