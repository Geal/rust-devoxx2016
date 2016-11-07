package com.inrustwetrust.search.rust;

import com.sun.jna.Structure;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by geal on 07/11/2016.
 */

public class SearchResult extends Structure {
    public static class ByReference extends SearchResult implements Structure.ByReference {
    }

    public static class ByValue extends SearchResult implements Structure.ByValue {
    }
    @Override
    protected List<String> getFieldOrder() {
        return new ArrayList<String>();
    }
}
