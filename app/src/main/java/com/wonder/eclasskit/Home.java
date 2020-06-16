package com.wonder.eclasskit;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wonder.eclasskit.Object.Common;


public class Home extends Fragment {
    private ImageButton quiz;
    private ImageButton timeTable;
    private FirebaseUser user;
    private Button copy;
    private EditText redeem;
    private DatabaseReference databaseReference;

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        copy=(Button)view.findViewById(R.id.copy_redeem);
        redeem=(EditText)view.findViewById(R.id.redeem_code_txt);

        if (TextUtils.isEmpty(Common.uid)) {
            user = FirebaseAuth.getInstance().getCurrentUser();
            Common.uid = user.getUid();
        }
        if (Common.limit == 1){
            redeem.setVisibility(View.GONE);
            copy.setVisibility(View.GONE);
        }

        quiz=view.findViewById(R.id.quiz);
        timeTable=view.findViewById(R.id.classTimetb);

        databaseReference = FirebaseDatabase.getInstance().getReference("EnrollmentKey/");

        quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),QuizHome.class);
                startActivity(intent);
            }
        });
        timeTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), Class.class);
                startActivity(intent);
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String redeemcd=redeem.getText().toString();
                String teacher=Common.uid;
                databaseReference.child(redeemcd).setValue(teacher).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Redeem copied", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return view;
    }

}
