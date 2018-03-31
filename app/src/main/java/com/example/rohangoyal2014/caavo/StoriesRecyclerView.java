package com.example.rohangoyal2014.caavo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class StoriesRecyclerView extends RecyclerView.Adapter<StoriesRecyclerView.StoriesViewHolder>{
    private ArrayList<StoryModel> arrayList;
    public StoryItemClickListener storyItemClickListener;

    StoriesRecyclerView(ArrayList<StoryModel> list,StoryItemClickListener listener) {
        arrayList = list;
        storyItemClickListener=listener;
    }

    @Override
    public StoriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.story_item,parent,false);
        final StoriesViewHolder holder=new StoriesViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storyItemClickListener.onStoryItemClick(v,holder.getAdapterPosition());
            }
        });
        return holder;
    }


    @Override
    public void onBindViewHolder(StoriesViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class StoriesViewHolder extends RecyclerView.ViewHolder{
        TextView storyTitleView,storyContentView,contributorsView;
        public StoriesViewHolder(View view){
            super(view);
            storyTitleView=view.findViewById(R.id.story_title);
            storyContentView=view.findViewById(R.id.story_content);
            contributorsView=view.findViewById( R.id.contributors);
        }
        public void bind(int position){
            StoryModel storyModel=arrayList.get(position);
            storyTitleView.setText(storyModel.getStory_title());
            storyContentView.setText(storyModel.getStory_content());
            contributorsView.setText(String.valueOf(storyModel.getContributor_count()).concat(" contributors"));
        }

    }

}
