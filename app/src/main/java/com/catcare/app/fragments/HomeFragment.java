package com.catcare.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.catcare.app.R;
import com.catcare.app.adapters.CatAdapter;
import com.catcare.app.models.Cat;
import com.catcare.app.utils.PrefsHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.util.List;
import android.content.Intent;
import com.catcare.app.activities.AddCatActivity;

public class HomeFragment extends Fragment {

    private PrefsHelper prefs;
    private RecyclerView rvCats;
    private MaterialCardView cardEmpty;
    private TextView tvStreak, tvCatCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = new PrefsHelper(requireContext());

        tvStreak   = view.findViewById(R.id.tv_streak);
        tvCatCount = view.findViewById(R.id.tv_cat_count);
        cardEmpty  = view.findViewById(R.id.card_empty);
        rvCats     = view.findViewById(R.id.rv_cats);
        MaterialButton btnAddCat = view.findViewById(R.id.btn_add_cat);

        rvCats.setLayoutManager(new LinearLayoutManager(getContext()));

        // TODO: wire to AddCatActivity in Phase 10
        btnAddCat.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), AddCatActivity.class)));

        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        tvStreak.setText(String.valueOf(prefs.getStreak()));

        List<Cat> cats = prefs.getAllCats();
        tvCatCount.setText(String.valueOf(cats.size()));

        if (cats.isEmpty()) {
            cardEmpty.setVisibility(View.VISIBLE);
            rvCats.setVisibility(View.GONE);
        } else {
            cardEmpty.setVisibility(View.GONE);
            rvCats.setVisibility(View.VISIBLE);
            CatAdapter adapter = new CatAdapter(cats, cat -> {
                Intent intent = new Intent(getActivity(), AddCatActivity.class);
                intent.putExtra("cat_id", cat.getId());
                startActivity(intent);
            });
            rvCats.setAdapter(adapter);
        }
    }
}