package jarachnea;

/* import java.io.*;
import java.net.*;
import java.text.*; */

import java.lang.*;

import jarachnea.*;


public class Relation {
    public static final Integer IS_FOLLOWED_BY = 0;
    public static final Integer IS_FOLLOWER_OF = 1;

    public Handle profileHandle;
    public Handle relationHandle;
    public int relationType;
    public int relationPageNumber;

    public Relation(Handle profileHandleObj, Handle relationHandleObj, int relationTypeFlag, int relationPageNumberInt) {
        profileHandle = profileHandleObj;
        relationHandle = relationHandleObj;
        relationType = relationTypeFlag;
        relationPageNumber = relationPageNumberInt;
    }

    public Relation(String profileUsername, String profileInstance, Handle relationHandleObj, int relationTypeFlag, int relationPageNumberInt) {
        profileHandle = new Handle(profileUsername, profileInstance);
        relationHandle = relationHandleObj;
        relationType = relationTypeFlag;
        relationPageNumber = relationPageNumberInt;
    }

    public Relation(String profileUsername, String profileInstance, String relationUsername, String relationInstance, int relationTypeFlag, int relationPageNumberInt) {
        profileHandle = new Handle(profileUsername, profileInstance);
        relationHandle = new Handle(relationUsername, relationInstance);
        relationType = relationTypeFlag;
        relationPageNumber = relationPageNumberInt;
    }

    public Relation(Handle profileHandleObj, String relationUsername, String relationInstance, int relationTypeFlag, int relationPageNumberInt) {
        profileHandle = profileHandleObj;
        relationHandle = new Handle(relationUsername, relationInstance);
        relationType = relationTypeFlag;
        relationPageNumber = relationPageNumberInt;
    }
}
