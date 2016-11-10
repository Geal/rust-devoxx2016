package com.inrustwetrust.search;

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
    HashMap<String, HashSet<Integer>> index;

    public HashMap<String, HashSet<Integer>> getIndex() {
        return index;
    }

    public Index() {
        index = new HashMap<String, HashSet<Integer>>();
    }

    public void insert(Integer id, String data) {
        String[] words   = data.split(" ");

        for (String _word : words) {
            String w = _word.toLowerCase().replaceAll("\\p{Punct}", "");
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
        String w0 = words[0].toLowerCase().replaceAll("\\p{Punct}", "");

        HashSet<Integer> res = searchWord(w0);

        for(int i = 1; i < words.length; i++) {
            String wi = words[i].toLowerCase().replaceAll("\\p{Punct}", "");
            res.retainAll(searchWord(wi));
        }

        return res;
    }
}
