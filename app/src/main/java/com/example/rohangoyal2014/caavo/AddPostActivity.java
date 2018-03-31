package com.example.rohangoyal2014.caavo;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddPostActivity extends AppCompatActivity {

    private TextInputEditText titleEditText;
    private TextInputEditText contentEditText;
    private Button submitButton;
    private Spinner spinner;
    private ProgressBar progressBar;
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        getSupportActionBar().setTitle("Add Story");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        titleEditText=findViewById(R.id.post_title);
        contentEditText=findViewById(R.id.post_content);
        submitButton=findViewById(R.id.submit_button);
        progressBar=findViewById(R.id.progress);
        spinner=findViewById(R.id.spinner);
        constraintLayout=findViewById(R.id.form);

        spinner.setAdapter(
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_dropdown_item,
                        Utilities.getGenresList()
                )
        );

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSavingPost();
            }
        });
    }

    private void startSavingPost()
    {
        final String title=titleEditText.getText().toString().trim();
        String content=contentEditText.getText().toString().trim();
        if(title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "None of the fields can be empty", Toast.LENGTH_SHORT).show();
        } else {
            String selectedItem=Utilities.getGenresList().get(spinner.getSelectedItemPosition());
            int words=Utilities.calculateWords(content);
            if(words>50){
                Toast.makeText(this, "The word limit is 50.You have typed "+String.valueOf(words)+ " words", Toast.LENGTH_SHORT).show();
            } else {
                showProgress();
                final long currTime=System.currentTimeMillis();
                StoryModel storyModel = new StoryModel(title, content, Utilities.FirebaseUtilities.mAuth.getUid(), 1, selectedItem,words,currTime);
                FirebaseDatabase.getInstance().
                        getReference().
                        child(Utilities.FirebaseUtilities.STORIES).
                        push().setValue(storyModel).
                        addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    completeUserConfigurationUpdation(String.valueOf(currTime));
                                } else{
                                    Toast.makeText(AddPostActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                                    hideProgress();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddPostActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                                hideProgress();
                            }
                        });
            }
        }
    }

    private void completeUserConfigurationUpdation(final String currTime){
        FirebaseDatabase.getInstance().getReference().child(Utilities.FirebaseUtilities.STORIES)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds:dataSnapshot.getChildren()) {
                            String uid=ds.child(Utilities.FirebaseUtilities.START_NODE_KEY).getValue().toString();
                            String time=ds.child(Utilities.FirebaseUtilities.TIME_KEY).getValue().toString();
                            if(uid.equals(Utilities.FirebaseUtilities.mAuth.getUid()) && time.equals(currTime)){
                                String key=ds.getKey();
                                FirebaseDatabase.getInstance().getReference().child(Utilities.FirebaseUtilities.USER_CONTRIBUTION_KEY)
                                        .child(uid).push().setValue(key).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(AddPostActivity.this, "Story Added!!", Toast.LENGTH_SHORT).show();
                                            hideProgress();
                                            finish();
                                        } else{
                                            Toast.makeText(AddPostActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                                        }
                                        //hideProgress();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AddPostActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                                        hideProgress();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void showProgress(){
        progressBar.setVisibility(View.VISIBLE);
        constraintLayout.setVisibility(View.GONE);
    }

    private void hideProgress(){
        progressBar.setVisibility(View.GONE);
        constraintLayout.setVisibility(View.VISIBLE);
    }
}
