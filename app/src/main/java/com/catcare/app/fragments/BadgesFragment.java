package com.catcare.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.catcare.app.R;
import com.catcare.app.utils.PrefsHelper;
import com.google.android.material.card.MaterialCardView;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BadgesFragment extends Fragment {

    private PrefsHelper prefs;

    // Badge id → [emoji, title, description]
    private static final Map<String, String[]> BADGE_DEFS = new LinkedHashMap<>();
    static {
        BADGE_DEFS.put("first_cat",  new String[]{"", "First Kitty!",   "Add your first cat"});
        BADGE_DEFS.put("cat_family", new String[]{"", "Cat Family",     "Add 3 or more cats"});
        BADGE_DEFS.put("meal_plan",  new String[]{"", "Meal Planner",  "Set up a feeding schedule"});
        BADGE_DEFS.put("health_log", new String[]{"", "Health Tracker", "Log your first health entry"});
        BADGE_DEFS.put("vet_ready",  new String[]{"", "Vet Ready",      "Add a vet reminder"});
        BADGE_DEFS.put("streak_3",   new String[]{"", "3-Day Streak",   "Log activity 3 days in a row"});
        BADGE_DEFS.put("streak_7",   new String[]{"", "Week Warrior",   "7-day streak"});
        BADGE_DEFS.put("streak_30",  new String[]{"", "Cat Champion",   "30-day streak!"});
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_badges, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = new PrefsHelper(requireContext());

        TextView tvStreak = view.findViewById(R.id.tv_streak_count);
        TextView tvBadgeCount = view.findViewById(R.id.tv_badge_count);
        ProgressBar progressBadges = view.findViewById(R.id.progress_badges);
        LinearLayout llBadges = view.findViewById(R.id.ll_badges);

        int streak = prefs.getStreak();
        List<String> earned = prefs.getBadges();

        tvStreak.setText(streak + " days 🔥");
        tvBadgeCount.setText(earned.size() + " / " + BADGE_DEFS.size());
        progressBadges.setProgress(earned.size());

        llBadges.removeAllViews();
        for (Map.Entry<String, String[]> entry : BADGE_DEFS.entrySet()) {
            String badgeId = entry.getKey();
            String[] info = entry.getValue();
            boolean isEarned = earned.contains(badgeId);

            View badgeView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_badge, llBadges, false);

            TextView tvEmoji  = badgeView.findViewById(R.id.tv_badge_emoji);
            TextView tvTitle  = badgeView.findViewById(R.id.tv_badge_title);
            TextView tvDesc   = badgeView.findViewById(R.id.tv_badge_desc);
            TextView tvStatus = badgeView.findViewById(R.id.tv_badge_status);
            MaterialCardView card = badgeView.findViewById(R.id.card_badge);

            tvEmoji.setText(info[0]);
            tvTitle.setText(info[1]);
            tvDesc.setText(info[2]);

            if (isEarned) {
                tvEmoji.setAlpha(1f);
                tvTitle.setAlpha(1f);
                tvDesc.setAlpha(1f);
                tvStatus.setText("✅");
                card.setCardBackgroundColor(
                        requireContext().getColor(R.color.pink_light));
                card.setStrokeColor(requireContext().getColor(R.color.pink_primary));
            } else {
                tvEmoji.setAlpha(0.3f);
                tvTitle.setAlpha(0.4f);
                tvDesc.setAlpha(0.4f);
                tvStatus.setText("🔒");
                card.setCardBackgroundColor(
                        requireContext().getColor(R.color.bg_cream));
                card.setStrokeColor(requireContext().getColor(R.color.pink_soft));
            }

            llBadges.addView(badgeView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh in case badges/streak changed elsewhere
        if (getView() != null) {
            onViewCreated(getView(), null);
        }
    }
}