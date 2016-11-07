package inrustwetrust.com.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static android.R.attr.data;

/**
 * Created by geal on 07/11/2016.
 */

public class Index {
    Map<String, HashSet<Integer>> index;

    public Map<String, HashSet<Integer>> getIndex() {
        return index;
    }

    public Index() {
        index = new HashMap<String, HashSet<Integer>>();
    }

    public void insert(Integer id, String data) {
        String[] words   = data.split(" ");

        for (String _word : words) {
            String w = _word.toLowerCase();
            HashSet s = index.get(w);
            if(s == null) {
                s = new HashSet<Integer>();
                index.put(w, s);
            }
            s.add(id);
        }
    }

    public HashSet<Integer> searchWord(String word) {
        HashSet<Integer> res = index.get(word);

        if(res == null) {
            return new HashSet<Integer>();
        }
        return res;
    }

    public Set<Integer> searchString(String s) {
        String[] words   = s.split(" ");

        HashSet<Integer> res = searchWord(words[0]);

        for(int i = 1; i < words.length; i++) {
            res.retainAll(searchWord(words[i]));
        }

        return res;
    }
}
