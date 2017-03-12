package com.example.henzoshimada.feeltrip;

import android.os.AsyncTask;
import android.util.Log;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.util.ArrayList;
import java.util.List;

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
    private static final String groupIndex = "cmput301W17T11";

    public static class AddMoodTask extends AsyncTask<Mood, Void, Void> {

        @Override
        protected Void doInBackground(Mood ... moods ) {
            verifySettings();

            for (Mood mood : moods) {
                Index index = new Index.Builder(mood).index(groupIndex).type("mood").build();

                try {
                    // where is the client?
                    DocumentResult result = client.execute(index);
                    if (result.isSucceeded()){
                        mood.setId(result.getId());
                    }
                    else{
                        Log.i("Error", "Elasticsearch was not able to add the mood");
                    }
                }
                catch (Exception e) {
                    Log.i("Error", "The application failed to build and send the moods");
                }
            }
            return null;
        }
    }

    public static class EditMoodTask extends AsyncTask<Mood, Void, Void>{

        @Override
        protected Void doInBackground(Mood ... moods ) {
            verifySettings();

            for (Mood mood : moods) {
                if (!mood.isChanged()){
                    return null;
                }
                String moodId = mood.getId();
                //Index index = new Index.Builder(mood).index(groupIndex).type("mood").id(moodId).build();
                String query = "{\n";
                Boolean changed;
                // find out what fields have been changed and build query accordingly
                for (int i = 0; i < 7; i++){
                    changed = mood.getStateByIndex(i);
                    if (changed){
                        switch (i){
                            case 0:
                                query += ("mood : " + mood.getMoodOption());
                                break;
                            case 1:
                                query += ("description : " + mood.getDescription());
                                break;
                            case 2:
                                query += ("date : " + mood.getDate());
                                break;
                            case 3:
                                query += ("socialSit : " + mood.getSocialSit());
                                break;
                            case 4:
                                query += ("isPrivate : " + mood.getPrivate());
                                break;
                            default:
                                break;
                        }
                    }
                    mood.resetState();
                    query += "}";
                    Log.d("update query: ", query);
                }


                // TODO: find out how to update certain fields
                try{
                    // no idea about correctness
                    Update update = new Update.Builder(query).index(groupIndex).type("mood").id(moodId).build();
                     client.execute(update);

                }catch (Exception e){
                    Log.i("Error", "The application failed to build and send the moods");
                }
            }
                return null;
        }
    }

    public static class GetMoodTask extends AsyncTask<String, Void, ArrayList<Mood>> {
        private String fieldToSearch;
        public GetMoodTask(String filterBy){
            this.fieldToSearch = filterBy;
        }
        @Override
        protected ArrayList<Mood> doInBackground(String... search_parameters) {
            verifySettings();
            if (search_parameters.length == 0){
                return null;
            }

            ArrayList<Mood> moods = new ArrayList<Mood>();

            // build the query
            String query = "{"+
                            "\"query\" : {" +
                                fieldToSearch + ":" + search_parameters[0] + "\"}" +
                            " }";

            Log.d("query", query);

            Search search = new Search.Builder(query)
                    .addIndex(groupIndex)
                    .addType("mood")
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

/* delete query:
client.execute(new Delete.Builder("1")
                .index("twitter")
                .type("tweet")
                .build());
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
