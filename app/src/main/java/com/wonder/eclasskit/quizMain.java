package com.wonder.eclasskit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wonder.eclasskit.Adpter.ListAdapterQuiz;
import com.wonder.eclasskit.Adpter.ListAdapterQuizList;
import com.wonder.eclasskit.Firebase.FirebaseDatabaseHelper3;
import com.wonder.eclasskit.Object.Answer;
import com.wonder.eclasskit.Object.Common;
import com.wonder.eclasskit.Object.Quizobj;



import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class quizMain extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private RecyclerView QuizListView;
    private ArrayList<Quizobj> uploadQuizS;
    private ListAdapterQuizList adapter;
    private ListAdapterQuiz adapterQuiz;
    private View v;
    private TabLayout mTabLayout;
    private ViewPager mPager;
    private Context context;
    private List<FragmentQuestion> fragmentlist=new ArrayList<>();
    private Button next,delete;
    private ImageButton flag;
    private int position,v1;
    private String[] array;
    private String name,keyname,time;
    private ArrayList<String> keys;
    private CountDownTimer countdown;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_main);
        Intent intent=getIntent();
        keyname=intent.getStringExtra("key");
        time=intent.getStringExtra("time");
        databaseReference = FirebaseDatabase.getInstance().getReference("quizHome/"+Common.uid+"/"+keyname+"/quiz");

        if (Common.limit == 1) {

        final TextView timeleft=(TextView)findViewById(R.id.time_left);

        int hoursToGo = 0;
        int minutesToGo = Integer.valueOf(time);
        int secondsToGo = 0;


        int millisToGo = secondsToGo*1000+minutesToGo*1000*60+hoursToGo*1000*60*60;


        countdown = new CountDownTimer(millisToGo, 1000) {

                @Override
                public void onTick(long millis) {
                    int seconds = (int) (millis / 1000) % 60;
                    int minutes = (int) ((millis / (1000 * 60)) % 60);
//                int hours   = (int) ((millis / (1000*60*60)) % 24);
                    String text = String.format("%02d : %02d", minutes, seconds);
                    if (minutes == 0) {
                        timeleft.setText(text);
                        timeleft.setTextColor(Color.parseColor("#FF0000"));
                    } else {
                        timeleft.setText(text);
                    }

                }

                @Override
                public void onFinish() {
                    submit();
                }
            }.start();
        }


        mTabLayout=findViewById(R.id.quiztablayout);
        mPager=findViewById(R.id.quizviewpager);
        flag=(ImageButton)findViewById(R.id.flag_imgbtn);
        next=findViewById(R.id.addnew_quest);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position=mPager.getCurrentItem()+1;

                if (position==adapterQuiz.getCount() && Common.limit == 1) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(
                            quizMain.this);
                    adb.setMessage("Do you want to complete?");
                    adb.setPositiveButton("Submit All", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            Toast.makeText(quizMain.this, "a:"+Common.answer+""+getName(), Toast.LENGTH_SHORT).show();
                            submit();
                        }

                    });
                    adb.setNegativeButton("Cancel", null);
                    adb.show();
                }else if(position==adapterQuiz.getCount() ){
                    setQuizPassword();
                }

                mPager.setCurrentItem(position);

            }
        });

        delete=(Button)findViewById(R.id.delete_quest);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int post=mPager.getCurrentItem();
                AlertDialog.Builder adb = new AlertDialog.Builder(quizMain.this);
                adb.setMessage("Do you want to Delete?");
                adb.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Quizobj quizobj=uploadQuizS.get(post);
                        Toast.makeText(quizMain.this, "p: "+post, Toast.LENGTH_SHORT).show();
                        if (!TextUtils.isEmpty(quizobj.getUriimg())) {
                            StorageReference quizRef = FirebaseStorage.getInstance().getReferenceFromUrl(quizobj.getUriimg());
                            quizRef.delete();
                        }

                        uploadQuizS.remove(post);
                        adapter.notifyDataSetChanged();
                        adapterQuiz.deletePage(post);




                        databaseReference.child(keys.get(post)).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(quizMain.this, "Long press deleted", Toast.LENGTH_SHORT).show();

                            }
                        });

                    }

                });
                adb.setNegativeButton("Cancel", null);
                adb.show();
            }
        });


        flag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int po = mPager.getCurrentItem();
                if (v1==0) {
                    Common.completelist.set(po, 1);
                    v1++;
                }else {
                    Common.completelist.set(po,0);
                    v1--;
                }
            }
        });
        context=this;

        viewAllFiles();
    }

    private void setQuizPassword() {
        AlertDialog.Builder adb = new AlertDialog.Builder(
                quizMain.this);
        adb.setMessage("Set quiz password");
        View v1= getLayoutInflater().inflate(R.layout.textfield, null);
        adb.setView(v1);
        EditText e=(EditText)v1.findViewById(R.id.all_vriable);
        adb.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("quizHome/"+Common.uid+"/"+keyname);
                databaseReference2.child("password").setValue(e.getText().toString().trim());
            }
        });
        adb.setNegativeButton("Cancel",null);
        adb.show();
    }


    private void viewAllFiles() {
        uploadQuizS = new ArrayList<>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                keys = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Quizobj uploadQuizobj = postSnapshot.getValue(Quizobj.class);
                    uploadQuizS.add(uploadQuizobj);
                    String mkey = postSnapshot.getKey();
                    keys.add(mkey);
                }


                adapter = new ListAdapterQuizList(uploadQuizS);

                LinearLayoutManager layoutManager= new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);

                QuizListView = (RecyclerView) findViewById(R.id.recyclerquizlist);
                QuizListView.setLayoutManager(layoutManager);
                QuizListView.setLayoutManager(new GridLayoutManager(quizMain.this,10));
                QuizListView.setAdapter(adapter);

                getFragmentList();

                adapterQuiz= new ListAdapterQuiz(getSupportFragmentManager(),1,fragmentlist);
                mPager.setAdapter(adapterQuiz);
                mTabLayout.setupWithViewPager(mPager);
                mPager.setSaveFromParentEnabled(false);

            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void getFragmentList() {
        for (int i=0;i<uploadQuizS.size();i++){
            FragmentQuestion fragmentQuestion=new FragmentQuestion(uploadQuizS.get(i),i);
            fragmentlist.add(fragmentQuestion);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Common.limit != 1) {
            getMenuInflater().inflate(R.menu.menu_quiz, menu);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_quest) {
            Intent intent=new Intent(this,addQuestions.class);
            intent.putExtra("key",keyname);
            startActivity(intent);
            finish();
            return true;
        }else if (id == R.id.review_quest) {
            Intent intent=new Intent(this,ReviewQuiz.class);
            intent.putExtra("key",keyname);
            startActivity(intent);
            return true;
        }else if(id == R.id.set_quizpassword2){
            setQuizPassword();
        }

        return super.onOptionsItemSelected(item);
    }
    public String getName(){
        try {
            FileInputStream fileInputStream = openFileInput("apprequirement.txt");
            InputStreamReader inputStreamReader=new InputStreamReader(fileInputStream);

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer =new StringBuffer();


            String lines;
            while ((lines = bufferedReader.readLine()) != null){
                stringBuffer.append(lines + "\n");
            }
            String str =stringBuffer.toString();
            array = str.split(",");

            name=array[1];

        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return name;
    }

    private void submit(){
        Answer answer=new Answer(Common.answer,getResult(),getName());
        new FirebaseDatabaseHelper3(keyname).addAnswerDetails(answer, new FirebaseDatabaseHelper3.DataStatus() {
            @Override
            public void DataIsLoaded(List<Answer> timetables, List<String> keys) {

            }

            @Override
            public void DataIsInserted() {
                Toast.makeText(quizMain.this, "Quiz Answer Data Inserted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }
        });
        Intent intent = new Intent(quizMain.this, MainActivity.class);
        Common.answer.clear();
        Common.completelist.clear();
        startActivity(intent);
    }

    private String getResult(){
        String result_tmp=null;
        int temp=0;
        for (int val:Common.answer){
            if (val>0){
                temp++;
            }
        }
        result_tmp=String.valueOf(temp);
        return result_tmp;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder adb = new AlertDialog.Builder(
                quizMain.this);
        adb.setMessage("Do you want to Quit?");
        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Common.limit == 1) {
                    countdown.cancel();
                }
                Common.answer.clear();
                Common.completelist.clear();
                finish();
            }
        });
        adb.setNegativeButton("No", null);
        adb.show();
    }
}
