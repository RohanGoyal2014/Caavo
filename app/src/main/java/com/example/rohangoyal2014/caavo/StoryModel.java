package com.example.rohangoyal2014.caavo;

public class StoryModel{
    private String story_title;
    private String story_content;
    private String start_node;
    private int contributor_count;
    private String genre;
    private int word_count;
    private long time;

    public StoryModel(String story_title, String story_content, String start_node, int contributor_count, String genre, int word_count,long time) {
        this.story_title = story_title;
        this.story_content = story_content;
        this.start_node = start_node;
        this.contributor_count = contributor_count;
        this.genre = genre;
        this.word_count = word_count;
        this.time=time;
    }

    public String getStory_title() {
        return story_title;
    }

    public void setStory_title(String story_title) {
        this.story_title = story_title;
    }

    public String getStory_content() {
        return story_content;
    }

    public void setStory_content(String story_content) {
        this.story_content = story_content;
    }

    public String getStart_node() {
        return start_node;
    }

    public void setStart_node(String start_node) {
        this.start_node = start_node;
    }

    public int getContributor_count() {
        return contributor_count;
    }

    public void setContributor_count(int contributor_count) {
        this.contributor_count = contributor_count;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getWord_count() {
        return word_count;
    }

    public void setWord_count(int word_count) {
        this.word_count = word_count;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
