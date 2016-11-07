package inrustwetrust.com.search;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    Button   searchButton;
    EditText searchBox;
    TextView text;

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
                Log.d("search", "room: "+room_id);
                JSONObject talk = slot.optJSONObject("talk");
                //slot.optJSONObject()
                if(talk == null) {
                    Log.d("search", "no talk here");
                } else {
                    String title   = talk.getString("title");
                    String summary = talk.getString("summary");
                    Log.d("search", "title: "+title);
                    JSONArray speakers = talk.getJSONArray("speakers");
                    String speaker_list = speakers.getJSONObject(0).getString("name");
                    for (int j = 1; j < speakers.length(); j++) {
                        String name = speakers.getJSONObject(j).getString("name");
                        speaker_list += ", "+name;
                    }


                    Log.d("search", title+ " - "+speaker_list+": "+summary);
                    Talk t = new Talk(speaker_list, title, summary);
                    talks.add(t);
                }

                /*cat_name = jArray.getJSONObject(i).getString("cat_name");
                Log.v("Cat ID", cat_Id);
                Log.v("Cat Name", cat_name);
                data.add(new String[] { cat_Id, cat_name });*/
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return talks;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text         = (TextView) findViewById(R.id.text);
        searchBox    = (EditText) findViewById(R.id.search_box);
        searchButton = (Button) findViewById(R.id.search_button);

        ArrayList<Talk> talks = new ArrayList<>();
        talks.addAll(loadTalks(loadJSON("monday.json")));
        talks.addAll(loadTalks(loadJSON("tuesday.json")));
        talks.addAll(loadTalks(loadJSON("wednesday.json")));
        talks.addAll(loadTalks(loadJSON("thursday.json")));
        talks.addAll(loadTalks(loadJSON("friday.json")));

        Index index = new Index();
        for(int i = 0; i < talks.size(); i++) {
            index.insert(i, talks.get(i).summary);
        }

        for (Map.Entry<String, HashSet<Integer>> entry : index.getIndex().entrySet()) {
            Log.d("search", entry.getKey() + ": "+entry.getValue().toString());
        }

        Set<Integer> res = index.searchString("java build");
        Log.d("search", "results for \"java build\": " + res.toString());
        String s = "results for \"java build\": " +Integer.toString(res.size())+" talks\n";
        for(Integer i: res) {
            Talk t = talks.get(i);
            Log.d("search", t.title + " - " + t.summary);
            s += t.title + "\n";
            s += t.summary + "\n\n";
        }

        text.setText(s);

        Log.d("search", Integer.toString(talks.size())+" talks stored");
    }
}
