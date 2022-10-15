package jarachnea.junit;

import junit.framework.*;

import jarachnea.*;

public class TestRelation extends TestCase {
    String profileInstance = "mastodon.social";
    String profileUsername = "Gargron";
    Handle profileHandle = new Handle(profileUsername, profileInstance);
    String relationInstance = "mastodon.social";
    String relationUsername = "mastodon";
    Handle relationHandle = new Handle(relationUsername, relationInstance);
    int relationType = Relation.IS_FOLLOWER_OF;
    int relationPageNumber = 1;

    public void testRelationHandleAndHandleConstructor() {
        Relation relationObj = new Relation(profileHandle, relationHandle, relationType, relationPageNumber);

        assertEquals(relationObj.profileHandle.username, profileHandle.username);
        assertEquals(relationObj.profileHandle.instance, profileHandle.instance);
        assertEquals(relationObj.relationHandle.username, relationHandle.username);
        assertEquals(relationObj.relationHandle.instance, relationHandle.instance);
        assertEquals(relationObj.relationType, relationType);
        assertEquals(relationObj.relationPageNumber, relationPageNumber);
    }

    public void testRelationHandleAndUsernamePlusInstanceConstructor() {
        Relation relationObj = new Relation(profileHandle, relationUsername, relationInstance, relationType, relationPageNumber);

        assertEquals(relationObj.profileHandle.username, profileHandle.username);
        assertEquals(relationObj.profileHandle.instance, profileHandle.instance);
        assertEquals(relationObj.relationHandle.username, relationHandle.username);
        assertEquals(relationObj.relationHandle.instance, relationHandle.instance);
        assertEquals(relationObj.relationType, relationType);
        assertEquals(relationObj.relationPageNumber, relationPageNumber);
    }

    public void testRelationUsernamePlusInstanceAndHandleConstructor() {
        Relation relationObj = new Relation(profileUsername, profileInstance, relationHandle, relationType, relationPageNumber);

        assertEquals(relationObj.profileHandle.username, profileHandle.username);
        assertEquals(relationObj.profileHandle.instance, profileHandle.instance);
        assertEquals(relationObj.relationHandle.username, relationHandle.username);
        assertEquals(relationObj.relationHandle.instance, relationHandle.instance);
        assertEquals(relationObj.relationType, relationType);
        assertEquals(relationObj.relationPageNumber, relationPageNumber);
    }

    public void testRelationUsernamePlusInstanceAndUsernamePlusInstanceConstructor() {
        Relation relationObj = new Relation(profileUsername, profileInstance, relationUsername, relationInstance, relationType, relationPageNumber);

        assertEquals(relationObj.profileHandle.username, profileHandle.username);
        assertEquals(relationObj.profileHandle.instance, profileHandle.instance);
        assertEquals(relationObj.relationHandle.username, relationHandle.username);
        assertEquals(relationObj.relationHandle.instance, relationHandle.instance);
        assertEquals(relationObj.relationType, relationType);
        assertEquals(relationObj.relationPageNumber, relationPageNumber);
    }

}
