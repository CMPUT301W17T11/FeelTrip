package com.example.henzoshimada.feeltrip;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.Update;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.mapping.PutMapping;
import io.searchbox.params.Parameters;

/**
 * Created by Esus2 on 2017-03-11.
 * <p>
 * This class is how the application "talks" to Elastic Search
 * This class is what creates and sends the queries, as well as handles the response and the info
 * that will be send to other methods in the app for use
 */
public class ElasticSearchController {
    private static JestDroidClient client;
    private static final String groupIndex = "cmput301w17t11";
    private static final String typeMood = "mood";
    private static final String typeUser = "user";
    private static final String typeRequest = "request";


    /**
     * The Username mapping.
     */
    static PutMapping usernameMapping = new PutMapping.Builder(
            groupIndex,
            typeUser,
            "{ \"user\" : { \"properties\" : { \"userName\" : {\"type\" : \"string\", \"index\" : \"not_analyzed\"} } } }"
            ).refresh(true).build();

    /**
     * The Password mapping.
     */
    static PutMapping passwordMapping = new PutMapping.Builder(
            groupIndex,
            typeUser,
            "{ \"user\" : { \"properties\" : { \"password\" : {\"type\" : \"string\", \"index\" : \"not_analyzed\"} } } }"
            ).refresh(true).build();

    /**
     * The Mood location mapping.
     */
    static PutMapping moodLocationMapping = new PutMapping.Builder(
            groupIndex,
            typeMood,
            "{ \"mood\" : { \"properties\" : { \"location\" : {\"type\" : \"geo_point\"} } } }"
            ).refresh(true).build();

    /**
     * The Mood username mapping.
     */
    static PutMapping moodUsernameMapping = new PutMapping.Builder(
            groupIndex,
            typeMood,
            "{ \"mood\" : { \"properties\" : { \"username\" : {\"type\" : \"string\", \"index\" : \"not_analyzed\"} } } }"
            ).refresh(true).build();

    /**
     * The Request sender mapping.
     */
    static PutMapping requestSenderMapping = new PutMapping.Builder(
            groupIndex,
            typeRequest,
            "{ \"request\" : { \"properties\" : { \"sender\" : {\"type\" : \"string\", \"index\" : \"not_analyzed\"} } } }"
    ).refresh(true).build();

    /**
     * The Request receiver mapping.
     */
    static PutMapping requestReceiverMapping = new PutMapping.Builder(
            groupIndex,
            typeRequest,
            "{ \"request\" : { \"properties\" : { \"receiver\" : {\"type\" : \"string\", \"index\" : \"not_analyzed\"} } } }"
    ).refresh(true).build();


