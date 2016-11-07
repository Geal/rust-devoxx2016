package com.inrustwetrust.search.rust;

import com.sun.jna.Structure;

/**
 * Created by geal on 07/11/2016.
 */

public class Index {
    public static class ByReference extends Index implements Structure.ByReference {
    }

    public static class ByValue extends Index implements Structure.ByValue {
    }
}
