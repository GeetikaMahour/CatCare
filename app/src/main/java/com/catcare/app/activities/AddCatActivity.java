package com.catcare.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.catcare.app.R;
import com.catcare.app.models.Cat;
import com.catcare.app.utils.PrefsHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import de.hdodenhof.circleimageview.CircleImageView;

public class AddCatActivity extends AppCompatActivity {

    private TextInputEditText etName, etBreed, etAge, etWeight;
    private RadioGroup rgGender;
    private CircleImageView imgCatPhoto;
    private String selectedPhotoUri = "";
    private String editCatId = null;
    private PrefsHelper prefs;

    private final ActivityResultLauncher<String> photoPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedPhotoUri = uri.toString();
                    imgCatPhoto.setImageURI(uri);
                    try {
                        getContentResolver().takePersistableUriPermission(
                                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (SecurityException ignored) {}
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cat);

        prefs = new PrefsHelper(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        etName    = findViewById(R.id.et_cat_name);
        etBreed   = findViewById(R.id.et_cat_breed);
        etAge     = findViewById(R.id.et_cat_age);
        etWeight  = findViewById(R.id.et_cat_weight);
        rgGender  = findViewById(R.id.rg_gender);
        imgCatPhoto = findViewById(R.id.img_cat_photo);
        MaterialButton btnPickPhoto = findViewById(R.id.btn_pick_photo);
        MaterialButton btnSave = findViewById(R.id.btn_save_cat);

        toolbar.setNavigationOnClickListener(v -> finish());

        editCatId = getIntent().getStringExtra("cat_id");
        if (editCatId != null) {
            toolbar.setTitle("Edit Cat 🐾");
            populateExistingCat();
        }

        btnPickPhoto.setOnClickListener(v -> photoPickerLauncher.launch("image/*"));
        btnSave.setOnClickListener(v -> saveCat());
    }

    private void populateExistingCat() {
        for (Cat c : prefs.getAllCats()) {
            if (c.getId().equals(editCatId)) {
                etName.setText(c.getName());
                etBreed.setText(c.getBreed());
                etAge.setText(String.valueOf(c.getAge()));
                etWeight.setText(String.valueOf(c.getWeight()));
                rgGender.check("male".equals(c.getGender()) ? R.id.rb_male : R.id.rb_female);
                if (c.getPhotoUri() != null && !c.getPhotoUri().isEmpty()) {
                    selectedPhotoUri = c.getPhotoUri();
                    try {
                        imgCatPhoto.setImageURI(Uri.parse(c.getPhotoUri()));
                    } catch (Exception ignored) {}
                }
                break;
            }
        }
    }

    private void saveCat() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String breed = etBreed.getText() != null ? etBreed.getText().toString().trim() : "";
        String ageStr = etAge.getText() != null ? etAge.getText().toString().trim() : "";
        String weightStr = etWeight.getText() != null ? etWeight.getText().toString().trim() : "";

        if (TextUtils.isEmpty(name)) {
            etName.setError("Please enter your cat's name 🐾");
            return;
        }
        if (TextUtils.isEmpty(ageStr)) {
            etAge.setError("Enter age");
            return;
        }
        if (TextUtils.isEmpty(weightStr)) {
            etWeight.setError("Enter weight");
            return;
        }

        float age, weight;
        try {
            age = Float.parseFloat(ageStr);
            weight = Float.parseFloat(weightStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
            return;
        }

        String gender = (rgGender.getCheckedRadioButtonId() == R.id.rb_male) ? "male" : "female";

        Cat cat = new Cat(editCatId, name, breed, age, weight, gender, selectedPhotoUri);
        prefs.saveCat(cat);
        prefs.checkProfileBadge();

        Toast.makeText(this, name + " saved! 🎉", Toast.LENGTH_SHORT).show();
        setResult(Activity.RESULT_OK);
        finish();
    }
}