package com.inrustwetrust.search;


import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

/**
 * Created by geal on 07/11/2016.
 */

public interface RustIndex extends Library {
    String JNA_LIBRARY_NAME = "index";
    NativeLibrary JNA_NATIVE_LIB = NativeLibrary.getInstance(JNA_LIBRARY_NAME);
    RustIndex INSTANCE = (RustIndex) Native.loadLibrary(JNA_LIBRARY_NAME, RustIndex.class);

    int add(int a, int b);
    Index index_create();
    void index_free(Index index);
}
