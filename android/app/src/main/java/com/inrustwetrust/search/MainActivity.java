package com.inrustwetrust.search;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.sun.jna.Pointer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    Button   searchButton;
    EditText searchBox;
    TextView text;
    ListView result_list;
    Index    index;
    ArrayList<Talk> talks;
    ArrayList<String> results;
    ArrayAdapter<String> adapter;

    String loadJSON(String filename) {
        String json = null;
        try {
            InputStream is = getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    ArrayList<Talk> loadTalks(String jsonData) {
        ArrayList<Talk> talks = new ArrayList<Talk>();
        try {
            JSONObject obj = new JSONObject(jsonData);
            JSONArray slots = obj.getJSONArray("slots");

            for (int i = 0; i < slots.length(); i++) {
                JSONObject slot = slots.getJSONObject(i);
                String room_id = slot.getString("roomId");
                //Log.d("search", "room: "+room_id);
                JSONObject talk = slot.optJSONObject("talk");
                if(talk == null) {
                    //Log.d("search", "no talk here");
                } else {
                    String title   = talk.getString("title");
                    String summary = talk.getString("summary");
                    //Log.d("search", "title: "+title);
                    JSONArray speakers = talk.getJSONArray("speakers");
                    String speaker_list = speakers.getJSONObject(0).getString("name");
                    for (int j = 1; j < speakers.length(); j++) {
                        String name = speakers.getJSONObject(j).getString("name");
                        speaker_list += ", "+name;
                    }

                    //Log.d("search", title+ " - "+speaker_list+": "+summary);
                    Talk t = new Talk(speaker_list, title, summary);
                    talks.add(t);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return talks;
    }

    void displaySearch(String query) {
        Log.d("search", "searching for \""+query+"\"");
        long start = System.nanoTime();
        final Set<Integer> res = index.searchString(query);
        long end = System.nanoTime();

        Log.d("search", "results for \""+query+"\": " + res.toString());
        String s = "found " +Integer.toString(res.size())+" talks in " + (end - start) / 1000 + " microseconds\n";
        results.clear();
        for(Integer i: res) {
            Talk t = talks.get(i);
            Log.d("search", t.speakerList+": "+ t.title + " - " + t.summary);
            s += t.title + "\n";
            s += t.summary + "\n\n";

            String s2 = t.title + "\n\n" + t.summary;
            results.add(s2);
        }

        final String txt = s;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(txt);
                Log.d("search", "found "+Integer.toString(res.size())+" talks");
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text         = (TextView) findViewById(R.id.text);
        searchBox    = (EditText) findViewById(R.id.search_box);
        searchButton = (Button)   findViewById(R.id.search_button);
        result_list  = (ListView) findViewById(R.id.search_results);

        results = new ArrayList<>();
        adapter = new ArrayAdapter<String>(
                MainActivity.this,
                android.R.layout.simple_list_item_1,
                results);

        result_list.setAdapter(adapter);

        new AsyncTask<Object, Object, Void>(){
            @Override
            protected Void doInBackground(Object... params) {
                long start = System.nanoTime();
                talks = new ArrayList<>();
                talks.addAll(loadTalks(loadJSON("monday.json")));
                talks.addAll(loadTalks(loadJSON("tuesday.json")));
                talks.addAll(loadTalks(loadJSON("wednesday.json")));
                talks.addAll(loadTalks(loadJSON("thursday.json")));
                talks.addAll(loadTalks(loadJSON("friday.json")));
                long talks_loaded = System.nanoTime();

                index = new Index();
                for(int i = 0; i < talks.size(); i++) {
                    index.insert(i, talks.get(i).summary);
                }
                long index_created = System.nanoTime();

                //displaySearch("java build");

                Log.d("search", "talks decoded in "+ (talks_loaded - start) / 1000000 + " milliseconds");
                Log.d("search", "index created in "+ (index_created - talks_loaded) / 1000000 + " milliseconds");

                Log.d("search", Integer.toString(talks.size())+" talks stored");
                return null;
            }

            @Override
            protected void onPostExecute(Void s) {

            }
        }.execute();


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = searchBox.getText().toString();
                displaySearch(query);
            }
        });

        int res = Rust.INSTANCE.add(30,12);
        Log.d("search", "JNA returned "+Integer.toString(res));

        Pointer idx = Rust.INSTANCE.index_create();
    }
}
