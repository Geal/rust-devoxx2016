package com.inrustwetrust.search;

import android.support.annotation.NonNull;

import com.sun.jna.Structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by geal on 07/11/2016.
 */

public class RustIndex extends Structure {
    public static class ByReference extends RustIndex implements Structure.ByReference {
    }

    public static class ByValue extends RustIndex implements Structure.ByValue {
    }

    @Override
    protected List<String> getFieldOrder() {
        return new ArrayList<String>();
    }
}
