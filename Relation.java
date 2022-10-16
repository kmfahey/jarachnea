package jarachnea;


public final class Relation {
    public static final Integer IS_FOLLOWED_BY = 0;
    public static final Integer IS_FOLLOWER_OF = 1;

    private Handle profileHandle;
    private Handle relationHandle;
    private int relationType;
    private int relationPageNumber;

    public Handle getProfileHandle() {
        return profileHandle;
    }

    public Handle getRelationHandle() {
        return relationHandle;
    }

    public int getRelationType() {
        return relationType;
    }

    public int getRelationPageNumber() {
        return relationPageNumber;
    }

    public Relation(final Handle profileHandleObj, final Handle relationHandleObj, final int relationTypeFlag, final int relationPageNumberInt) {
        profileHandle = profileHandleObj;
        relationHandle = relationHandleObj;
        relationType = relationTypeFlag;
        relationPageNumber = relationPageNumberInt;
    }

    public Relation(final String profileUsername, final String profileInstance, final Handle relationHandleObj, final int relationTypeFlag,
                    final int relationPageNumberInt) {
        profileHandle = new Handle(profileUsername, profileInstance);
        relationHandle = relationHandleObj;
        relationType = relationTypeFlag;
        relationPageNumber = relationPageNumberInt;
    }

    public Relation(final String profileUsername, final String profileInstance, final String relationUsername, final String relationInstance,
                    final int relationTypeFlag, final int relationPageNumberInt) {
        profileHandle = new Handle(profileUsername, profileInstance);
        relationHandle = new Handle(relationUsername, relationInstance);
        relationType = relationTypeFlag;
        relationPageNumber = relationPageNumberInt;
    }

    public Relation(final Handle profileHandleObj, final String relationUsername, final String relationInstance, final int relationTypeFlag,
                    final int relationPageNumberInt) {
        profileHandle = profileHandleObj;
        relationHandle = new Handle(relationUsername, relationInstance);
        relationType = relationTypeFlag;
        relationPageNumber = relationPageNumberInt;
    }
}
