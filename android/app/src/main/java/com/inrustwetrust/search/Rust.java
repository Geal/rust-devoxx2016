package com.inrustwetrust.search;


import com.inrustwetrust.search.rust.SearchResult;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;

/**
 * Created by geal on 07/11/2016.
 */

public interface Rust extends Library {
    String JNA_LIBRARY_NAME = "index";
    NativeLibrary JNA_NATIVE_LIB = NativeLibrary.getInstance(JNA_LIBRARY_NAME);
    Rust INSTANCE = (Rust) Native.loadLibrary(JNA_LIBRARY_NAME, Rust.class);

    int add(int a, int b);
    Pointer index_create();
    void index_free(Pointer index);
    void index_insert(Pointer index, int id, String text);
    Pointer index_search(Pointer index, String text);
    int search_result_count(Pointer result);
    void search_result_free(Pointer result);
    int search_result_get(Pointer result, int index);
}
