package com.example.henzoshimada.feeltrip;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.Update;

/**
 * Created by Esus2 on 2017-03-11.
 */

public class ElasticSearchController {
    private static JestDroidClient client;
    private static final String groupIndex = "cmput301w17t11";
    private static final String typeMood = "mood";
    private static final String typeUser = "user";

    public static class AddMoodTask extends AsyncTask<Mood, Void, Void> {

        @Override
        protected Void doInBackground(Mood ... moods ) {
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            verifySettings();

            for (Mood mood : moods) {
                mood.setMade(new Date()); // document around the instant the mood is actually built and posted onto elasticsearch
                if(mood.getMade() != null) {
                    Index index = new Index.Builder(mood).index(groupIndex).type(typeMood).build();

                    try {
                        // where is the client?
                        DocumentResult result = client.execute(index);
                        if (result.isSucceeded()) {
                            mood.setId(result.getId());
                        } else {
                            Log.i("Error", "Elasticsearch was not able to add the mood");
                            mood.setAdd();
                            UpdateQueueController updateQueueController = FeelTripApplication.getUpdateQueueController();
                            updateQueueController.addMood(mood);
                        }
                    } catch (Exception e) {
                        Log.i("Error", "The application failed to build and send the moods");
                        mood.setAdd();
                        UpdateQueueController updateQueueController = FeelTripApplication.getUpdateQueueController();
                        updateQueueController.addMood(mood);
                    }
                }
            }
            return null;
        }
    }

    public static class EditMoodTask extends AsyncTask<Mood, Void, Void>{ // TODO: Fix edit when removing a field

