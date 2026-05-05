package com.example.finora;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;


public class ExpenseFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;
    private RecyclerView recyclerView;

    private TextView expenseTotalSum;

    private EditText edtAmount;
    private EditText edtType;
    private EditText edtNote;

    private Button btnUpdate;
    private Button btnDelete;

    private String type;
    private String note;
    private int amount;

    private  String post_key;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myview=inflater.inflate(R.layout.fragment_expense, container, false);
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        if(mAuth!=null) {
            String uid = mUser.getUid();

            mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);
        }

        expenseTotalSum=myview.findViewById(R.id.expense_txt_result);
        recyclerView=myview.findViewById(R.id.recycler_id_expense);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);


        mExpenseDatabase.addValueEventListener(new ValueEventListener() {

            int totalvalue=0;

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot mysnapshot:dataSnapshot.getChildren()){
                    Data data=mysnapshot.getValue(Data.class);
                    totalvalue=totalvalue+data.getAmount();
                    String stTotalvalue=String.valueOf(totalvalue);
                    expenseTotalSum.setText(stTotalvalue+".00");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // Add Expense FAB
        View fab = myview.findViewById(R.id.fab_add_expense);
        if (fab != null) {
            fab.setOnClickListener(v -> showAddExpenseDialog());
        }
        return  myview;
    }

    private void showAddExpenseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View dialogView = inflater.inflate(R.layout.update_data_item, null);
        builder.setView(dialogView);

        EditText edtAmount = dialogView.findViewById(R.id.amount_edt);
        EditText edtType = dialogView.findViewById(R.id.type_edt);
        Spinner typeSpinner = dialogView.findViewById(R.id.type_spinner);
        typeSpinner.setVisibility(View.VISIBLE);
        String[] categories = new String[]{"Food", "Transport", "Shopping", "Bills", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories);
        typeSpinner.setAdapter(adapter);
        EditText edtNote = dialogView.findViewById(R.id.note_edt);
        Button btnUpdate = dialogView.findViewById(R.id.btn_upd_Update);
        Button btnDelete = dialogView.findViewById(R.id.btnuPD_Delete);
        btnDelete.setVisibility(View.GONE); // Hide delete button for add
        btnUpdate.setText("Add");

        // Constraints & UX
        final int MAX_INPUT_LENGTH = 12;
        edtAmount.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(MAX_INPUT_LENGTH)});
        edtType.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(MAX_INPUT_LENGTH)});
        edtNote.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(40)});
        btnUpdate.setEnabled(false);

        android.text.TextWatcher watcher = new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(android.text.Editable s) {
                String amountStr = edtAmount.getText().toString().trim();
                String typeStr = edtType.getText().toString().trim();
                boolean hasSpinner = typeSpinner.getSelectedItem() != null;
                boolean amountOk = false;
                try { amountOk = Integer.parseInt(amountStr) > 0; } catch (Exception ignored) {}
                boolean typeOk = !typeStr.isEmpty() || hasSpinner;
                btnUpdate.setEnabled(amountOk && typeOk);
            }
        };
        edtAmount.addTextChangedListener(watcher);
        edtType.addTextChangedListener(watcher);
        typeSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) { watcher.afterTextChanged(null); }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) { watcher.afterTextChanged(null); }
        });

        AlertDialog dialog = builder.create();
        btnUpdate.setOnClickListener(v -> {
            String amountStr = edtAmount.getText().toString().trim();
            String typeStr = edtType.getText().toString().trim().replaceAll("\\s+"," ");
            if (typeStr.isEmpty()) {
                typeStr = typeSpinner.getSelectedItem() != null ? typeSpinner.getSelectedItem().toString() : "Other";
            }
            String noteStr = edtNote.getText().toString().trim().replaceAll("\\s+"," ");
            int amount;
            try {
                amount = Integer.parseInt(amountStr);
                if (amount <= 0) { edtAmount.setError("Enter a positive amount"); return; }
            } catch (NumberFormatException e) {
                edtAmount.setError("Enter a valid number");
                return;
            }
            String id = mExpenseDatabase.push().getKey();
            if (id == null) {
                android.widget.Toast.makeText(getActivity(), "Unable to create entry. Try again.", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            String date = java.text.DateFormat.getDateInstance().format(new java.util.Date());
            Data data = new Data(amount, typeStr, noteStr, id, date);
            mExpenseDatabase.child(id).setValue(data)
                    .addOnCompleteListener(t -> {
                        if (t.isSuccessful()) {
                            android.widget.Toast.makeText(getActivity(), "Expense added", android.widget.Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            android.widget.Toast.makeText(getActivity(), "Failed to add expense", android.widget.Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> android.widget.Toast.makeText(getActivity(), e.getMessage(), android.widget.Toast.LENGTH_LONG).show());
        });
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Data, ExpenseFragment.MyViewHolder> adapter= new FirebaseRecyclerAdapter<Data, ExpenseFragment.MyViewHolder>(

                Data.class,
                R.layout.expense_recycler_data,
                ExpenseFragment.MyViewHolder.class,
                mExpenseDatabase
        ) {
            @Override
            protected void populateViewHolder(ExpenseFragment.MyViewHolder myViewHolder, Data model, int position) {

                myViewHolder.setType(model.getType());
                myViewHolder.setNote(model.getNote());
                myViewHolder.setDate(model.getDate());
                myViewHolder.setAmount(model.getAmount());

                myViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        post_key=getRef(position).getKey();

                        type=model.getType();
                        note=model.getNote();
                        amount=model.getAmount();

                        updateDataItem();
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View mView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }

        private void setType(String type){
            TextView mType=mView.findViewById(R.id.type_txt_expense);
            mType.setText(type);
        }
        private void setNote(String note){
            TextView mNote=mView.findViewById(R.id.note_txt_expense);
            mNote.setText(note);
        }
        private void setDate(String date){
            TextView mDate=mView.findViewById(R.id.date_txt_expense);
            mDate.setText(date);
        }
        private void setAmount(int amount){
            TextView mAmount=mView.findViewById(R.id.amount_txt_expense);
            String stamount=String.valueOf(amount);
            mAmount.setText("-"+stamount);
        }
    }

    private void updateDataItem() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.update_data_item, null);
        mydialog.setView(myview);

        edtAmount = myview.findViewById(R.id.amount_edt);
        edtType = myview.findViewById(R.id.type_edt);
        edtNote = myview.findViewById(R.id.note_edt);

        //Set data to edit text..
        edtType.setText(type);
        edtType.setSelection(type.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());

        btnUpdate = myview.findViewById(R.id.btn_upd_Update);
        btnDelete = myview.findViewById(R.id.btnuPD_Delete);

        AlertDialog dialog = mydialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = edtType.getText().toString().trim().replaceAll("\\s+"," ");
                note = edtNote.getText().toString().trim().replaceAll("\\s+"," ");

                String mdAmount = edtAmount.getText().toString().trim();
                if (mdAmount.isEmpty()) {
                    edtAmount.setError("Amount is required");
                    return;
                }
                if (type.isEmpty()) {
                    edtType.setError("Type is required");
                    return;
                }
                int myAmount;
                try {
                    myAmount = Integer.parseInt(mdAmount);
                    if (myAmount <= 0) { edtAmount.setError("Enter a positive amount"); return; }
                } catch (NumberFormatException e) {
                    edtAmount.setError("Enter a valid number");
                    return;
                }

                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(myAmount, type, note, post_key, mDate);

                if (post_key == null) {
                    android.widget.Toast.makeText(getActivity(), "Invalid item. Please refresh.", android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }
                mExpenseDatabase.child(post_key).setValue(data)
                        .addOnCompleteListener(t -> {
                            if (t.isSuccessful()) {
                                android.widget.Toast.makeText(getActivity(), "Expense updated", android.widget.Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            } else {
                                android.widget.Toast.makeText(getActivity(), "Update failed", android.widget.Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> android.widget.Toast.makeText(getActivity(), e.getMessage(), android.widget.Toast.LENGTH_LONG).show());
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (post_key == null) {
                    android.widget.Toast.makeText(getActivity(), "Invalid item. Please refresh.", android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }
                new AlertDialog.Builder(getActivity())
                        .setTitle("Delete expense")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton("Delete", (d, which) -> {
                            mExpenseDatabase.child(post_key).removeValue()
                                    .addOnCompleteListener(t -> {
                                        if (t.isSuccessful()) {
                                            android.widget.Toast.makeText(getActivity(), "Expense deleted", android.widget.Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        } else {
                                            android.widget.Toast.makeText(getActivity(), "Delete failed", android.widget.Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> android.widget.Toast.makeText(getActivity(), e.getMessage(), android.widget.Toast.LENGTH_LONG).show());
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
        dialog.show();
    }
}
