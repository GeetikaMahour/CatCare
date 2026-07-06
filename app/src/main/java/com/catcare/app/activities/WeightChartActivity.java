package com.catcare.app.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.catcare.app.R;
import com.catcare.app.models.Cat;
import com.catcare.app.models.HealthEntry;
import com.catcare.app.utils.PrefsHelper;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WeightChartActivity extends AppCompatActivity {

    private PrefsHelper prefs;
    private List<Cat> cats;
    private LineChart weightChart;
    private TextView tvCurrentWeight, tvLowestWeight, tvHighestWeight;
    private TextView tvChartEmpty, tvTrendEmoji, tvTrendTitle, tvTrendDesc;
    private MaterialCardView cardTrend;
    private Spinner spinnerCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_chart);

        prefs = new PrefsHelper(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        weightChart      = findViewById(R.id.weight_chart);
        spinnerCat       = findViewById(R.id.spinner_cat_chart);
        tvCurrentWeight  = findViewById(R.id.tv_current_weight);
        tvLowestWeight   = findViewById(R.id.tv_lowest_weight);
        tvHighestWeight  = findViewById(R.id.tv_highest_weight);
        tvChartEmpty     = findViewById(R.id.tv_chart_empty);
        tvTrendEmoji     = findViewById(R.id.tv_trend_emoji);
        tvTrendTitle     = findViewById(R.id.tv_trend_title);
        tvTrendDesc      = findViewById(R.id.tv_trend_desc);
        cardTrend        = findViewById(R.id.card_trend);

        setupChart();

        cats = prefs.getAllCats();
        if (cats.isEmpty()) {
            showEmptyState();
            return;
        }

        String[] catNames = new String[cats.size()];
        for (int i = 0; i < cats.size(); i++) catNames[i] = cats.get(i).getName();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, catNames);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerCat.setAdapter(adapter);
        spinnerCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                loadChartForCat(cats.get(pos).getId());
            }
            @Override
            public void onNothingSelected(AdapterView<?> p) {}
        });

        loadChartForCat(cats.get(0).getId());
    }

    private void setupChart() {
        weightChart.getDescription().setEnabled(false);
        weightChart.setTouchEnabled(true);
        weightChart.setDragEnabled(true);
        weightChart.setScaleEnabled(true);
        weightChart.setPinchZoom(true);
        weightChart.setDrawGridBackground(false);
        weightChart.getLegend().setEnabled(false);
        weightChart.setExtraBottomOffset(10f);

        // X Axis
        XAxis xAxis = weightChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.parseColor("#9C6B8E"));
        xAxis.setTextSize(10f);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(-30f);

        // Y Axis
        YAxis leftAxis = weightChart.getAxisLeft();
        leftAxis.setTextColor(Color.parseColor("#9C6B8E"));
        leftAxis.setTextSize(10f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#F8BBD0"));
        leftAxis.setGridLineWidth(1f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.1f kg", value);
            }
        });

        weightChart.getAxisRight().setEnabled(false);
    }

    private void loadChartForCat(String catId) {
        List<HealthEntry> entries = prefs.getHealthForCat(catId);

        // Sort oldest first for chart
        Collections.sort(entries, (a, b) -> a.getDate().compareTo(b.getDate()));

        // Filter entries that have weight > 0
        List<HealthEntry> weighted = new ArrayList<>();
        for (HealthEntry e : entries) {
            if (e.getWeight() > 0) weighted.add(e);
        }

        if (weighted.isEmpty()) {
            showEmptyState();
            return;
        }

        weightChart.setVisibility(View.VISIBLE);
        tvChartEmpty.setVisibility(View.GONE);
        cardTrend.setVisibility(View.VISIBLE);

        // Build chart entries
        List<Entry> chartEntries = new ArrayList<>();
        final List<String> dateLabels = new ArrayList<>();

        for (int i = 0; i < weighted.size(); i++) {
            chartEntries.add(new Entry(i, weighted.get(i).getWeight()));
            // Short date label e.g. "Jun 24"
            String date = weighted.get(i).getDate();
            try {
                String[] parts = date.split("-");
                String[] months = {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                int month = Integer.parseInt(parts[1]);
                dateLabels.add(months[month] + " " + parts[2]);
            } catch (Exception e) {
                dateLabels.add(date);
            }
        }

        // X Axis labels
        weightChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < dateLabels.size()) return dateLabels.get(index);
                return "";
            }
        });
        weightChart.getXAxis().setLabelCount(Math.min(weighted.size(), 6));

        // Dataset styling
        LineDataSet dataSet = new LineDataSet(chartEntries, "Weight");
        dataSet.setColor(Color.parseColor("#F48FB1"));
        dataSet.setCircleColor(Color.parseColor("#C2185B"));
        dataSet.setCircleHoleColor(Color.parseColor("#FCE4EC"));
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleRadius(5f);
        dataSet.setCircleHoleRadius(3f);
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(Color.parseColor("#9C6B8E"));
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.1f kg", value);
            }
        });
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        // Fill under line
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#F8BBD0"));
        dataSet.setFillAlpha(60);

        weightChart.setData(new LineData(dataSet));
        weightChart.invalidate();
        weightChart.animateX(800);

        // Summary stats
        float current = weighted.get(weighted.size() - 1).getWeight();
        float lowest  = Float.MAX_VALUE;
        float highest = Float.MIN_VALUE;

        for (HealthEntry e : weighted) {
            if (e.getWeight() < lowest) lowest = e.getWeight();
            if (e.getWeight() > highest) highest = e.getWeight();
        }

        tvCurrentWeight.setText(current + " kg");
        tvLowestWeight.setText(lowest + " kg");
        tvHighestWeight.setText(highest + " kg");

        // Trend analysis
        showTrend(weighted);
    }

    private void showTrend(List<HealthEntry> entries) {
        if (entries.size() < 2) {
            cardTrend.setVisibility(View.GONE);
            return;
        }

        float first = entries.get(0).getWeight();
        float last  = entries.get(entries.size() - 1).getWeight();
        float diff  = last - first;
        float diffAbs = Math.abs(diff);

        cardTrend.setVisibility(View.VISIBLE);

        if (diffAbs < 0.1f) {
            tvTrendEmoji.setText("✅");
            tvTrendTitle.setText("Stable weight");
            tvTrendDesc.setText("Your cat's weight has been consistent. Great job!");
            cardTrend.setCardBackgroundColor(getColor(R.color.mint_light));
        } else if (diff > 0) {
            if (diff > 0.5f) {
                tvTrendEmoji.setText("⚠️");
                tvTrendTitle.setText("Notable weight gain (+" + String.format("%.1f", diff) + " kg)");
                tvTrendDesc.setText("Consider reviewing diet and portion sizes. Consult your vet if gain is rapid.");
                cardTrend.setCardBackgroundColor(getColor(R.color.peach_light));
            } else {
                tvTrendEmoji.setText("📈");
                tvTrendTitle.setText("Slight weight gain (+" + String.format("%.1f", diff) + " kg)");
                tvTrendDesc.setText("Minor gain — monitor portions and keep logging.");
                cardTrend.setCardBackgroundColor(getColor(R.color.peach_light));
            }
        } else {
            if (diffAbs > 0.5f) {
                tvTrendEmoji.setText("🚨");
                tvTrendTitle.setText("Notable weight loss (-" + String.format("%.1f", diffAbs) + " kg)");
                tvTrendDesc.setText("Significant loss can indicate illness. See your vet.");
                cardTrend.setCardBackgroundColor(getColor(R.color.color_alert));
            } else {
                tvTrendEmoji.setText("📉");
                tvTrendTitle.setText("Slight weight loss (-" + String.format("%.1f", diffAbs) + " kg)");
                tvTrendDesc.setText("Minor loss — keep monitoring and logging regularly.");
                cardTrend.setCardBackgroundColor(getColor(R.color.pink_light));
            }
        }
    }

    private void showEmptyState() {
        weightChart.setVisibility(View.GONE);
        tvChartEmpty.setVisibility(View.VISIBLE);
        cardTrend.setVisibility(View.GONE);
        tvCurrentWeight.setText("—");
        tvLowestWeight.setText("—");
        tvHighestWeight.setText("—");
    }
}