        @Override
        protected Void doInBackground(Mood ... moods ) {
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            verifySettings();

            for (Mood mood : moods) {
                if (!mood.isChanged()){
                    return null;
                }
                String moodId = mood.getId();
                if (moodId == "-1") { //mood doesn't exist within the elasticsearch database yet, so we can't edit it
                    Log.i("Error", "This mood does not exist within the Elasticsearch database");
                    return null;
                }
                String query = "{\"doc\" : {"; // calls the doc function in _update query, automatically upserts if field doesn't exist yet
                // find out what fields have been changed and build query accordingly
                int notDone = 0;
                for (int i = 0; i < 8; i++) { //find out how many fields were changed
                    if (mood.getStateByIndex(i)) {
                        notDone += 1;
                    }
                }
                for (int i = 0; i < 8; i++) {
                    if (mood.getStateByIndex(i)) {
                        notDone -= 1;
                        switch (i) {
                            case 0:
                                query += ("\"emotionalState\" : \"" + mood.getMoodOption() + "\"");
                                if (notDone != 0) {
                                    query += (",");
                                }
                                break;
                            case 1:
                                query += ("\"description\" : \"" + mood.getDescription() + "\"");
                                if (notDone != 0) {
                                    query += (",");
                                }
                                break;
                            case 2:
                                query += ("\"date\" : " + mood.getDate().getTime()); //Date types are compatible with Calendar classes
                                if (notDone != 0) {
                                    query += (",");
                                }
                                break;
                            case 3:
                                query += ("\"socialSit\" : \"" + mood.getSocialSit() + "\"");
                                if (notDone != 0) {
                                    query += (",");
                                }
                                break;
                            case 4:
                                query += ("\"isPrivate\" : " + mood.getPrivate());
                                if (notDone != 0) {
                                    query += (",");
                                }
                                break;
                            case 5:
                                query += ("\"image\" : \"" + mood.getImage() + "\"");
                                if (notDone != 0) {
                                    query += (",");
                                }
                                break;
                            case 6:
                                query += ("\"location\" : \"" + mood.getLocation() + "\"");
                                if (notDone != 0) {
                                    query += (",");
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
                    query += "}}";

                    try{
                        Update update = new Update.Builder(query).index(groupIndex).type(typeMood).id(moodId).build();
                        client.execute(update);

                    }catch (Exception e){
                        Log.i("Error", "The application failed to build and send the moods");
                    }

                    mood.resetState();
                    Log.d("update query: ", query);
                }
                return null;
        }
    }


    public static class DeleteMoodTask extends AsyncTask<Mood, Void, Void> {

        @Override
        protected Void doInBackground(Mood ... moods ) {
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            verifySettings();

            for (Mood mood : moods) {
                String moodId = mood.getId();
                Delete delete = new Delete.Builder(moodId).index(groupIndex).type(typeMood).build();

                try {
                    client.execute(delete);
                }
                catch (Exception e) {
                    Log.i("Error", "The application failed to build and delete the moods");
                    mood.setDel();
                    UpdateQueueController updateQueueController = FeelTripApplication.getUpdateQueueController();
                    updateQueueController.addMood(mood);
                }
            }
            return null;
        }
    }

    // call constructor GetMoodTask(String filterBy) if filtering by a specific field
    // call constructor GetMoodTask() if fetching all moods
    public static class GetMoodTask extends AsyncTask<String, Void, ArrayList<Mood>> {
        private String fieldToSearch;
        public GetMoodTask(String filterBy){
            this.fieldToSearch = filterBy;
        }
        public GetMoodTask(){this.fieldToSearch = null; }

        @Override
        protected ArrayList<Mood> doInBackground(String... search_parameters) {
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            verifySettings();

            ArrayList<Mood> moods = new ArrayList<>();
            String query;
            if (fieldToSearch == null){ //must specify what field you want to search upon creation of controller, should suffice for our purposes
                query = "";
            }
            else {
                query = "{" +
                        "\"query\" : {" +
                        "\"match\" : {" +
                        "\"" + fieldToSearch + "\""+ ": \"" + search_parameters[0] + "\"}" +
                        " }}";
            }
            Log.d("query", query);

            Search search = new Search.Builder(query)
                    .addIndex(groupIndex)
                    .addType(typeMood)
                    .build();


            try {
                SearchResult result = client.execute(search);
                if (result.isSucceeded()){
                    List<Mood> foundMoods = result.getSourceAsObjectList(Mood.class);
                    moods.addAll(foundMoods);
                }
                else {
                    Log.i("Error", "the search query failed to find any moods that matched");
                }
            }
            catch (Exception e) {
                Log.i("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
            }

            return moods;
        }
    }

    public static class AddParticipantTask extends AsyncTask<Participant, Void, Void> { //TODO: What if username already exists within the database? We need usernames to be UNIQUE for proper sorting.

        @Override
        protected Void doInBackground(Participant ... participants ) {
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            verifySettings();

            for (Participant participant : participants) {
                Index index = new Index.Builder(participant).index(groupIndex).type(typeUser).build();

                try {
                    // where is the client?
                    DocumentResult result = client.execute(index);
                    if (result.isSucceeded()){
                        participant.setId(result.getId());
                    }
                    else{
                        Log.i("Error", "Elasticsearch was not able to create user");
                    }
                }
                catch (Exception e) {
                    Log.i("Error", "The application was not able to create the user");
                }
            }
            return null;
        }
    }

    public static class DeleteParticipantTask extends AsyncTask<Participant, Void, Void> {

        @Override
        protected Void doInBackground(Participant ... participants ) {
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            verifySettings();

            for (Participant participant : participants) {
                String userId = participant.getId();
                Delete delete = new Delete.Builder(userId).index(groupIndex).type(typeUser).build();

                try {
                    client.execute(delete);
                }
                catch (Exception e) {
                    Log.i("Error", "The application failed to delete the user");
                }
            }
            return null;
        }
    }

    public static class GetParticipantTask extends AsyncTask<String, Void, ArrayList<Participant>> {

        private String username;
        private String password;

        public GetParticipantTask(String username, String password) { // must specify username and password upon creation of the controller
            this.username = username;
            this.password = password;
        }

        @Override
        protected ArrayList<Participant> doInBackground(String... params) {
            if (android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            verifySettings();

            ArrayList<Participant> participants = new ArrayList<>();
            String query; // elasticsearch bool queries are amazing in every way
            query = "{" +
                    "\"query\" : {" +
                    "\"bool\" : {" +
                    "\"must\" : [" +
                    "{ \"match\": { \"userName\": \"" + username + "\" }}," +
                    "{ \"match\": { \"password\": \"" + password + "\" }}" +
                    "]}}}";

            Log.d("query", query);

            Search search = new Search.Builder(query)
                    .addIndex(groupIndex)
                    .addType(typeUser)
                    .build();


            try {
                SearchResult result = client.execute(search);
                if (result.isSucceeded()) {
                    List<Participant> foundParticipants = result.getSourceAsObjectList(Participant.class);
                    participants.addAll(foundParticipants);
                } else {
                    Log.i("Error", "the search query failed to find any moods that matched");
                }
            } catch (Exception e) {
                Log.i("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
            }

            return participants;
        }
    }

    public static class GetUsernameTask extends AsyncTask<String, Void, ArrayList<Participant>> {

        private String username;

        public GetUsernameTask(String username) { // must specify username and password upon creation of the controller
            this.username = username;
        }

        @Override
        protected ArrayList<Participant> doInBackground(String... params) {
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            verifySettings();

            ArrayList<Participant> participants = new ArrayList<>();
            String query; // elasticsearch bool queries are amazing in every way
            query = "{" +
                    "\"query\" : {" +
                    "\"match\" : {" +
                    "\"userName\""+ ": \"" + username + "\"}" +
                    " }}";

            Log.d("query", query);

            Search search = new Search.Builder(query)
                    .addIndex(groupIndex)
                    .addType(typeUser)
                    .build();


            try {
                SearchResult result = client.execute(search);
                if (result.isSucceeded()){
                    List<Participant> foundParticipants = result.getSourceAsObjectList(Participant.class);
                    participants.addAll(foundParticipants);
                }
                else {
                    Log.i("Error", "the search query failed to find any moods that matched");
                }
            }
            catch (Exception e) {
                Log.i("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
            }

            return participants;
        }
    }

    // The following method is to call respective filter queries on Elasticsearch.
    public static class GetFilteredMoodsTask extends AsyncTask<String, Void, ArrayList<Mood>> { // fetch user's moods, sorted by newest to oldest.
        // These booleans are only here for brief clarity within the already confusing enough filter call.
        private boolean mainmode;
        private boolean profilemode;
        private boolean mapmode;
        private boolean pastweekfilter;
        private boolean mostrecentfilter;
        private boolean emotionfilter;
        private boolean friendsonlyfilter;
        private boolean keywordfilter;

        private String emotion; // stores the passed emotion we're filtering by
        private String following = "[\"\"]"; // stores the string containing all the users the participant follows, initialize it to "blank" array by default
        private String participant; // stores the participant's username
        private Double currentlat;
        private Double currentlon; // TODO: actually pass in the user's current lat and lon to these variables

        public GetFilteredMoodsTask(String searchmode){ // must pass this specific set of Strings in this order while constructing
            switch(searchmode) {
                case "main":
                    participant = FeelTripApplication.getParticipant().getUserName();
                    if(!FeelTripApplication.getParticipant().getFollowing().isEmpty()) {
                        following = "[\"" + TextUtils.join("\",\"", FeelTripApplication.getParticipant().getFollowing()) + "\"]"; // sets up the following array of the participant as a String with proper delimiters for elasticsearch terms query
                    }
                    mainmode = true;
                    break;
                case "profile":
                    participant = FeelTripApplication.getParticipant().getUserName();
                    profilemode = true;
                    break;
                case "map":
                    participant = FeelTripApplication.getParticipant().getUserName();
                    if(!FeelTripApplication.getParticipant().getFollowing().isEmpty()) {
                        following = "[\"" + TextUtils.join("\",\"", FeelTripApplication.getParticipant().getFollowing()) + "\"]"; // sets up the following array of the participant as a String with proper delimiters for elasticsearch terms query
                    }
                    mapmode = true;
                    break;
                default:
                    Log.i("Error", "The given search mode is invalid.");
                    return;
            }
            if(FeelTripApplication.getFilterController().isPastweekfilter()) {
                pastweekfilter = true;
            }
            if(FeelTripApplication.getFilterController().isMostrecentfilter()) {
                mostrecentfilter = true;
            }
            if(!FeelTripApplication.getFilterController().getEmotionfilter().isEmpty()) {
                emotionfilter = true;
                this.emotion = FeelTripApplication.getFilterController().getEmotionfilter();
            }
            if(FeelTripApplication.getFilterController().isFriendsonlyfilter()) {
                friendsonlyfilter = true;
            }
        }

        @Override
        protected ArrayList<Mood> doInBackground(String... search_parameters) {
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            verifySettings();

            if(search_parameters.length != 0) {
                keywordfilter = true;
            }
            ArrayList<Mood> moods = new ArrayList<>();
            String query; // things are going to get complicated very fast now, booleans are here to understand whether a filter is being applied or not
            query = "{" +
                    "\"query\" : {" +
                    "\"bool\" : {";

            if(pastweekfilter) {
                Calendar cal = new GregorianCalendar(); // for the purposes of grabbing exact time one week prior (this takes DST into account)
                cal.add(Calendar.DAY_OF_MONTH, -7);
                long lastweek = cal.getTimeInMillis();
                query += "\"must\" : { \"range\" : { \"made\" : { \"gte\" : " + lastweek + "}}},";
            }

            if(emotionfilter) {
                query += "\"must\" : { \"term\" : { \"emotionalState\" : \"" + emotion + "\" }},";
            }

            if(keywordfilter) {
                query += "\"must\" : { \"term\" : { \"description\" : \"" + search_parameters[0] + "\" }},";
            }

            if(mainmode) {
                query += "\"must\" : { \"bool\" : {";
                if(!friendsonlyfilter) { // note this line says if NOT friendsonly filter... aka we add public as well
                    query += "\"should\" : { \"term\" : { \"isPrivate\" : false }},";
                }
                query += "\"should\" : { \"terms\" : { \"username\" : " + following + " }}" +
                         "}}," +
                         "\"must_not\" : { \"term\" : { \"username\" : \"" + participant + "\" }}";
            }

            else if (profilemode) {
                query += "\"must\" : { \"term\" : { \"username\" : \"" + participant + "\" }}";
            }

            else if (mapmode) {
                query += "\"must\" : { \"geo_distance\" : { \"distance\" : \"5km\", \"location\" : { \"lat\" : " + currentlat + ", \"lon\" : " + currentlon + "}}},";
                query += "\"must\" : { \"bool\" : {";
                if(!friendsonlyfilter) { // note this line says if NOT friendsonly filter... aka we add public as well
                    query += "\"should\" : { \"term\" : { \"isPrivate\" : false }},";
                }
                query += "\"should\" : { \"terms\" : { \"username\" : " + following + " }}" +
                        "}}," +
                        "\"must_not\" : { \"term\" : { \"username\" : \"" + participant + "\" }}";
            }

            query +="}},";

            if (mostrecentfilter) {
                query += "\"size\" : 1,";
            }

            query +="\"sort\" : { \"made\": { \"order\": \"desc\" }}" +
                    "}";

            Log.d("query", query);

            Search search = new Search.Builder(query)
                    .addIndex(groupIndex)
                    .addType(typeMood)
                    .build();


            try {
                SearchResult result = client.execute(search);
                if (result.isSucceeded()){
                    List<Mood> foundMoods = result.getSourceAsObjectList(Mood.class);
                    moods.addAll(foundMoods);
                }
                else {
                    Log.i("Error", "the search query failed to find any moods that matched");
                }
            }
            catch (Exception e) {
                Log.i("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
            }

            return moods;
        }
    }

        public static class GetMyMoodsTask extends AsyncTask<String, Void, ArrayList<Mood>> { // fetch user's moods, sorted by newest to oldest.
        private String username;
        public GetMyMoodsTask(String filterBy){ // must pass username through on creation of controller
            this.username = filterBy;
        }

        @Override
        protected ArrayList<Mood> doInBackground(String... search_parameters) {
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            verifySettings();

            ArrayList<Mood> moods = new ArrayList<>();
            String query;
            // bool queries are absolutely positively amazing
            query = "{" +
                    "\"query\" : {" +
                    "\"bool\" : {" +
                    "\"must\" : { \"term\" : { \"user\" : \"" + username + "\" }}" +
                    "}}," +
                    "\"sort\": { \"made\": { \"order\": \"desc\" }}" +
                    "}";

            Log.d("query", query);

            Search search = new Search.Builder(query)
                    .addIndex(groupIndex)
                    .addType(typeMood)
                    .build();


            try {
                SearchResult result = client.execute(search);
                if (result.isSucceeded()){
                    List<Mood> foundMoods = result.getSourceAsObjectList(Mood.class);
                    moods.addAll(foundMoods);
                }
                else {
                    Log.i("Error", "the search query failed to find any moods that matched");
                }
            }
            catch (Exception e) {
                Log.i("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
            }

            return moods;
        }
    }

    // singleton
    public static void verifySettings() {
        if (client == null) {
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder("http://cmput301.softwareprocess.es:8080");
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client = (JestDroidClient) factory.getObject();
        }
    }
}
