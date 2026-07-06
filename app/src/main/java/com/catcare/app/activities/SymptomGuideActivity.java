package com.catcare.app.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.catcare.app.R;
import com.catcare.app.models.Symptom;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class SymptomGuideActivity extends AppCompatActivity {

    private LinearLayout llSymptoms;
    private List<Symptom> allSymptoms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom_guide);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        llSymptoms = findViewById(R.id.ll_symptoms);
        TextInputEditText etSearch = findViewById(R.id.et_search);

        allSymptoms = buildSymptomList();
        renderSymptoms(allSymptoms);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                filterSymptoms(s.toString().trim().toLowerCase());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void filterSymptoms(String query) {
        if (query.isEmpty()) {
            renderSymptoms(allSymptoms);
            return;
        }
        List<Symptom> filtered = new ArrayList<>();
        for (Symptom s : allSymptoms) {
            if (s.getSymptom().toLowerCase().contains(query)
                    || s.getPossibleCauses().toLowerCase().contains(query)
                    || s.getAction().toLowerCase().contains(query)) {
                filtered.add(s);
            }
        }
        renderSymptoms(filtered);
    }

    private void renderSymptoms(List<Symptom> symptoms) {
        llSymptoms.removeAllViews();

        if (symptoms.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No symptoms found 🐾");
            empty.setTextColor(getColor(R.color.text_secondary));
            empty.setPadding(0, 32, 0, 0);
            empty.setGravity(android.view.Gravity.CENTER);
            llSymptoms.addView(empty);
            return;
        }

        for (Symptom symptom : symptoms) {
            View card = LayoutInflater.from(this)
                    .inflate(R.layout.item_symptom, llSymptoms, false);

            TextView tvEmoji   = card.findViewById(R.id.tv_symptom_emoji);
            TextView tvName    = card.findViewById(R.id.tv_symptom_name);
            TextView tvUrgency = card.findViewById(R.id.tv_urgency_badge);
            TextView tvCauses  = card.findViewById(R.id.tv_causes);
            TextView tvAction  = card.findViewById(R.id.tv_action);

            tvEmoji.setText(symptom.getEmoji());
            tvName.setText(symptom.getSymptom());
            tvCauses.setText(symptom.getPossibleCauses());
            tvAction.setText(symptom.getAction());

            switch (symptom.getUrgency()) {
                case "high":
                    tvUrgency.setText("🔴 See vet soon");
                    tvUrgency.setBackground(getDrawable(R.drawable.urgency_high));
                    tvUrgency.setTextColor(Color.parseColor("#B71C1C"));
                    break;
                case "medium":
                    tvUrgency.setText("🟡 Watch closely");
                    tvUrgency.setBackground(getDrawable(R.drawable.urgency_medium));
                    tvUrgency.setTextColor(Color.parseColor("#F57F17"));
                    break;
                default:
                    tvUrgency.setText("🟢 Monitor");
                    tvUrgency.setBackground(getDrawable(R.drawable.urgency_low));
                    tvUrgency.setTextColor(Color.parseColor("#1B5E20"));
                    break;
            }

            llSymptoms.addView(card);
        }
    }

    private List<Symptom> buildSymptomList() {
        List<Symptom> list = new ArrayList<>();

        // ── EATING & DIGESTION ──
        list.add(new Symptom("🤢", "Vomiting (occasional)",
                "Hairballs, eating too fast, mild stomach upset",
                "Monitor for 24 hrs. Withhold food for 2 hrs then offer small portions.",
                "low"));

        list.add(new Symptom("🤮", "Vomiting (frequent / blood)",
                "Poisoning, obstruction, serious illness, parasites",
                "See vet immediately. Do not wait.",
                "high"));

        list.add(new Symptom("🍽️", "Not eating (1 day)",
                "Stress, new food, minor illness, dental pain",
                "Try a different food. See vet if no improvement in 24 hours.",
                "medium"));

        list.add(new Symptom("🚫", "Not eating (2+ days)",
                "Serious illness, liver disease (hepatic lipidosis risk)",
                "See vet urgently. Cats can develop liver disease within 48 hrs of not eating.",
                "high"));

        list.add(new Symptom("💧", "Drinking excessively",
                "Diabetes, kidney disease, hyperthyroidism",
                "Note amount and frequency. See vet — blood test needed.",
                "medium"));

        list.add(new Symptom("🚽", "Diarrhea (1–2 days)",
                "Diet change, stress, parasites, mild infection",
                "Keep hydrated. Feed bland food. See vet if blood present or lasts 2+ days.",
                "low"));

        list.add(new Symptom("🩸", "Blood in stool",
                "Parasites, infection, colitis, trauma",
                "See vet within 24 hrs.",
                "high"));

        list.add(new Symptom("😣", "Straining to defecate",
                "Constipation, obstruction, megacolon",
                "Ensure hydration. See vet if no bowel movement in 48 hrs.",
                "medium"));

        // ── URINARY ──
        list.add(new Symptom("🚾", "Straining to urinate",
                "UTI, bladder stones, FLUTD, urinary blockage",
                "URGENT if male cat — urinary blockage is life-threatening. See vet immediately.",
                "high"));

        list.add(new Symptom("💦", "Urinating outside litter box",
                "UTI, stress, litter box aversion, kidney disease",
                "Rule out medical cause first. Vet check recommended.",
                "medium"));

        list.add(new Symptom("🩸", "Blood in urine",
                "UTI, bladder stones, trauma, FLUTD",
                "See vet within 24 hrs.",
                "high"));

        // ── BREATHING & NOSE ──
        list.add(new Symptom("🤧", "Sneezing (occasional)",
                "Dust, allergens, mild irritation",
                "Monitor. No action needed unless persistent.",
                "low"));

        list.add(new Symptom("😤", "Sneezing (frequent, with discharge)",
                "Upper respiratory infection (cat flu), herpesvirus, calicivirus",
                "Keep warm and comfortable. See vet if discharge is thick/coloured.",
                "medium"));

        list.add(new Symptom("😮‍💨", "Laboured / rapid breathing",
                "Asthma, heart disease, fluid in lungs, trauma",
                "See vet immediately. This is an emergency.",
                "high"));

        list.add(new Symptom("👃", "Runny nose (clear)",
                "Mild allergy, viral infection",
                "Monitor. See vet if worsens or lasts 5+ days.",
                "low"));

        // ── EYES ──
        list.add(new Symptom("👁️", "Watery eyes",
                "Allergy, blocked tear duct, conjunctivitis",
                "Clean gently with damp cloth. See vet if redness or swelling develops.",
                "low"));

        list.add(new Symptom("🔴", "Red / swollen eyes",
                "Conjunctivitis, infection, injury, uveitis",
                "See vet — eye problems can worsen quickly.",
                "medium"));

        list.add(new Symptom("🌫️", "Cloudy eye / third eyelid visible",
                "Infection, injury, illness, glaucoma",
                "See vet within 24 hrs.",
                "high"));

        // ── SKIN & COAT ──
        list.add(new Symptom("🐛", "Excessive scratching",
                "Fleas, mites, allergies, dry skin",
                "Check for fleas. Apply vet-approved flea treatment.",
                "low"));

        list.add(new Symptom("🩹", "Hair loss / bald patches",
                "Over-grooming from stress, ringworm, allergies, hormonal issue",
                "See vet — ringworm is contagious to humans.",
                "medium"));

        list.add(new Symptom("🔴", "Skin sores or scabs",
                "Flea allergy, fight wounds, skin infection",
                "See vet if spreading or infected.",
                "medium"));

        // ── BEHAVIOUR ──
        list.add(new Symptom("😴", "Lethargy / low energy",
                "Illness, pain, infection, depression",
                "Monitor for 12 hrs. See vet if persistent or combined with other symptoms.",
                "medium"));

        list.add(new Symptom("😾", "Sudden aggression",
                "Pain, fear, illness, hormonal changes",
                "Don't punish. See vet to rule out pain-related cause.",
                "medium"));

        list.add(new Symptom("😰", "Hiding constantly",
                "Stress, illness, pain, fear",
                "Give space. See vet if accompanied by other symptoms.",
                "low"));

        list.add(new Symptom("😵", "Disorientation / head tilting",
                "Ear infection, vestibular disease, neurological issue",
                "See vet as soon as possible.",
                "high"));

        list.add(new Symptom("🌀", "Seizures",
                "Epilepsy, toxin exposure, brain issue, low blood sugar",
                "Do not restrain. Time the seizure. See vet immediately after.",
                "high"));

        // ── WEIGHT & APPETITE ──
        list.add(new Symptom("📉", "Sudden weight loss",
                "Hyperthyroidism, diabetes, cancer, kidney disease",
                "See vet — weight loss in cats is rarely normal.",
                "medium"));

        list.add(new Symptom("📈", "Rapid weight gain",
                "Overfeeding, hypothyroidism, fluid retention",
                "Review diet. See vet for assessment.",
                "low"));

        // ── EARS ──
        list.add(new Symptom("👂", "Scratching ears / head shaking",
                "Ear mites, yeast infection, bacterial infection",
                "Check for dark discharge in ears. See vet for treatment.",
                "medium"));

        list.add(new Symptom("🦠", "Smelly / dark ear discharge",
                "Ear infection, mites",
                "See vet — ear infections need specific treatment.",
                "medium"));

        // ── MOUTH ──
        list.add(new Symptom("🦷", "Bad breath",
                "Dental disease, kidney disease, diabetes",
                "Book dental check. Bad breath is not normal in cats.",
                "medium"));

        list.add(new Symptom("🩸", "Bleeding gums / mouth",
                "Gingivitis, stomatitis, trauma",
                "See vet. Dental disease causes pain affecting eating.",
                "high"));

        // ── LIMBS ──
        list.add(new Symptom("🦵", "Limping",
                "Injury, sprain, arthritis, abscess from fight wound",
                "Check paws for wounds. See vet if not weight-bearing or worsening.",
                "medium"));

        list.add(new Symptom("💥", "Dragging hind legs",
                "Aortic thromboembolism (blood clot), spinal injury",
                "Emergency. See vet immediately.",
                "high"));

        return list;
    }
}