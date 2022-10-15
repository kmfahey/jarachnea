package jarachnea;

import java.util.*;

import jarachnea.*;

public class RelationSet {
    public Handle profileHandle;
    public int relationType;
    public int relationPageNumber;
    public HashSet<Relation> relationSet;

    public RelationSet(Handle profileHandleObj, int relationTypeFlag, int relationPageNumberInt) {
        profileHandle = profileHandleObj;
        relationType = relationTypeFlag;
        relationPageNumber = relationPageNumberInt;
        relationSet = new HashSet<Relation>();
    }

    public RelationSet(String profileUsername, String profileInstance, int relationTypeFlag, int relationPageNumberInt) {
        profileHandle = new Handle(profileUsername, profileInstance);
        relationType = relationTypeFlag;
        relationPageNumber = relationPageNumberInt;
        relationSet = new HashSet<Relation>();
    }

    public void addToSet(Relation relationObj) {
        relationSet.add(relationObj);
    }

    public void clearSet() {
        relationSet.clear();
    }

    public boolean setContains(Relation relationObj) {
        return relationSet.contains(relationObj);
    }

    public boolean setIsEmpty() {
        return relationSet.isEmpty();
    }

    public Iterator<Relation> setIterator() {
        return relationSet.iterator();
    }

    public boolean removeFromSet(Relation relationObj) {
        return relationSet.remove(relationObj);
    }

    public int sizeOfSet() {
        return relationSet.size();
    }
}
