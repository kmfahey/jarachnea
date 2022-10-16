package jarachnea.junit;

import junit.framework.TestCase;

import jarachnea.Handle;
import jarachnea.Relation;


public final class TestRelation extends TestCase {
    private String profileInstance = "mastodon.social";
    private String profileUsername = "Gargron";
    private Handle profileHandle = new Handle(profileUsername, profileInstance);
    private String relationInstance = "mastodon.social";
    private String relationUsername = "mastodon";
    private Handle relationHandle = new Handle(relationUsername, relationInstance);
    private int relationType = Relation.IS_FOLLOWER_OF;
    private int relationPageNumber = 1;

    public void testRelationHandleAndHandleConstructor() {
        Relation relationObj = new Relation(profileHandle, relationHandle, relationType, relationPageNumber);

        assertEquals(relationObj.getProfileHandle().getUsername(), profileHandle.getUsername());
        assertEquals(relationObj.getProfileHandle().getInstance(), profileHandle.getInstance());
        assertEquals(relationObj.getRelationHandle().getUsername(), relationHandle.getUsername());
        assertEquals(relationObj.getRelationHandle().getInstance(), relationHandle.getInstance());
        assertEquals(relationObj.getRelationType(), relationType);
        assertEquals(relationObj.getRelationPageNumber(), relationPageNumber);
    }

    public void testRelationHandleAndUsernamePlusInstanceConstructor() {
        Relation relationObj = new Relation(profileHandle, relationUsername, relationInstance, relationType, relationPageNumber);

        assertEquals(relationObj.getProfileHandle().getUsername(), profileHandle.getUsername());
        assertEquals(relationObj.getProfileHandle().getInstance(), profileHandle.getInstance());
        assertEquals(relationObj.getRelationHandle().getUsername(), relationHandle.getUsername());
        assertEquals(relationObj.getRelationHandle().getInstance(), relationHandle.getInstance());
        assertEquals(relationObj.getRelationType(), relationType);
        assertEquals(relationObj.getRelationPageNumber(), relationPageNumber);
    }

    public void testRelationUsernamePlusInstanceAndHandleConstructor() {
        Relation relationObj = new Relation(profileUsername, profileInstance, relationHandle, relationType, relationPageNumber);

        assertEquals(relationObj.getProfileHandle().getUsername(), profileHandle.getUsername());
        assertEquals(relationObj.getProfileHandle().getInstance(), profileHandle.getInstance());
        assertEquals(relationObj.getRelationHandle().getUsername(), relationHandle.getUsername());
        assertEquals(relationObj.getRelationHandle().getInstance(), relationHandle.getInstance());
        assertEquals(relationObj.getRelationType(), relationType);
        assertEquals(relationObj.getRelationPageNumber(), relationPageNumber);
    }

    public void testRelationUsernamePlusInstanceAndUsernamePlusInstanceConstructor() {
        Relation relationObj = new Relation(profileUsername, profileInstance, relationUsername, relationInstance, relationType, relationPageNumber);

        assertEquals(relationObj.getProfileHandle().getUsername(), profileHandle.getUsername());
        assertEquals(relationObj.getProfileHandle().getInstance(), profileHandle.getInstance());
        assertEquals(relationObj.getRelationHandle().getUsername(), relationHandle.getUsername());
        assertEquals(relationObj.getRelationHandle().getInstance(), relationHandle.getInstance());
        assertEquals(relationObj.getRelationType(), relationType);
        assertEquals(relationObj.getRelationPageNumber(), relationPageNumber);
    }

}
