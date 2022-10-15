package jarachnea;


public final class Relation {
    public static final Integer IS_FOLLOWED_BY = 0;
    public static final Integer IS_FOLLOWER_OF = 1;

    public Handle profileHandle;
    public Handle relationHandle;
    public int relationType;
    public int relationPageNumber;

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
