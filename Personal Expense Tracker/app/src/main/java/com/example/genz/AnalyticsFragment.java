package com.example.finora;


import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.myapplication.Model.Data;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Calendar;
import java.util.Date;

public class AnalyticsFragment extends Fragment {

    PieChart pieChart;
    BarChart barChart;

    TextView totalSpentView, breakdownTextView;

    float latestPieTotal = 1f; // to prevent divide-by-zero
    List<PieEntry> latestPieEntries = new ArrayList<>();

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mIncomeDatabase, mExpenseDatabase;
    private String uid;

    // For chart data
    private List<Data> incomeList = new ArrayList<>();
    private List<Data> expenseList = new ArrayList<>();

    /**
     * Fetch all income and expense data from Firebase, populate lists, and update charts.
     */
    private void fetchAllDataAndLoadCharts() {
        if (mIncomeDatabase == null || mExpenseDatabase == null) return;
        incomeList.clear();
        expenseList.clear();
        // Fetch income data
        mIncomeDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                incomeList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Data data = ds.getValue(Data.class);
                    if (data != null) incomeList.add(data);
                }
                // Fetch expense data only after income is loaded
                mExpenseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        expenseList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Data data = ds.getValue(Data.class);
                            if (data != null) expenseList.add(data);
                        }
                        // Now update charts
                        loadMonthlyBarChart();
                        loadMonthlyPieChart();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Optionally show error
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Optionally show error
            }
        });
    }

    @Override
    public View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analytics, container, false);

        pieChart = view.findViewById(R.id.pieChart);
        barChart = view.findViewById(R.id.barChart);

        totalSpentView = view.findViewById(R.id.totalSpentText);
        breakdownTextView = view.findViewById(R.id.breakdownTextView);

        // Button references
        Button btnLast6Months = view.findViewById(R.id.btnLast6Months);
        Button btnDaily = view.findViewById(R.id.btnDaily);
        Button btnPieMonthly = view.findViewById(R.id.btnPieMonthly);
        Button btnPieDaily = view.findViewById(R.id.btnPieDaily);

        // Firebase init
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            uid = mUser.getUid();
            mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
            mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);
        }

        // Default selection: monthly for both charts
        // Button highlight helper
        View.OnClickListener barListener = v -> {
            if (v == btnDaily) {
                highlightBarButton(btnDaily, btnLast6Months);
                loadDailyBarChart();
            } else {
                highlightBarButton(btnLast6Months, btnDaily);
                loadMonthlyBarChart();
            }
        };
        View.OnClickListener pieListener = v -> {
            if (v == btnPieDaily) {
                highlightPieButton(btnPieDaily, btnPieMonthly);
                loadDailyPieChart();
            } else {
                highlightPieButton(btnPieMonthly, btnPieDaily);
                loadMonthlyPieChart();
            }
        };
        btnDaily.setOnClickListener(barListener);
        btnLast6Months.setOnClickListener(barListener);
        btnPieDaily.setOnClickListener(pieListener);
        btnPieMonthly.setOnClickListener(pieListener);
        // Set initial highlight and load
        highlightBarButton(btnLast6Months, btnDaily);
        highlightPieButton(btnPieMonthly, btnPieDaily);
        fetchAllDataAndLoadCharts();
        return view;
    }

    // Helper to highlight selected bar chart button
    private void highlightBarButton(Button selected, Button other) {
        selected.setBackgroundColor(Color.parseColor("#ffcb47"));
        selected.setTextColor(Color.BLACK);
        other.setBackgroundColor(Color.LTGRAY);
        other.setTextColor(Color.DKGRAY);
    }
    // Helper to highlight selected pie chart button
    private void highlightPieButton(Button selected, Button other) {
        selected.setBackgroundColor(Color.parseColor("#ff6b6b"));
        selected.setTextColor(Color.WHITE);
        other.setBackgroundColor(Color.LTGRAY);
        other.setTextColor(Color.DKGRAY);
    }

    private void updateTotalSpent(float total) {
        latestPieTotal = total; // update latest total for slice breakdown
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        String formatted = formatter.format(total);
        totalSpentView.setText("Total Spent: " + formatted);
    }

    private void setPieClickListener() {
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(com.github.mikephil.charting.data.Entry e, Highlight h) {
                if (e instanceof PieEntry) {
                    PieEntry pe = (PieEntry) e;
                    float percent = (pe.getValue() / latestPieTotal) * 100;
                    String breakdown = pe.getLabel() + ": ₹" + pe.getValue() +
                            String.format(" (%.1f%%)", percent);
                    breakdownTextView.setText(breakdown);
                }
            }

            @Override
            public void onNothingSelected() {
                breakdownTextView.setText("");
            }
        });
    }

    private void loadMonthlyPieChart() {
        // Aggregate EXPENSE by category for last 6 months
        latestPieEntries.clear();
        Map<String, Float> categoryTotals = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -6);
        Date sixMonthsAgo = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        for (Data data : expenseList) {
            try {
                Date entryDate = sdf.parse(data.getDate());
                if (entryDate != null && entryDate.after(sixMonthsAgo)) {
                    String cat = data.getType();
                    float amt = data.getAmount();
                    categoryTotals.put(cat, categoryTotals.getOrDefault(cat, 0f) + amt);
                }
            } catch (ParseException e) { /* skip */ }
        }
        for (Map.Entry<String, Float> entry : categoryTotals.entrySet()) {
            latestPieEntries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }
        PieDataSet dataSet = new PieDataSet(latestPieEntries, "");
        dataSet.setColors(Color.parseColor("#ffcb47"), Color.parseColor("#ff6b6b"), Color.parseColor("#2196f3"), Color.parseColor("#f44336"), Color.parseColor("#4caf50"), Color.parseColor("#9c27b0"));
        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(14f);
        pieData.setValueTextColor(Color.BLACK);
        pieChart.setData(pieData);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(40f);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setCenterText("Spending");
        pieChart.setCenterTextSize(18f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.animateY(1000);
        pieChart.invalidate();
        float total = 0;
        for (PieEntry entry : latestPieEntries) {
            total += entry.getValue();
        }
        updateTotalSpent(total);
        setPieClickListener();
    }

    private void loadDailyBarChart() {
        // Aggregate by day for current week (Mon-Sun)
        List<BarEntry> incomeEntries = new ArrayList<>();
        List<BarEntry> expenseEntries = new ArrayList<>();
        String[] days = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        Map<String, Float> incomeDayTotals = new HashMap<>();
        Map<String, Float> expenseDayTotals = new HashMap<>();
        // Get the start of the week (Monday)
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault());
        // Build a map of date string for each day in the week
        Map<String, Integer> dateToIndex = new HashMap<>();
        List<String> weekDates = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            String dateStr = dateFormat.format(cal.getTime());
            weekDates.add(dateStr);
            dateToIndex.put(dateStr, i);
            cal.add(Calendar.DAY_OF_WEEK, 1);
        }
        // Aggregate income/expense for each day
        for (Data data : incomeList) {
            try {
                Date entryDate = dateFormat.parse(data.getDate());
                for (int i = 0; i < weekDates.size(); i++) {
                    Date weekDate = dateFormat.parse(weekDates.get(i));
                    if (entryDate != null && weekDate != null && entryDate.equals(weekDate)) {
                        String day = days[i];
                        float amt = data.getAmount();
                        incomeDayTotals.put(day, incomeDayTotals.getOrDefault(day, 0f) + amt);
                    }
                }
            } catch (ParseException e) { /* skip */ }
        }
        for (Data data : expenseList) {
            try {
                Date entryDate = dateFormat.parse(data.getDate());
                for (int i = 0; i < weekDates.size(); i++) {
                    Date weekDate = dateFormat.parse(weekDates.get(i));
                    if (entryDate != null && weekDate != null && entryDate.equals(weekDate)) {
                        String day = days[i];
                        float amt = data.getAmount();
                        expenseDayTotals.put(day, expenseDayTotals.getOrDefault(day, 0f) + amt);
                    }
                }
            } catch (ParseException e) { /* skip */ }
        }
        boolean hasData = false;
        for (int i = 0; i < days.length; i++) {
            String d = days[i];
            float income = incomeDayTotals.getOrDefault(d, 0f);
            float expense = expenseDayTotals.getOrDefault(d, 0f);
            incomeEntries.add(new BarEntry(i, income));
            expenseEntries.add(new BarEntry(i, expense));
            if (income > 0 || expense > 0) hasData = true;
        }
        BarDataSet incomeDataSet = new BarDataSet(incomeEntries, "Income");
        incomeDataSet.setColor(Color.parseColor("#2196f3"));
        BarDataSet expenseDataSet = new BarDataSet(expenseEntries, "Expense");
        expenseDataSet.setColor(Color.parseColor("#f44336"));
        BarData data = new BarData(incomeDataSet, expenseDataSet);
        data.setBarWidth(0.4f);
        barChart.setData(data);
        barChart.groupBars(0f, 0.2f, 0.02f);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setCenterAxisLabels(true);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(days.length);
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getAxisRight().setEnabled(false);
        barChart.setFitBars(true);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(true);
        barChart.invalidate();
        // Show/hide empty state
        TextView emptyText = getView().findViewById(R.id.barChartEmptyText);
        if (emptyText != null) emptyText.setVisibility(hasData ? View.GONE : View.VISIBLE);
    }

    private void loadDailyPieChart() {
        // Show category breakdown for today only (robust date matching)
        latestPieEntries.clear();
        Map<String, Float> categoryTotals = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        Date today = new Date();
        boolean hasData = false;
        int totalEntries = expenseList.size();
        int matchedEntries = 0;
        for (Data data : expenseList) {
            try {
                Date entryDate = dateFormat.parse(data.getDate());
                // Compare only the date part (ignore time)
                if (entryDate != null) {
                    Calendar cal1 = Calendar.getInstance();
                    cal1.setTime(today);
                    cal1.set(Calendar.HOUR_OF_DAY, 0);
                    cal1.set(Calendar.MINUTE, 0);
                    cal1.set(Calendar.SECOND, 0);
                    cal1.set(Calendar.MILLISECOND, 0);
                    Calendar cal2 = Calendar.getInstance();
                    cal2.setTime(entryDate);
                    cal2.set(Calendar.HOUR_OF_DAY, 0);
                    cal2.set(Calendar.MINUTE, 0);
                    cal2.set(Calendar.SECOND, 0);
                    cal2.set(Calendar.MILLISECOND, 0);
                    if (cal1.getTime().equals(cal2.getTime())) {
                        String cat = data.getType();
                        float amt = data.getAmount();
                        categoryTotals.put(cat, categoryTotals.getOrDefault(cat, 0f) + amt);
                        hasData = true;
                        matchedEntries++;
                    }
                }
            } catch (ParseException e) { /* skip */ }
        }
        for (Map.Entry<String, Float> entry : categoryTotals.entrySet()) {
            latestPieEntries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }
        PieDataSet dataSet = new PieDataSet(latestPieEntries, "");
        dataSet.setColors(Color.parseColor("#ffcb47"), Color.parseColor("#ff6b6b"), Color.parseColor("#2196f3"), Color.parseColor("#f44336"), Color.parseColor("#4caf50"), Color.parseColor("#9c27b0"));
        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(14f);
        pieData.setValueTextColor(Color.BLACK);
        pieChart.setData(pieData);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(40f);
        pieChart.setHoleColor(Color.TRANSPARENT);
        if (hasData) {
            pieChart.setCenterText("Today's Spending");
        } else {
            pieChart.setCenterText("No expenses today");
        }
        pieChart.setCenterTextSize(18f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.animateY(1000);
        pieChart.invalidate();
        float total = 0;
        for (PieEntry entry : latestPieEntries) {
            total += entry.getValue();
        }
        updateTotalSpent(total);
        setPieClickListener();
        // Show/hide empty state
        TextView emptyText = getView().findViewById(R.id.pieChartEmptyText);
        if (emptyText != null) {
            if (hasData) {
                emptyText.setVisibility(View.GONE);
            } else {
                emptyText.setText("No expenses today");
                emptyText.setVisibility(View.VISIBLE);
            }
        }
        // Debug log
        android.util.Log.d("AnalyticsFragment", "PieChart: total expense entries=" + totalEntries + ", matched today=" + matchedEntries + ", today=" + dateFormat.format(today));
    }

    private void loadMonthlyBarChart() {
        // Aggregate by month for last 6 months
        List<BarEntry> incomeEntries = new ArrayList<>();
        List<BarEntry> expenseEntries = new ArrayList<>();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
        Map<String, Float> incomeMonthTotals = new HashMap<>();
        Map<String, Float> expenseMonthTotals = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -6);
        Date sixMonthsAgo = cal.getTime();
        // Fill maps
        for (Data data : incomeList) {
            try {
                Date entryDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).parse(data.getDate());
                if (entryDate != null && entryDate.after(sixMonthsAgo)) {
                    String month = monthFormat.format(entryDate);
                    float amt = data.getAmount();
                    incomeMonthTotals.put(month, incomeMonthTotals.getOrDefault(month, 0f) + amt);
                }
            } catch (ParseException e) { /* skip */ }
        }
        for (Data data : expenseList) {
            try {
                Date entryDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).parse(data.getDate());
                if (entryDate != null && entryDate.after(sixMonthsAgo)) {
                    String month = monthFormat.format(entryDate);
                    float amt = data.getAmount();
                    expenseMonthTotals.put(month, expenseMonthTotals.getOrDefault(month, 0f) + amt);
                }
            } catch (ParseException e) { /* skip */ }
        }
        // Sort months
        List<String> months = new ArrayList<>(incomeMonthTotals.keySet());
        for (String m : expenseMonthTotals.keySet()) {
            if (!months.contains(m)) months.add(m);
        }
        months.sort((a, b) -> {
            try {
                return monthFormat.parse(a).compareTo(monthFormat.parse(b));
            } catch (Exception e) { return a.compareTo(b); }
        });
        for (int i = 0; i < months.size(); i++) {
            String m = months.get(i);
            incomeEntries.add(new BarEntry(i, incomeMonthTotals.getOrDefault(m, 0f)));
            expenseEntries.add(new BarEntry(i, expenseMonthTotals.getOrDefault(m, 0f)));
        }
        BarDataSet incomeDataSet = new BarDataSet(incomeEntries, "Income");
        incomeDataSet.setColor(Color.parseColor("#ffcb47"));
        BarDataSet expenseDataSet = new BarDataSet(expenseEntries, "Expense");
        expenseDataSet.setColor(Color.parseColor("#ff6b6b"));
        BarData data = new BarData(incomeDataSet, expenseDataSet);
        data.setBarWidth(0.4f);
        barChart.setData(data);
        barChart.groupBars(0f, 0.2f, 0.02f);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months.toArray(new String[0])));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setCenterAxisLabels(true);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(months.size());
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getAxisRight().setEnabled(false);
        barChart.setFitBars(true);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(true);
        barChart.invalidate();
    }
}
