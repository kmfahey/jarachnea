package jarachnea;

import java.util.HashSet;
import java.util.Iterator;


public final class RelationSet extends HashSet<Relation> {
    private Handle profileHandle;
    private int relationType;
    private int relationPageNumber;

    public Handle getProfileHandle() {
        return profileHandle;
    }

    public int getRelationType() {
        return relationType;
    }

    public int getRelationPageNumber() {
        return relationPageNumber;
    }


    public RelationSet(final Handle profileHandleObj, final int relationTypeFlag, final int relationPageNumberInt) {
        super();
        profileHandle = profileHandleObj;
        relationType = relationTypeFlag;
        relationPageNumber = relationPageNumberInt;
    }

    public RelationSet(final String profileUsername, final String profileInstance, final int relationTypeFlag, final int relationPageNumberInt) {
        super();
        profileHandle = new Handle(profileUsername, profileInstance);
        relationType = relationTypeFlag;
        relationPageNumber = relationPageNumberInt;
    }

    public RelationSet(final RelationSet baseRelationSet, final Handle profileHandleObj, final int relationTypeFlag,
                       final int relationPageNumberInt) {
        super(baseRelationSet);
        profileHandle = profileHandleObj;
        relationType = relationTypeFlag;
        relationPageNumber = relationPageNumberInt;
    }

    public RelationSet(final RelationSet baseRelationSet, final String profileUsername, final String profileInstance, final int relationTypeFlag,
                       final int relationPageNumberInt) {
        super(baseRelationSet);
        profileHandle = new Handle(profileUsername, profileInstance);
        relationType = relationTypeFlag;
        relationPageNumber = relationPageNumberInt;
    }

    public RelationSet clone() {
        RelationSet newRelationSetObj;
        Iterator setIterator;

        newRelationSetObj = new RelationSet(profileHandle, relationType, relationPageNumber);

        setIterator = iterator();

        while (setIterator.hasNext()) {
            newRelationSetObj.add((Relation) setIterator.next());
        }

        return newRelationSetObj;
    }
}
