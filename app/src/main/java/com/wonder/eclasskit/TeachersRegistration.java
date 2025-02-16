package com.wonder.eclasskit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wonder.eclasskit.Object.Teachers;

public class TeachersRegistration extends AppCompatActivity {

    private EditText email_txt, password_txt;
    private Button regBtn,toLogin;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers_registration);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Teachers");


        email_txt = findViewById(R.id.email);
        password_txt = findViewById(R.id.password);
        regBtn = findViewById(R.id.register);
        toLogin = findViewById(R.id.backToLogin);
        progressBar = findViewById(R.id.progressBar);


        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regBtn.setEnabled(false);
                registerNewUser();
            }
        });

        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeachersRegistration.this,TeacherLogin.class);
                startActivity(intent);
            }
        });

    }


    private void registerNewUser() {
        progressBar.setVisibility(View.VISIBLE);

        String email, password;
        email = email_txt.getText().toString();
        password = password_txt.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                           final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Teachers teachers = new Teachers("SIR",email);
                                        databaseReference.child(user.getUid()+"/Main").setValue(teachers);

                                        Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        mAuth.signOut();
                                        Intent intent = new Intent(TeachersRegistration.this, TeacherLogin.class);
                                        startActivity(intent);

                                    }
                                }
                            });

                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Registration failed !!!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            regBtn.setEnabled(true);
                        }
                    }
                });




    }

//    private void wirteFile(String textToSave) {
//        String space = ",";
//        try {
//            FileOutputStream fileOutputStream = openFileOutput("teachercourse.txt", Context.MODE_APPEND);
//            fileOutputStream.write((textToSave + space).getBytes());
//            fileOutputStream.close();
//
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


}
