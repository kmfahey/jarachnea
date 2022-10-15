package jarachnea;

import java.util.HashSet;
import java.util.Iterator;

import jarachnea.Relation;


public final class RelationSet {
    public Handle profileHandle;
    public int relationType;
    public int relationPageNumber;
    public HashSet<Relation> relationSet;

    public RelationSet(final Handle profileHandleObj, final int relationTypeFlag, final int relationPageNumberInt) {
        profileHandle = profileHandleObj;
        relationType = relationTypeFlag;
        relationPageNumber = relationPageNumberInt;
        relationSet = new HashSet<Relation>();
    }

    public RelationSet(final String profileUsername, final String profileInstance, final int relationTypeFlag, final int relationPageNumberInt) {
        profileHandle = new Handle(profileUsername, profileInstance);
        relationType = relationTypeFlag;
        relationPageNumber = relationPageNumberInt;
        relationSet = new HashSet<Relation>();
    }

    public void addToSet(final Relation relationObj) {
        relationSet.add(relationObj);
    }

    public void clearSet() {
        relationSet.clear();
    }

    public boolean setContains(final Relation relationObj) {
        return relationSet.contains(relationObj);
    }

    public boolean setIsEmpty() {
        return relationSet.isEmpty();
    }

    public Iterator<Relation> setIterator() {
        return relationSet.iterator();
    }

    public boolean removeFromSet(final Relation relationObj) {
        return relationSet.remove(relationObj);
    }

    public int sizeOfSet() {
        return relationSet.size();
    }
}
