package com.example.finora;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BaseDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.Query;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Collections;

public class DashboardFragment extends Fragment {

    BarChart barChart;
    BarData barData;
    BarDataSet barDataSet;
    ArrayList<BarEntry> barEntries;
    ArrayList<String> dateLabels = new ArrayList<>();
    boolean isMonthlyView = false; // Track current chart mode

    private FloatingActionButton fab_main;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    private TextView fab_income_txt;
    private TextView fab_expense_txt;

    private boolean isOpen=false;
    private Animation FadOpen,FadClose,Rob,Rof;

    //Dashboard income and expense result

    private TextView totalIncomeResult;
    private TextView totalExpenseResult;
    private TextView totalBalanceResult;

    static int totalsumexpense=0;
    static int totalsumincome=0;
    static int balance;

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;
    private DatabaseReference mIncomeCategoryTotals;
    private DatabaseReference mExpenseCategoryTotals;

    //recycler view

    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview=inflater.inflate(R.layout.fragment_dashboard, container, false);

        mAuth=FirebaseAuth.getInstance();

        FirebaseUser mUser=mAuth.getCurrentUser();

        if(mAuth.getCurrentUser()!=null) {

            String uid = mUser.getUid();

            mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
            mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);
            mIncomeCategoryTotals = FirebaseDatabase.getInstance().getReference().child("IncomeCategoryTotals").child(uid);
            mExpenseCategoryTotals = FirebaseDatabase.getInstance().getReference().child("ExpenseCategoryTotals").child(uid);
            // Speed: enable local cache sync for these paths
            mIncomeDatabase.keepSynced(true);
            mExpenseDatabase.keepSynced(true);
            mIncomeCategoryTotals.keepSynced(true);
            mExpenseCategoryTotals.keepSynced(true);
        }


        fab_main=myview.findViewById(R.id.fb_main_lus_btn);
        fab_income_btn=myview.findViewById(R.id.income_ft_btn);
        fab_expense_btn=myview.findViewById(R.id.expense_ft_btn);
        fab_income_txt=myview.findViewById(R.id.income_ft_text);
        fab_expense_txt=myview.findViewById(R.id.expense_ft_text);

        //Tatal inc and exp

        totalIncomeResult=myview.findViewById(R.id.income_set_result);
        totalExpenseResult=myview.findViewById(R.id.expense_set_result);
        totalBalanceResult=myview.findViewById(R.id.balance_set_result);

        //Recycler

        mRecyclerIncome=myview.findViewById(R.id.recycler_income);
        mRecyclerExpense=myview.findViewById(R.id.recycler_expense);

        FadOpen= AnimationUtils.loadAnimation(getActivity(),R.anim.fab_open);
        FadClose=AnimationUtils.loadAnimation(getActivity(),R.anim.fab_close);
        Rob=AnimationUtils.loadAnimation(getActivity(),R.anim.rotate_backward);
        Rof=AnimationUtils.loadAnimation(getActivity(),R.anim.rotate_forward);


        barChart=myview.findViewById(R.id.bar_chart);

        // Setup chart filter chips
        Chip chipDaily = myview.findViewById(R.id.chip_daily);
        Chip chipMonthly = myview.findViewById(R.id.chip_monthly);

        chipDaily.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isMonthlyView = false;
                loadAndDisplayChart();
            }
        });
        chipMonthly.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isMonthlyView = true;
                loadAndDisplayChart();
            }
        });

        // Initial chart load
        loadAndDisplayChart();

        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addData();

                if(isOpen){
                    fab_main.startAnimation(Rof);
                    fab_income_btn.startAnimation(FadClose);

                    fab_expense_btn.startAnimation(FadClose);

                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_income_txt.startAnimation(FadClose);
                    fab_expense_txt.startAnimation(FadClose);
                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);
                    isOpen=false;

                }

                else{
                    fab_main.startAnimation(Rob);
                    fab_income_btn.startAnimation(FadOpen);

                    fab_expense_btn.startAnimation(FadOpen);

                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_txt.startAnimation(FadOpen);
                    fab_expense_txt.startAnimation(FadOpen);
                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);
                    isOpen=true;

                }

            }
        });

        // Limit totals to recent entries to reduce initial load
        final Query expenseTotalsQuery = mExpenseDatabase.orderByChild("timestamp").limitToLast(180);
        expenseTotalsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                totalsumexpense=0;
                for(DataSnapshot mysnap:snapshot.getChildren()){

                    Data data=mysnap.getValue(Data.class);
                    totalsumexpense+=data.getAmount();

                    String strResult=String.valueOf(totalsumexpense);

                    totalExpenseResult.setText(strResult+".00");

                }
                balance=totalsumincome-totalsumexpense;
                String strBalance=String.valueOf(balance);
                totalBalanceResult.setText(strBalance+".00");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //calc total income

        final Query incomeTotalsQuery = mIncomeDatabase.orderByChild("timestamp").limitToLast(180);
        incomeTotalsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                totalsumincome=0;
                for(DataSnapshot mysnap:snapshot.getChildren()){

                    Data data=mysnap.getValue(Data.class);
                    totalsumincome+=data.getAmount();

                    String stResult=String.valueOf(totalsumincome);

                    totalIncomeResult.setText(stResult+".00");

                }
                balance=totalsumincome-totalsumexpense;
                if(balance>0.05*totalsumincome && balance<=0.1*totalsumincome){
                    androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(getActivity());
                    builder.setTitle("Less than 10% balance remaining!");
                    builder.setMessage("Need to control your expenses.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //finishAffinity();
                            //startActivity(new Intent(getActivity(),DashboardFragment.class));
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
                else if(balance>0.0*totalsumincome && balance<=0.05*totalsumincome)
                {androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(getActivity());
                    builder.setTitle("Less than 5% balance remaining!");
                    builder.setMessage("Need to control your expenses.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //finishAffinity();
                            //startActivity(new Intent(getActivity(),DashboardFragment.class));
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
                String strBalance=String.valueOf(balance);
                totalBalanceResult.setText(strBalance+".00");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Recycler

        LinearLayoutManager layoutManagerIncome =new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);

        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);

        LinearLayoutManager layoutManagerExpense =new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);

        layoutManagerExpense.setStackFromEnd(true);
        layoutManagerExpense.setReverseLayout(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);

        return myview;
    }

    private void ftAnimation(){
        if(isOpen){
            fab_main.startAnimation(Rof);
            fab_income_btn.startAnimation(FadClose);

            fab_expense_btn.startAnimation(FadClose);

            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_txt.startAnimation(FadClose);
            fab_expense_txt.startAnimation(FadClose);
            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);
            isOpen=false;

        }

        else{
            fab_main.startAnimation(Rob);
            fab_income_btn.startAnimation(FadOpen);

            fab_expense_btn.startAnimation(FadOpen);

            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_txt.startAnimation(FadOpen);
            fab_expense_txt.startAnimation(FadOpen);
            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);
            isOpen=true;

        }


    }

    private void addData(){
        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incomeDataInsert();

            }
        });

        fab_expense_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                expenseDataInsert();
            }
        });


    }

    public void incomeDataInsert(){
        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());

        LayoutInflater inflater=LayoutInflater.from(getActivity());

        View myviewm=inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        mydialog.setView(myviewm);
        AlertDialog dialog=mydialog.create();

        EditText edtAmount=myviewm.findViewById(R.id.amount_edt);
        EditText edtType=myviewm.findViewById(R.id.type_edt);
        Spinner typeSpinner = myviewm.findViewById(R.id.type_spinner);
        // Input constraints
        final int MAX_INPUT_LENGTH = 12; // applies to amount/type/note text fields
        edtAmount.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(MAX_INPUT_LENGTH)});
        edtType.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(MAX_INPUT_LENGTH)});
        if (typeSpinner != null) {
            typeSpinner.setVisibility(View.VISIBLE);
            String[] incomeCategories = new String[]{"Salary", "Business", "Investment", "Freelance", "Interest", "Rent", "Dividends", "Gift", "Refunds", "Other"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, incomeCategories);
            typeSpinner.setAdapter(adapter);
        }
        EditText edtNote=myviewm.findViewById(R.id.note_edt);
        edtNote.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(40)});

        Button btnSave=myviewm.findViewById(R.id.btnSave);
        Button btnCancel=myviewm.findViewById(R.id.btnCancel);
        // Disable save until valid
        btnSave.setEnabled(false);

        android.text.TextWatcher watcher = new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(android.text.Editable s) {
                String amountTxt = edtAmount.getText().toString().trim();
                String typeTxt = edtType.getText().toString().trim();
                boolean hasSpinnerType = typeSpinner != null && typeSpinner.getSelectedItem() != null;
                boolean amountValid = false;
                try { amountValid = Integer.parseInt(amountTxt) > 0; } catch (Exception ignored) {}
                boolean typeValid = (!typeTxt.isEmpty()) || hasSpinnerType;
                btnSave.setEnabled(amountValid && typeValid);
            }
        };
        edtAmount.addTextChangedListener(watcher);
        edtType.addTextChangedListener(watcher);
        if (typeSpinner != null) {
            typeSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) { watcher.afterTextChanged(null); }
                @Override public void onNothingSelected(android.widget.AdapterView<?> parent) { watcher.afterTextChanged(null); }
            });
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type=edtType.getText().toString().trim().replaceAll("\\s+"," ");
                String amount=edtAmount.getText().toString().trim();
                String note=edtNote.getText().toString().trim().replaceAll("\\s+"," ");

                if(TextUtils.isEmpty(amount)){
                    edtAmount.setError("Amount is required");
                    return;
                }
                int ouramountint;
                try {
                    ouramountint = Integer.parseInt(amount);
                    if (ouramountint <= 0) {
                        edtAmount.setError("Enter a positive amount");
                        return;
                    }
                } catch (NumberFormatException e) {
                    edtAmount.setError("Enter a valid number");
                    return;
                }
                if(TextUtils.isEmpty(type)){
                    if (typeSpinner != null && typeSpinner.getSelectedItem() != null) {
                        type = typeSpinner.getSelectedItem().toString();
                    } else {
                        edtType.setError("Type is required");
                        return;
                    }
                }
                // Note is optional; remove below block if required
                // if(TextUtils.isEmpty(note)){
                //     edtNote.setError("Required Field..");
                //     return;
                // }

                if(mAuth.getCurrentUser()!=null) {
                    String id = mIncomeDatabase.push().getKey();
                    if (id == null) {
                        id = mIncomeDatabase.push().getKey();
                    }
                    if (id == null) {
                        Toast.makeText(getActivity(), "Failed to create income entry. Please try again.", Toast.LENGTH_SHORT).show();
                        Log.e("DashboardFragment", "Income push key is null");
                        return;
                    }
                    // Use consistent date format across app
                    String mDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());
                    Data data = new Data(ouramountint, type, note, id, mDate);
                    // Also store sortable timestamp for ordering
                    Map<String, Object> withTimestamp = new HashMap<>();
                    withTimestamp.put("amount", data.getAmount());
                    withTimestamp.put("type", data.getType());
                    withTimestamp.put("note", data.getNote());
                    withTimestamp.put("id", data.getId());
                    withTimestamp.put("date", data.getDate());
                    withTimestamp.put("timestamp", System.currentTimeMillis());

                    mIncomeDatabase.child(id).setValue(withTimestamp)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Income added", Toast.LENGTH_SHORT).show();
                                    // Increment category total atomically
                                    final String cat = data.getType();
                                    mIncomeCategoryTotals.child(cat).runTransaction(new Transaction.Handler() {
                                        @NonNull
                                        @Override
                                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                            Long current = currentData.getValue(Long.class);
                                            if (current == null) current = 0L;
                                            currentData.setValue(current + data.getAmount());
                                            return Transaction.success(currentData);
                                        }

                                        @Override
                                        public void onComplete(@androidx.annotation.Nullable DatabaseError error, boolean committed, @androidx.annotation.Nullable DataSnapshot currentData) {
                                            if (error != null) {
                                                Log.e("DashboardFragment", "Income category total update failed", error.toException());
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(getActivity(), "Failed to add income", Toast.LENGTH_SHORT).show();
                                    if (task.getException() != null) {
                                        Log.e("DashboardFragment", "Income add failed", task.getException());
                                    }
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.e("DashboardFragment", "Income add error", e);
                            });
                }

                ftAnimation();
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void expenseDataInsert(){
        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());

        LayoutInflater inflater=LayoutInflater.from(getActivity());

        View myviewm=inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        mydialog.setView(myviewm);
        final AlertDialog dialog=mydialog.create();
        dialog.setCancelable(false);

        EditText edtAmount=myviewm.findViewById(R.id.amount_edt);
        EditText edtType=myviewm.findViewById(R.id.type_edt);
        Spinner typeSpinner = myviewm.findViewById(R.id.type_spinner);
        final int MAX_INPUT_LENGTH = 12; // same constraints as income
        edtAmount.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(MAX_INPUT_LENGTH)});
        edtType.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(MAX_INPUT_LENGTH)});
        if (typeSpinner != null) {
            typeSpinner.setVisibility(View.VISIBLE);
            String[] expenseCategories = new String[]{
                    "Food", "Groceries", "Transport", "Fuel", "Shopping", "Bills", "Rent", "Utilities",
                    "Healthcare", "Education", "Entertainment", "Travel", "EMI", "Subscriptions", "Gifts", "Other"
            };
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, expenseCategories);
            typeSpinner.setAdapter(adapter);
        }
        EditText edtNote=myviewm.findViewById(R.id.note_edt);
        edtNote.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(40)});

        Button btnSave=myviewm.findViewById(R.id.btnSave);
        Button btnCancel=myviewm.findViewById(R.id.btnCancel);
        btnSave.setEnabled(false);

        android.text.TextWatcher watcher = new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(android.text.Editable s) {
                String amountTxt = edtAmount.getText().toString().trim();
                String typeTxt = edtType.getText().toString().trim();
                boolean hasSpinnerType = typeSpinner != null && typeSpinner.getSelectedItem() != null;
                boolean amountValid = false;
                try { amountValid = Integer.parseInt(amountTxt) > 0; } catch (Exception ignored) {}
                boolean typeValid = (!typeTxt.isEmpty()) || hasSpinnerType;
                btnSave.setEnabled(amountValid && typeValid);
            }
        };
        edtAmount.addTextChangedListener(watcher);
        edtType.addTextChangedListener(watcher);
        if (typeSpinner != null) {
            typeSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) { watcher.afterTextChanged(null); }
                @Override public void onNothingSelected(android.widget.AdapterView<?> parent) { watcher.afterTextChanged(null); }
            });
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type=edtType.getText().toString().trim().replaceAll("\\s+"," ");
                String amount=edtAmount.getText().toString().trim();
                String note=edtNote.getText().toString().trim().replaceAll("\\s+"," ");

                if(TextUtils.isEmpty(amount)){
                    edtAmount.setError("Amount is required");
                    return;
                }
                int ouramountinte;
                try {
                    ouramountinte = Integer.parseInt(amount);
                    if (ouramountinte <= 0) {
                        edtAmount.setError("Enter a positive amount");
                        return;
                    }
                } catch (NumberFormatException e) {
                    edtAmount.setError("Enter a valid number");
                    return;
                }
                if(TextUtils.isEmpty(type)){
                    if (typeSpinner != null && typeSpinner.getSelectedItem() != null) {
                        type = typeSpinner.getSelectedItem().toString();
                    } else {
                        edtType.setError("Type is required");
                        return;
                    }
                }
                // Note is optional; remove below block if required
                // if(TextUtils.isEmpty(note)){
                //     edtNote.setError("Required Field..");
                //     return;
                // }
                if(mAuth.getCurrentUser()!=null) {
                    String id = mExpenseDatabase.push().getKey();
                    String mDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());
                    Data data = new Data(ouramountinte, type, note, id, mDate);
                    Map<String, Object> withTimestamp = new HashMap<>();
                    withTimestamp.put("amount", data.getAmount());
                    withTimestamp.put("type", data.getType());
                    withTimestamp.put("note", data.getNote());
                    withTimestamp.put("id", data.getId());
                    withTimestamp.put("date", data.getDate());
                    withTimestamp.put("timestamp", System.currentTimeMillis());

                    mExpenseDatabase.child(id).setValue(withTimestamp)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Increment expense category totals atomically
                                    final String cat = data.getType();
                                    mExpenseCategoryTotals.child(cat).runTransaction(new Transaction.Handler() {
                                        @NonNull
                                        @Override
                                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                            Long current = currentData.getValue(Long.class);
                                            if (current == null) current = 0L;
                                            currentData.setValue(current + data.getAmount());
                                            return Transaction.success(currentData);
                                        }

                                        @Override
                                        public void onComplete(@androidx.annotation.Nullable DatabaseError error, boolean committed, @androidx.annotation.Nullable DataSnapshot currentData) {
                                            if (error != null) {
                                                Log.e("DashboardFragment", "Expense category total update failed", error.toException());
                                            }
                                        }
                                    });
                                } else {
                                    if (task.getException() != null) {
                                        Log.e("DashboardFragment", "Expense add failed", task.getException());
                                    }
                                }
                            })
                            .addOnFailureListener(e -> Log.e("DashboardFragment", "Expense add error", e));
                    Toast.makeText(getActivity(),"Data ADDED",Toast.LENGTH_SHORT).show();
                }
                ftAnimation();
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();

        Query incomeListQuery = mIncomeDatabase.orderByChild("timestamp").limitToLast(50);
        FirebaseRecyclerAdapter<Data,IncomeViewHolder>incomeAdapter=new FirebaseRecyclerAdapter<Data, IncomeViewHolder>
                (
                        Data.class,
                        R.layout.dashboard_income,
                        DashboardFragment.IncomeViewHolder.class,
                        incomeListQuery
                ) {
            @Override
            protected void populateViewHolder(IncomeViewHolder incomeViewHolder, Data model, int position) {
                incomeViewHolder.setIncomeType(model.getType());
                incomeViewHolder.setIncomeAmount(model.getAmount());
                incomeViewHolder.setIncomeDate(model.getDate());
            }
        };
        mRecyclerIncome.setAdapter(incomeAdapter);

        Query expenseListQuery = mExpenseDatabase.orderByChild("timestamp").limitToLast(50);
        FirebaseRecyclerAdapter<Data,ExpenseViewHolder>expenseAdapter=new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>
                (Data.class,R.layout.dashboard_expense,DashboardFragment.ExpenseViewHolder.class,expenseListQuery) {
            @Override
            protected void populateViewHolder(ExpenseViewHolder expenseViewHolder, Data data, int i) {

                expenseViewHolder.setExpenseType(data.getType());
                expenseViewHolder.setExpenseAmount(data.getAmount());
                expenseViewHolder.setExpenseDate(data.getDate());
            }
        };
        mRecyclerExpense.setAdapter(expenseAdapter);

    }

    //For Income Data
    public static class IncomeViewHolder extends RecyclerView.ViewHolder{

        View mIncomeView;

                public IncomeViewHolder(View itemView)
                {
                    super(itemView);
                    mIncomeView=itemView;
                }
                public void setIncomeType(String type)
                {
                    TextView mtype=mIncomeView.findViewById(R.id.type_Income_ds);
                    mtype.setText(type);
                }
                public void setIncomeAmount(int amount)
                {
                    TextView mAmount=mIncomeView.findViewById(R.id.amount_Income_ds);
                    String strAmount= String.valueOf(amount);
                    mAmount.setText(strAmount);
                }
                public void setIncomeDate(String date)
                {
                    TextView mDate=mIncomeView.findViewById(R.id.date_Income_ds);
                    mDate.setText(date);
                }
    }

    //For expense data
    public static class ExpenseViewHolder extends RecyclerView.ViewHolder{

        View mExpenseView;

        public ExpenseViewHolder(View itemView)
        {
            super(itemView);
            mExpenseView=itemView;
        }
        public void setExpenseType(String type)
        {
            TextView mtype=mExpenseView.findViewById(R.id.type_Expense_ds);
            mtype.setText(type);
        }
        public void setExpenseAmount(int amount)
        {
            TextView mAmount=mExpenseView.findViewById(R.id.amount_Expense_ds);
            String strAmount= String.valueOf(amount);
            mAmount.setText(strAmount);
        }
        public void setExpenseDate(String date)
        {
            TextView mDate=mExpenseView.findViewById(R.id.date_Expense_ds);
            mDate.setText(date);
        }
    }

    private void getBarEntries(DataSnapshot snap) {
        barEntries = new ArrayList<>();
        dateLabels = new ArrayList<>();
        Map<String, Float> grouped = new HashMap<>();
        SimpleDateFormat inputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
        if (snap.exists()) {
            for (DataSnapshot ds : snap.getChildren()) {
                Data data = ds.getValue(Data.class);
                if (data == null) continue;
                String rawDate = data.getDate();
                String key = "";
                try {
                    Date dateObj = inputFormat.parse(rawDate);
                    key = isMonthlyView ? monthFormat.format(dateObj) : dayFormat.format(dateObj);
                } catch (Exception e) {
                    key = rawDate;
                }
                float amount = data.getAmount();
                grouped.put(key, grouped.getOrDefault(key, 0f) + amount);
            }
            // Sort dates chronologically
            List<String> sortedKeys = new ArrayList<>(grouped.keySet());
            Collections.sort(sortedKeys, (d1, d2) -> {
                try {
                    SimpleDateFormat fmt = isMonthlyView ? monthFormat : dayFormat;
                    return fmt.parse(d1).compareTo(fmt.parse(d2));
                } catch (Exception e) { return d1.compareTo(d2); }
            });
            int idx = 0;
            for (String label : sortedKeys) {
                barEntries.add(new BarEntry(idx, grouped.get(label)));
                dateLabels.add(label);
                idx++;
            }
        }
    }

    private void loadAndDisplayChart() {
        mExpenseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getBarEntries(snapshot);
                barDataSet = new BarDataSet(barEntries, isMonthlyView ? "Monthly Expenses" : "Daily Expenses");
                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                barDataSet.setValueTextColor(Color.BLACK);
                barDataSet.setValueTextSize(14f);
                barDataSet.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return "₹" + ((int)value);
                    }
                });
                barData = new BarData(barDataSet);
                barChart.setData(barData);
                barChart.getDescription().setText(isMonthlyView ? "Expenses Per Month" : "Expenses Per Day");
                barChart.getDescription().setTextColor(Color.DKGRAY);
                barChart.getDescription().setTextSize(13f);
                XAxis xAxis = barChart.getXAxis();
                xAxis.setDrawLabels(true);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f);
                xAxis.setTextColor(Color.DKGRAY);
                xAxis.setTextSize(12f);
                xAxis.setValueFormatter(new IndexAxisValueFormatter(dateLabels));
                xAxis.setLabelRotationAngle(-30f);
                xAxis.setDrawGridLines(false);
                barChart.getAxisLeft().setTextColor(Color.DKGRAY);
                barChart.getAxisLeft().setTextSize(12f);
                barChart.getAxisLeft().setDrawGridLines(true);
                barChart.getAxisRight().setEnabled(false);
                barChart.setExtraBottomOffset(16f);
                barChart.getLegend().setEnabled(false);
                barChart.animateY(900);
                barChart.invalidate();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}