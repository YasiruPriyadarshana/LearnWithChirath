package com.wonder.eclasskit;


import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wonder.eclasskit.Adpter.ListAdapterReview;
import com.wonder.eclasskit.Adpter.RecyclerAdapterReview;
import com.wonder.eclasskit.Object.Answer;
import com.wonder.eclasskit.Object.Review;

import java.util.ArrayList;

public class ReviewQuiz extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private ArrayList<Answer> answers;
    private ListView ReviewListView;
    private ListAdapterReview adapter;
    private RecyclerView ReviewAnsListView;
    private RecyclerAdapterReview adapterReview;
    private ArrayList<Review> reviews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_quiz);
        Intent intent=getIntent();
        String keyname=intent.getStringExtra("key");
        databaseReference = FirebaseDatabase.getInstance().getReference("quizHome/Answer_Student/"+keyname);
        ReviewListView=(ListView)findViewById(R.id.recyclerviewreview);
        answers=new ArrayList<>();
        reviews=new ArrayList<>();
        viewAllFiles();

    }

    private void viewAllFiles2() {
        ReviewAnsListView=(RecyclerView)findViewById(R.id.recyclerreviewlist);
        LinearLayoutManager layoutManager= new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
        ReviewAnsListView.setLayoutManager(layoutManager);
        ReviewAnsListView.setLayoutManager(new GridLayoutManager(ReviewQuiz.this,3));
        adapterReview=new RecyclerAdapterReview(reviews);
        ReviewAnsListView.setAdapter(adapterReview);
    }

    private void viewAllFiles() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i=1;
                ArrayList<Integer> sumAnswer=new ArrayList<>();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Answer answer = postSnapshot.getValue(Answer.class);
                    answers.add(answer);
                }

                int temp=0;
                for (int j=0;j<answers.size();j++){
                    if (j!=0) {
                        for (int k=0; k<answers.get(j).getAnswer().size();k++) {
                            temp=sumAnswer.get(k);
                            if (answers.get(j).getAnswer().get(k) > 0) {
                                sumAnswer.set(k,++temp);
                            }
                            temp=0;
                        }
                    }
                    if (j==0){
                        for (int k=0; k<answers.get(j).getAnswer().size();k++) {
                            temp=answers.get(0).getAnswer().get(k);
                            sumAnswer.add(temp);
                        }
                    }
                }
                Toast.makeText(ReviewQuiz.this, "a: "+sumAnswer, Toast.LENGTH_SHORT).show();

                for (int l=0;l<sumAnswer.size();l++){
                    Review review=new Review(i,((sumAnswer.get(l)*1.0)/(answers.size()*1.0))*100.0);
                    i++;
                    reviews.add(review);
                }





                adapter = new ListAdapterReview(ReviewQuiz.this, R.layout.itemreview,answers);
                ReviewListView.setAdapter(adapter);
                viewAllFiles2();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
