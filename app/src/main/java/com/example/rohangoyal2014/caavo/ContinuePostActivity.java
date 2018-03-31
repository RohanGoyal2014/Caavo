package com.example.rohangoyal2014.caavo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ContinuePostActivity extends AppCompatActivity {

    private StoryModel storyModel;
    private EditText continuationView;
    private TextView titleView;
    private TextView contentView;
    private TextView genreView;
    private TextView startNodeDetailsView;
    private TextView wordLimit;
    private ScrollView scrollView;
    private TextView submitView;
    private ProgressBar progressBar;
    String message="\"Total limit for story is 500 words and your maximum limit is 50 provided that it does not let the total limit exceed\"";
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continue_post);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent i=getIntent();
        if(i!=null)
        {
            if(i.hasExtra(Utilities.POST_DATA_TRANSFER_KEY)){
                ArrayList<String> extras=i.getStringArrayListExtra(Utilities.POST_DATA_TRANSFER_KEY);
                storyModel=new StoryModel(
                        extras.get(0),
                        extras.get(1),
                        extras.get(2),
                        Integer.parseInt(extras.get(3)),
                        extras.get(4),
                        Integer.parseInt(extras.get(5)),
                        Long.parseLong(extras.get(6))
                );
            }
        }
        checkIfAlreadyContributed(storyModel.getStory_title(),String.valueOf(storyModel.getTime()),String.valueOf(storyModel.getWord_count()));
        titleView=findViewById(R.id.title);
        contentView=findViewById(R.id.content);
        progressBar=findViewById(R.id.progress);
        scrollView=findViewById(R.id.scroll_view);
        continuationView=findViewById(R.id.continuation);
        genreView=findViewById(R.id.genre);
        startNodeDetailsView=findViewById(R.id.start_node_details);
        wordLimit=findViewById(R.id.past_word_count);
        submitView=findViewById(R.id.submit);

        titleView.setText(storyModel.getStory_title());
        contentView.setText(storyModel.getStory_content());
        genreView.setText(storyModel.getGenre());
        startNodeDetailsView.setText("( Started by ".concat(storyModel.getStart_node()).concat(")"));
        wordLimit.setText("(".concat(String.valueOf(storyModel.getWord_count())).concat(" words)"));
        //showAlert();

        submitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(continuationView.getText().toString().trim().isEmpty()){
                    Toast.makeText(ContinuePostActivity.this, "The field can not be left empty", Toast.LENGTH_SHORT).show();
                } else{
                    int word_count=Utilities.calculateWords(continuationView.getText().toString().trim());
                    if(word_count>50 ){
                        Toast.makeText(ContinuePostActivity.this,"Your word limit is "+String.valueOf(word_count) +" which exceeds 50.",Toast.LENGTH_SHORT).show();
                    } else if(storyModel.getWord_count()+word_count>500){
                        Toast.makeText(ContinuePostActivity.this, "The total word limit is "+String.valueOf(word_count+storyModel.getWord_count())+" which exceeds 500", Toast.LENGTH_SHORT).show();
                    } else{
                        storyModel.setWord_count(storyModel.getWord_count()+word_count);
                        storyModel.setContributor_count(storyModel.getContributor_count()+1);
                        storyModel.setStory_content(storyModel.getStory_content().concat(continuationView.getText().toString().trim()));
                        long prevTime=storyModel.getTime();
                        storyModel.setTime(System.currentTimeMillis());
                        startSavingPost(String.valueOf(prevTime));
                    }
                }
            }
        });


    }

    private void checkIfAlreadyContributed(final String title, final String time, final String wordCount){
        FirebaseDatabase.getInstance().getReference().child(Utilities.FirebaseUtilities.STORIES)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds:dataSnapshot.getChildren()) {
                            String storyTItle=ds.child(Utilities.FirebaseUtilities.STORY_TITLE_KEY).getValue().toString();
                            String timeStamp=ds.child(Utilities.FirebaseUtilities.TIME_KEY).getValue().toString();
                            String words=ds.child(Utilities.FirebaseUtilities.WORD_COUNT_KEY).getValue().toString();
                            if(title.equals(storyTItle) && time.equals(timeStamp) && wordCount.equals(words) ){
                                final String key=ds.getKey();
                                FirebaseDatabase.getInstance().getReference().child(Utilities.FirebaseUtilities.USER_CONTRIBUTION_KEY)
                                        .child(Utilities.FirebaseUtilities.mAuth.getUid())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot ds1:dataSnapshot.getChildren()) {
                                                    String postKey=ds1.getValue().toString();
                                                    if(postKey.equals(key)){
                                                        Toast.makeText(ContinuePostActivity.this, "You have already contributed to this!!", Toast.LENGTH_SHORT).show();
                                                        continuationView.setVisibility(View.GONE);
                                                        finish();
                                                        break;
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                Toast.makeText(ContinuePostActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(ContinuePostActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        genreView.setVisibility(View.GONE);
        scrollView.setVisibility(View.GONE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
        genreView.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.VISIBLE);
    }

    private void startSavingPost(final String prevTime) {
        showProgress();
        FirebaseDatabase.getInstance().getReference().child(Utilities.FirebaseUtilities.STORIES)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds:dataSnapshot.getChildren()) {
                            String uid=ds.child(Utilities.FirebaseUtilities.START_NODE_KEY).getValue().toString();
                            String time=ds.child(Utilities.FirebaseUtilities.TIME_KEY).getValue().toString();
                            if(uid.equals(Utilities.FirebaseUtilities.mAuth.getUid()) && time.equals(prevTime)){
                                final String key=ds.getKey();
                                FirebaseDatabase.getInstance().getReference().child(Utilities.FirebaseUtilities.STORIES)
                                        .child(key).setValue(storyModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            saveUserConfiguration(key);
                                        } else{
                                            Toast.makeText(ContinuePostActivity.this,getString(R.string.error),Toast.LENGTH_SHORT).show();
                                            hideProgress();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ContinuePostActivity.this,getString(R.string.error),Toast.LENGTH_SHORT).show();
                                        hideProgress();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(ContinuePostActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                        hideProgress();
                    }
                });
    }

    private void saveUserConfiguration(String key){
        FirebaseDatabase.getInstance().getReference().child(Utilities.FirebaseUtilities.USER_CONTRIBUTION_KEY)
                .child(Utilities.FirebaseUtilities.mAuth.getUid())
                .push().setValue(key).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ContinuePostActivity.this, "Updations made.Go and refresh the stories page to see the changes.", Toast.LENGTH_SHORT).show();
                    finish();
                } else{
                    Toast.makeText(ContinuePostActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
                hideProgress();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ContinuePostActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                hideProgress();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        showAlert(message);
    }

    void showAlert(String message){
        dialog=new AlertDialog.Builder(this).setTitle("Note")
                .setMessage(message)
                .setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setCancelable(true).create();
        dialog.show();
    }


}
