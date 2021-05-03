package com.example.expensemanager;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

public class IncomeFragment extends Fragment {

    //Firebase Database

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;

    //Recycler View

    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<Data, IncomeFragment.MyViewHolder> mAdapter;

    //TextView

    private TextView incomesum;

    //Update

    private EditText editAmount;
    private EditText editType;
    private EditText editNote;

    //button for update and delete

    private Button bUpdate;
    private Button bDelete;

    //Data Item

    private String type;
    private String note;
    private int amount;

    private String post_key;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview=inflater.inflate(R.layout.fragment_income, container, false);

        mAuth= FirebaseAuth.getInstance();

        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();

        mIncomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);

        incomesum=myview.findViewById(R.id.income_txt_result);

        recyclerView=myview.findViewById(R.id.recycler_id_income);

        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int sum = 0;
                for (DataSnapshot mysnapshot:snapshot.getChildren()){

                    Data data=mysnapshot.getValue(Data.class);
                    sum+=data.getAmount();

                    String stsum=String.valueOf(sum);
                    incomesum.setText(stsum);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return myview;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Data> options=
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(mIncomeDatabase,Data.class)
                        .setLifecycleOwner(this)
                        .build();
        FirebaseRecyclerAdapter<Data, IncomeFragment.MyViewHolder> firebaseRecyclelerAdapter = new FirebaseRecyclerAdapter<Data, IncomeFragment.MyViewHolder>(options) {
            @NonNull
            @Override
            public IncomeFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.income_recycler_data,parent,false));
            }

            @Override
            protected void onBindViewHolder(@NonNull IncomeFragment.MyViewHolder holder, int position, @NonNull Data model) {
                holder.setAmount(model.getAmount());
                holder.setType(model.getType());
                holder.setNote(model.getNote());
                holder.setDate(model.getDate());

                holder.mView.setOnClickListener(new View.OnClickListener() {
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
        recyclerView.setAdapter(firebaseRecyclelerAdapter);
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }

        private void setType(String type) {
            TextView mType=mView.findViewById(R.id.type_txt_income);
            mType.setText(type);
        }

        private void setNote(String note) {
            TextView mNote=mView.findViewById(R.id.note_txt_income);
            mNote.setText(note);
        }

        private void setDate(String date) {
            TextView mDate=mView.findViewById(R.id.date_txt_income);
            mDate.setText(date);
        }

        private void setAmount(int amount) {
            TextView mAmount=mView.findViewById(R.id.amount_txt_income);
            String stamount=String.valueOf(amount);
            mAmount.setText(stamount);
        }
    }

    private void updateDataItem(){

        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View myview=inflater.inflate(R.layout.update_data_item,null);
        mydialog.setView(myview);

        editAmount=myview.findViewById(R.id.amount_edt);
        editType=myview.findViewById(R.id.type_edt);
        editNote=myview.findViewById(R.id.note_edt);

        editType.setText(type);
        editType.setSelection(type.length());

        editNote.setText(note);
        editNote.setSelection(type.length());

        editAmount.setText(String.valueOf(amount));
        editAmount.setSelection(String.valueOf(amount).length());

        bUpdate=myview.findViewById(R.id.btnUpdate);
        bDelete=myview.findViewById(R.id.btnDelete);

        AlertDialog dialog=mydialog.create();

        bUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type=editType.getText().toString().trim();
                note=editNote.getText().toString().trim();
                String udtamount=String.valueOf(amount);
                udtamount=editAmount.getText().toString().trim();
                int intamount=Integer.parseInt(udtamount);

                String mDate= DateFormat.getDateInstance().format(new Date());
                Data data=new Data(intamount,type,note,post_key,mDate);

                mIncomeDatabase.child(post_key).setValue(data);

                dialog.dismiss();
            }
        });

        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mIncomeDatabase.child(post_key).removeValue();
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}