    /**
     * Load from elastic search.
     * <p>
     * This is the method that will load either the Participants moods if they are on the profile
     * page, or only other users that they are following or have their moods as "public" if they are
     * on the home page.
     */
    public static void loadFromElasticSearch(){
        ArrayList<Mood> moodArray =  FeelTripApplication.getMoodArrayList();
        moodArray.clear();

        // no internet connection, load local cache into moodArrayList
        if (!FeelTripApplication.getNetworkAvailable()){
            if (FeelTripApplication.getFrag().equals("main")){
                ArrayList<Mood> localCache = FeelTripApplication.getLocalMainList();
                moodArray.addAll(localCache);
            }
            else if(FeelTripApplication.getFrag().equals("profile")){
                ArrayList<Mood> localCache = FeelTripApplication.getLocalProfileList();
                moodArray.addAll(localCache);
            }
            return;
        }

        GetFilteredMoodsTask getMoodTask;
        if(FeelTripApplication.getFrag().equals("main") || FeelTripApplication.getFrag().equals("profile") || FeelTripApplication.getFrag().equals("map")) {
            getMoodTask = new GetFilteredMoodsTask(FeelTripApplication.getFrag());
        }
        else {
            Log.i("Error", "The given search mode is invalid.");
            return;
        }
        getMoodTask.execute();
        try {
            FeelTripApplication.getMoodArrayList().addAll(getMoodTask.get());
            Log.d("mood array", "moodArrayList");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    /**
     * The type Add mood task.
     * This is the method that will add a mood to the database of Elastic search
     */
    public static class AddMoodTask extends AsyncTask<Mood, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Mood ... moods ) {
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            verifySettings();

            for (Mood mood : moods) {
                mood.setMade(new Date()); // document around the instant the mood is actually built and posted onto elasticsearch
                if(mood.getMade() != null) {
                    Index index = new Index.Builder(mood).index(groupIndex).type(typeMood).refresh(true).build();

                    try {
                        // where is the client?
                        client.execute(moodUsernameMapping); // Sets username to be a non_indexed string on elasticsearch
                        client.execute(moodLocationMapping); // Sets type of location to be "geo_point" on elasticsearch
                        DocumentResult result = client.execute(index);
                        if (result.isSucceeded()) {
                            mood.setId(result.getId());
                        } else {
                            Log.i("Error",mood.getDescription());
                            Log.i("Error",mood.getId());
                            Log.i("Error", "Elasticsearch was not able to add the mood");
                            mood.setAdd();
                            UpdateQueueController updateQueueController = FeelTripApplication.getUpdateQueueController();
                            updateQueueController.addMood(mood);
                            FeelTripApplication.getLocalProfileList().add(0, mood);
                        }
                    } catch (Exception e) {
                        Log.i("Error", ""+e);
                        mood.setAdd();
                        UpdateQueueController updateQueueController = FeelTripApplication.getUpdateQueueController();
                        updateQueueController.addMood(mood);
                        FeelTripApplication.getLocalProfileList().add(0, mood);

                    }
                }
            }
            return true;
        }
    }

    /**
     * The type Edit mood task.
     * This is what runs if the participant is on their profile page and they select one of their
     * moods. It will automatically expand for editing if the participant chooses to change anything
     */
    public static class EditMoodTask extends AsyncTask<Mood, Void, Boolean>{ //TODO: Take concurrency into account

        @Override
        protected Boolean doInBackground(Mood ... moods ) {
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            verifySettings();

            for (Mood mood : moods) {
                if (!mood.isChanged()){
                    Log.d("query", "No changes to the mood were found");
                    return null;
                }
                String moodId = mood.getId();
                if (moodId.equals("-1")) { //mood doesn't exist within the elasticsearch database yet, so we can't edit it
                    Log.i("Error", "This mood does not exist within the Elasticsearch database");
                    return null;
                }
                mood.setMade(new Date()); // document around the instant the mood is edited and update accordingly
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
                                String desc = mood.getDescription().replace("\\","\\\\");
                                query += ("\"description\" : \"" + desc.replace("\"","\\\"") + "\""); // adds support for us to have \ and " chars while editing description.
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
                                if(mood.getImage() == null) {
                                    query += ("\"image\" : " + mood.getImage());
                                }
                                else {
                                    query += ("\"image\" : \"" + mood.getImage() + "\"");
                                }
                                if (notDone != 0) {
                                    query += (",");
                                }
                                break;
                            case 6:
                                if(mood.getLocation() == null) {
                                    query += ("\"location\" : " + mood.getLocation());
                                }
                                else {
                                    query += ("\"location\" : \"" + mood.getLocation() + "\"");
                                }
                                if (notDone != 0) {
                                    query += (",");
                                }
                                break;
                            case 7:
                                query += ("\"emoji\" : " + mood.getEmoji());
                                if (notDone != 0) {
                                    query += (",");
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
                    query += ",\"made\" : " + mood.getMade() + "}}";

                    try{
                        
                        Update update = new Update.Builder(query).index(groupIndex).type(typeMood).id(moodId).refresh(true).build();
                        client.execute(update);

                    }catch (Exception e){
                        Log.i("Error", "The application failed to build and send the moods");
                        UpdateQueueController updateQueueController = FeelTripApplication.getUpdateQueueController();
                        updateQueueController.addMood(mood);

                        return null;
                    }

                    Log.d("update query: ", query);
                }
            return true;
        }
    }


