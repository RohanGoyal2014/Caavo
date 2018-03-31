package com.example.rohangoyal2014.caavo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

public class StoriesActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText searchEditText;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private ArrayList<StoryModel> storyModelArrayList;
    TextView noStoriesView;
    private ProgressBar progressBar;
    private StoriesRecyclerView adapter;
    private RecyclerView searchRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories);
        getSupportActionBar().setElevation(0);
        searchEditText=findViewById(R.id.search_bar);
        recyclerView=findViewById(R.id.recycler_view);
        floatingActionButton=findViewById(R.id.fab);
        progressBar=findViewById(R.id.progress);
        noStoriesView=findViewById(R.id.no_stories);
        searchRecyclerView=findViewById(R.id.recycler_view_search);
        floatingActionButton.setOnClickListener(this);

        storyModelArrayList=new ArrayList<>();

        populateStoriesArrayList();
        watchSearch();
    }

    private void populateStoriesArrayList(){
        final ArrayList<StoryModel> temp=new ArrayList<>();
        noStoriesView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase.getInstance().getReference().child(Utilities.FirebaseUtilities.STORIES)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds:dataSnapshot.getChildren()) {
                            String title= ds.child(Utilities.FirebaseUtilities.STORY_TITLE_KEY).getValue().toString();
                            String content=ds.child(Utilities.FirebaseUtilities.STORY_CONTENT_KEY).getValue().toString();
                            String time=ds.child(Utilities.FirebaseUtilities.TIME_KEY).getValue().toString();
                            String noOfContributors=ds.child(Utilities.FirebaseUtilities.CONTRIBUTOR_COUNT_KEY).getValue().toString();
                            String startNode=ds.child(Utilities.FirebaseUtilities.START_NODE_KEY).getValue().toString();
                            String genre=ds.child(Utilities.FirebaseUtilities.GENRE_KEY).getValue().toString();
                            String wordCount=ds.child(Utilities.FirebaseUtilities.WORD_COUNT_KEY).getValue().toString();
                            StoryModel storyModel=new StoryModel(
                                    title,
                                    content,
                                    startNode,
                                    Integer.parseInt(noOfContributors),
                                    genre,
                                    Integer.parseInt(wordCount),
                                    Long.parseLong(time)
                            );
                            temp.add(storyModel);
                        }
                        if(temp.isEmpty()){
                            noStoriesView.setVisibility(View.VISIBLE);
                            storyModelArrayList=temp;
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(StoriesActivity.this);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            recyclerView.setHasFixedSize(true);
                            storyModelArrayList = temp;
                            adapter = new StoriesRecyclerView(storyModelArrayList, new StoryItemClickListener() {
                                @Override
                                public void onStoryItemClick(View v, int pos) {
                                    StoryModel tempStoryModel=storyModelArrayList.get(pos);
                                    ArrayList<String> postData=new ArrayList<>();
                                    postData.add(tempStoryModel.getStory_title());
                                    postData.add(tempStoryModel.getStory_content());
                                    postData.add(tempStoryModel.getStart_node());
                                    postData.add(String.valueOf(tempStoryModel.getContributor_count()));
                                    postData.add(tempStoryModel.getGenre());
                                    postData.add(String.valueOf(tempStoryModel.getWord_count()));
                                    postData.add(String.valueOf(tempStoryModel.getTime()));
                                    startActivity(new Intent(StoriesActivity.this,ContinuePostActivity.class)
                                            .putStringArrayListExtra(Utilities.POST_DATA_TRANSFER_KEY,postData)
                                    );
                                }
                            });
                            recyclerView.setAdapter(adapter);
                        }
                        progressBar.setVisibility(View.GONE);


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(StoriesActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void watchSearch()
    {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String query=s.toString().trim();
                if(query.isEmpty()){
                    recyclerView.setVisibility(View.VISIBLE);
                    searchRecyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
                recyclerView.setVisibility(View.GONE);
                searchRecyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                query=query.toLowerCase();
                final ArrayList<StoryModel> searchArrayList=new ArrayList<>();
                for(int i=0;i<storyModelArrayList.size();++i){
                    StoryModel tmp=storyModelArrayList.get(i);
                    String title=tmp.getStory_title().toLowerCase();
                    String genre=tmp.getGenre().toLowerCase();
                    if(title.contains(query) || genre.contains(query)){
                        searchArrayList.add(tmp);
                    }
                }
                searchRecyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(StoriesActivity.this);
                searchRecyclerView.setLayoutManager(linearLayoutManager);
                searchRecyclerView.setHasFixedSize(true);
                searchRecyclerView.setAdapter(new StoriesRecyclerView(searchArrayList, new StoryItemClickListener() {
                    @Override
                    public void onStoryItemClick(View v, int pos) {
                        StoryModel tempStoryModel=searchArrayList.get(pos);
                        ArrayList<String> postData=new ArrayList<>();
                        postData.add(tempStoryModel.getStory_title());
                        postData.add(tempStoryModel.getStory_content());
                        postData.add(tempStoryModel.getStart_node());
                        postData.add(String.valueOf(tempStoryModel.getContributor_count()));
                        postData.add(tempStoryModel.getGenre());
                        postData.add(String.valueOf(tempStoryModel.getWord_count()));
                        postData.add(String.valueOf(tempStoryModel.getTime()));
                        startActivity(new Intent(StoriesActivity.this,ContinuePostActivity.class)
                                .putStringArrayListExtra(Utilities.POST_DATA_TRANSFER_KEY,postData)
                        );
                    }
                }));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_options,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.sign_out){
            Utilities.FirebaseUtilities.mAuth.signOut();
            startActivity(new Intent(this,AccountsActivity.class));
            finish();
            return true;
        } else if(id==R.id.refresh){
            populateStoriesArrayList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.fab){
            startActivity(new Intent(this,AddPostActivity.class));
        }
    }
}
