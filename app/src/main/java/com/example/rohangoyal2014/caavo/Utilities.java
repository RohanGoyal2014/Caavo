package com.example.rohangoyal2014.caavo;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class Utilities {
    public static class FirebaseUtilities{
        public static FirebaseAuth mAuth;
        public static final String STORIES="stories";
        public static final String STORY_TITLE_KEY="story_title";
        public static final String STORY_CONTENT_KEY="story_content";
        public static final String START_NODE_KEY="start_node";
        public static final String CONTRIBUTOR_COUNT_KEY="contributor_count";
        public static final String GENRE_KEY="genre";
        public static final String WORD_COUNT_KEY="word_count";
        public static final String STORY_ID_KEY="story_id";
        public static final String USER_CONTRIBUTION_KEY="user_contribution";
        public static final String TIME_KEY="time";
    }
    public static final String POST_DATA_TRANSFER_KEY="bundle_post";

    public static ArrayList<String> getGenresList() {
        ArrayList<String> genres = new ArrayList<>();
        genres.add("Comedy");
        genres.add("Drama");
        genres.add("Horror");
        genres.add("Romance");
        genres.add("Tragedy");
        genres.add("Adventure");
        genres.add("Mythology");
        return genres;
    }

    public static int calculateWords(String s){

        int wordCount = 0;

        boolean word = false;
        int endOfLine = s.length() - 1;

        for (int i = 0; i < s.length(); i++) {
            // if the char is a letter, word = true.
            if (Character.isLetter(s.charAt(i)) && i != endOfLine) {
                word = true;
                // if char isn't a letter and there have been letters before,
                // counter goes up.
            } else if (!Character.isLetter(s.charAt(i)) && word) {
                wordCount++;
                word = false;
                // last word of String; if it doesn't end with a non letter, it
                // wouldn't count without this.
            } else if (Character.isLetter(s.charAt(i)) && i == endOfLine) {
                wordCount++;
            }
        }
        return wordCount;
    }

}