    /**
     * The type Delete mood task.
     * Deletes the mood locally and from the Elastic search database
     */
    public static class DeleteMoodTask extends AsyncTask<Mood, Void, Void> {

        @Override
        protected Void doInBackground(Mood ... moods ) {
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            verifySettings();

            for (Mood mood : moods) {
                String moodId = mood.getId();
                Delete delete = new Delete.Builder(moodId).index(groupIndex).type(typeMood).refresh(true).build();

                try {
                    
                    client.execute(delete);
                }
                catch (Exception e) {
                    Log.i("Error", "The application failed to build and delete the moods");
                    mood.setDel();
                    UpdateQueueController updateQueueController = FeelTripApplication.getUpdateQueueController();
                    updateQueueController.addMood(mood);
                    FeelTripApplication.getLocalProfileList().remove(mood);
                }
            }
            return null;
        }
    }

    /**
     * The type Get mood task.
     */
// call constructor GetMoodTask(String filterBy) if filtering by a specific field
    // call constructor GetMoodTask() if fetching all moods
    public static class GetMoodTask extends AsyncTask<String, Void, ArrayList<Mood>> {
        private String fieldToSearch;

        /**
         * Instantiates a new Get mood task.
         *
         * @param filterBy the filter by
         */
        public GetMoodTask(String filterBy){
            this.fieldToSearch = filterBy;
        }

        /**
         * Instantiates a new Get mood task.
         */
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

    /**
     * The type Add participant task.
     * This method is used when registering a new user onto the Elastic Seach database.
     * It will check if the username used is unique and not used before and will create an account
     * for that username, otherwise an error message will be sent to the participant
     */
    public static class AddParticipantTask extends AsyncTask<Participant, Void, Void> {

