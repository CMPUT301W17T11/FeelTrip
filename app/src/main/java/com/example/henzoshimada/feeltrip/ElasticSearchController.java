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
                        Log.i("Error", "Elasticsearch was not able to add the tweet");
                    }
                }
                catch (Exception e) {
                    Log.i("Error", "The application failed to build and send the tweets");
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
                Index index = new Index.Builder(mood).index(groupIndex).type("mood").build();

                try{
                    // no idea what to do

                }catch (Exception e){
                    Log.i("Error", "The application failed to build and send the tweets");
                }

            }
                return null;
        }
    }

    // we need a function which gets tweets from elastic search
    public static class GetMoodTask extends AsyncTask<String, Void, ArrayList<Mood>> {
        @Override
        protected ArrayList<Mood> doInBackground(String... search_parameters) {
            verifySettings();
            if (search_parameters.length == 0){
                return null;
            }

            ArrayList<Mood> moods = new ArrayList<Mood>();

            // build the query
            String query = "{"+
                    "\"query\" : {\"term\": { \"message\" : \"" + search_parameters[0] + "\"}}}";
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
                    Log.i("Error", "the search query failed to find any tweets that matched");
                }
            }
            catch (Exception e) {
                Log.i("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
            }

            return moods;
        }
    }



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