        @Override
        protected Void doInBackground(Participant ... participants ) {
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            verifySettings();

            for (Participant participant : participants) {
                Index index = new Index.Builder(participant).index(groupIndex).type(typeUser).refresh(true).build();

                try {
                    // where is the client?
                    client.execute(new CreateIndex.Builder(groupIndex).build());
                    client.execute(usernameMapping); // Sets the index of important exact value strings to "non_analyzed" within elasticsearch
                    client.execute(passwordMapping);
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

    /**
     * The type Delete participant task.
     */
    public static class DeleteParticipantTask extends AsyncTask<Participant, Void, Void> {

        @Override
        protected Void doInBackground(Participant ... participants ) {
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            verifySettings();

            for (Participant participant : participants) {
                String userId = participant.getId();
                Delete delete = new Delete.Builder(userId).index(groupIndex).type(typeUser).refresh(true).build();

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

    /**
     * The type Get participant task.
     */
    public static class GetParticipantTask extends AsyncTask<String, Void, ArrayList<Participant>> {

        private String username = null;
        private String password = null;

        private boolean searchParticipant = false;

        /**
         * Instantiates a new Get participant task.
         *
         * @param username the username
         * @param password the password
         */
        public GetParticipantTask(String username, String password) { // must specify username and password upon creation of the controller
            this.username = username;
            this.password = password;
        }

        /**
         * Instantiates a new Get participant task.
         *
         * @param searchParticipant the search participant
         * @param username          the username
         */
        public GetParticipantTask(boolean searchParticipant, String username){
            this.searchParticipant = searchParticipant;
            this.username = username;
        }

        @Override
        protected ArrayList<Participant> doInBackground(String... params) {
            if (android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            verifySettings();

            ArrayList<Participant> participants = new ArrayList<>();
            String query; // elasticsearch bool queries are amazing in every way

            if (searchParticipant){
                query = "{" +
                        "\"query\" : {" +
                        "\"term\" : {" +
                        "\"userName\" : \"" + username + "\"" +
                        "}}}";
            }
            else {
                if (password == null){
                    return null;
                }
                query = "{" +
                        "\"query\" : {" +
                        "\"bool\" : {" +
                        "\"must\" : [" +
                        "{ \"term\": { \"userName\": \"" + username + "\" }}," +
                        "{ \"term\": { \"password\": \"" + password + "\" }}" +
                        "]}}}";
            }

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

    /**
     * The type Edit participant task.
     */
    public static class EditParticipantTask extends AsyncTask<Participant, Void, Void>{
        private String fieldToEdit;

        /**
         * Instantiates a new Edit participant task.
         *
         * @param fieldToEdit the field to edit
         */
        public EditParticipantTask(String fieldToEdit){
            this.fieldToEdit = fieldToEdit;
        }

        @Override
        protected Void doInBackground(Participant...participants){
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            verifySettings();

            for (Participant participant : participants){

                String query;

                if (fieldToEdit.equals("following")) {
                    String following = new Gson().toJson(participant.getFollowing());
                    query = "{\"doc\" : {" +
                            "\"following\" : " + following +
                            "}}";

                }
                else if (fieldToEdit.equals("geoLocation")){

                    query = "{\"doc\" : {" +
                            "\"longitude\" : \"" + participant.getLongitude() + "\" ," +
                            "\"latitude\" : \"" + participant.getLatitude() + "\"" +
                            "}}";
                }
                else{
                    return null;
                }

                Log.d("query is :", query);

                Update update = new Update
                        .Builder(query)
                        .index(groupIndex)
                        .type(typeUser)
                        .id(participant.getId())
                        .build();
                try {
                    client.execute(update);
                } catch (IOException e) {
                    Log.i("Error", "The application failed to build and send request");
                }
            }


            return null;
        }
    }


    /**
     * The type Add request task.
     * This mood is what adds a request to Elastic search to follow another user
     */
    public static class AddRequestTask extends AsyncTask<FollowRequest, Void, Void> {

        @Override
        protected Void doInBackground(FollowRequest ... followRequests ) {
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            verifySettings();

            for (FollowRequest followRequest : followRequests) {
                    Index index = new Index
                            .Builder(followRequest)
                            .index(groupIndex)
                            .type(typeRequest)
                            .build();

                    try {
                        client.execute(requestSenderMapping);
                        client.execute(requestReceiverMapping);
                        DocumentResult result = client.execute(index);
                        if (result.isSucceeded()) {
                            followRequest.setId(result.getId());
                        } else {
                            Log.i("Error", "Elasticsearch was not able to add the request");
                        }
                    } catch (Exception e) {
                        Log.i("Error", "The application failed to build and send the requests");
                    }
            }
            return null;
        }
    }

    /**
     * The type Get request task.
     */
    public static class GetRequestTask extends AsyncTask<String, Void, ArrayList<FollowRequest>>{
        private boolean checkAccept;

        /**
         * Instantiates a new Get request task.
         *
         * @param checkAccept the check accept
         */
        public GetRequestTask(boolean checkAccept){
            this.checkAccept = checkAccept;
        }

        @Override
        protected ArrayList<FollowRequest> doInBackground(String... username){
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            verifySettings();

            ArrayList<FollowRequest> followRequests = new ArrayList<>();
            String query;
            if (checkAccept){
                query = "{" +
                        "\"query\" : {" +
                        "\"bool\" : {" +
                        "\"must\" : [" +
                        "{ \"term\": { \"sender\": \"" + username[0] + "\" }}," +
                        "{ \"term\": { \"accepted\": \"true\" }}" +
                        "]}}}";
            }
            else {
                if (username.length == 1) {
                    query = "{" +
                            "\"query\" : {" +
                            "\"bool\" : {" +
                            "\"must\" : [" +
                            "{ \"term\": { \"receiver\": \"" + username[0] + "\" }}," +
                            "{ \"term\": { \"accepted\": \"false\" }}" +
                            "]}}}";
                }
                else {
                    query = "{" +
                            "\"query\" : {" +
                            "\"bool\" : {" +
                            "\"must\" : [" +
                            "{ \"term\": { \"sender\": \"" + username[0] + "\" }}," +
                            "{ \"term\": { \"receiver\": \"" + username[1] + "\" }}," +
                            "{ \"term\": { \"accepted\": \"false\" }}" +
                            "]}}}";
                }
            }

            Log.d("query", query);
            Search search = new Search.Builder(query)
                    .addIndex(groupIndex)
                    .addType(typeRequest)
                    .build();


            try {
                SearchResult result = client.execute(search);
                if (result.isSucceeded()){
                    List<FollowRequest> foundRequests = result.getSourceAsObjectList(FollowRequest.class);
                    followRequests.addAll(foundRequests);
                }
                else {
                    Log.i("Error", "the search query failed to find any request that matched");
                }
            }
            catch (Exception e) {
                Log.i("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
            }

            return followRequests;
        }
    }

    /**
     * The type Edit request task.
     */
    public static class EditRequestTask extends AsyncTask<FollowRequest, Void, Void>{

        @Override
        protected Void doInBackground(FollowRequest...followRequests){
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            verifySettings();

            for (FollowRequest followRequest : followRequests){

                String query = "{\"doc\" : {"+
                        "\"accepted\" : \"true\"" +
                        "}}";


                Log.d("query is :", query);

                Update update = new Update
                        .Builder(query)
                        .index(groupIndex)
                        .type(typeRequest)
                        .id(followRequest.getId())
                        .build();
                try {
                    client.execute(update);
                } catch (IOException e) {
                    Log.i("Error", "The application failed to build and send request");
                }
            }


            return null;
        }
    }


    /**
     * The type Delete request task.
     */
    public static class DeleteRequestTask extends AsyncTask<FollowRequest, Void, Void> {

        @Override
        protected Void doInBackground(FollowRequest ... followRequests ) {
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            verifySettings();

            for (FollowRequest followRequest : followRequests) {
                String id = followRequest.getId();
                Delete delete = new Delete
                        .Builder(id)
                        .index(groupIndex)
                        .type(typeRequest)
                        .build();

                try {
                    client.execute(delete);
                }
                catch (Exception e) {
                    Log.i("Error", "The application failed to delete the request");
                }
            }
            return null;
        }
    }


    /**
     * The type Get username task.
     */
    public static class GetUsernameTask extends AsyncTask<String, Void, ArrayList<Participant>> {

        private String username;

        /**
         * Instantiates a new Get username task.
         *
         * @param username the username
         */
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
                    "\"term\" : {" +
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

    /**
     * The Get filtered moods task.
     */
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
        private String keyword; // stores the passed keyword we're filtering by
        private String following = "[\"\"]"; // stores the string containing all the users the participant follows, initialize it to "blank" array by default
        private String participant; // stores the participant's username
        private Double currentlat = null;
        private Double currentlon = null;

        /**
         * Instantiates a new Get filtered moods task.
         *
         * @param searchmode the searchmode
         */
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
                    currentlat = FeelTripApplication.getLatitude();
                    currentlon = FeelTripApplication.getLongitude();
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
            if(!FeelTripApplication.getFilterController().getKeywordfilter().isEmpty()) {
                keywordfilter = true;
                this.keyword =  FeelTripApplication.getFilterController().getKeywordfilter();
            }
        }

        @Override
        protected ArrayList<Mood> doInBackground(String... search_parameters) {
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            verifySettings();

            ArrayList<Mood> moods = new ArrayList<>();
            ArrayList<Mood> localCache = null;
            String query; // things are going to get complicated very fast now, booleans are here to understand whether a filter is being applied or not
            query = "{" +
                    "\"query\" : {" +
                    "\"filtered\" : {" +
                    "\"query\" : {" +
                    "\"bool\" : {";

            if(pastweekfilter) {
                Calendar cal = new GregorianCalendar(); // for the purposes of grabbing exact time one week prior (this takes DST into account)
                long thisweek = cal.getTimeInMillis();
                cal.add(Calendar.DAY_OF_MONTH, -7);
                long lastweek = cal.getTimeInMillis();
                query += "\"must\" : { \"range\" : { \"date\" : { \"gte\" : " + lastweek + ", \"lte\" : " + thisweek + "}}},";
            }

            if(emotionfilter) {
                query += "\"must\" : { \"match\" : { \"emotionalState\" : \"" + emotion + "\" }},";
            }

            if(keywordfilter) {
                query += "\"must\" : { \"match\" : { \"description\" : \"" + keyword + "\" }},";
            }

            if(mainmode) {
                query += "\"must\" : { \"bool\" : {";
                if(!friendsonlyfilter) { // note this line says if NOT friendsonly filter... aka we add public as well
                    query += "\"should\" : { \"term\" : { \"isPrivate\" : false }},";
                }
                query += "\"should\" : { \"terms\" : { \"username\" : " + following + " }}" +
                         "}}," +
                         "\"must_not\" : { \"term\" : { \"username\" : \"" + participant + "\" }}" +
                         "}}";
                localCache = FeelTripApplication.getLocalMainList();
            }

            else if (profilemode) {
                query += "\"must\" : { \"term\" : { \"username\" : \"" + participant + "\" }}" +
                         "}}";
                localCache = FeelTripApplication.getLocalProfileList();
            }

            else if (mapmode) {
                query += "\"must\" : { \"bool\" : {";
                if (!friendsonlyfilter) { // note this line says if NOT friendsonly filter... aka we add public as well
                    query += "\"should\" : { \"term\" : { \"isPrivate\" : false }},";
                }
                query += "\"should\" : { \"terms\" : { \"username\" : " + following + " }}" +
                         "}}," +
                         "\"must_not\" : { \"term\" : { \"username\" : \"" + participant + "\" }}" +
                         "}}," +
                         "\"filter\" : { \"geo_distance\" : { \"distance\" : \"5km\", \"location\" : { \"lat\" : " + currentlat + ", \"lon\" : " + currentlon + "}}}";
            }

            query += "}},";

            query +="\"sort\" : { \"made\": { \"order\": \"desc\" }}" +
                    "}";

            Log.d("query", query);

            Search search = new Search.Builder(query)
                    .addIndex(groupIndex)
                    .addType(typeMood)
                    .setParameter(Parameters.SIZE, 10000) // TODO: Our app likely won't have more than 10000 posts displyaed at a time, but y'know... maybe we'll modify this.
                    .refresh(true).build();


            try {
                
                SearchResult result = client.execute(search);
                if (result.isSucceeded()){
                    List<Mood> foundMoods = result.getSourceAsObjectList(Mood.class);
                    moods.addAll(foundMoods);
                    if(!mostrecentfilter) {
                        if (!profilemode) { // take into account that we only want the most recent post of each user - so we'll use a LinkedHashMap to de-duplicate by username
                            List<Mood> revmoods = Lists.reverse(moods); // Reverse the order of the moods list since the LinkedHashMap will squash the duplicates in a "Last Man Standing" format
                            LinkedHashMap<String, Mood> map = new LinkedHashMap<>();
                            for (Mood mood : revmoods) {
                                map.put(mood.getUsername(), mood);
                            }
                            moods.clear();
                            moods.addAll(map.values());
                            Collections.sort(moods);
                        }
                        if (localCache != null) {
                            localCache.clear();
                            localCache.addAll(foundMoods);
                        }
                    } else {
                        moods.subList(1, moods.size()).clear();

                        if (localCache != null) {
                            localCache.clear();
                            localCache.addAll(moods);
                        }

                        return moods;
                    }
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

    /**
     * Verify settings.
     */
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
