package jarachnea.junit;

import java.util.Iterator;

import junit.framework.TestCase;

import jarachnea.Handle;
import jarachnea.Relation;
import jarachnea.RelationSet;


public final class TestRelationSet extends TestCase {
    private String profileUsername = "Gargron";
    private String profileInstance = "mastodon.social";
    private Handle profileHandle = new Handle(profileUsername, profileInstance);
    private int followersRelationType = Relation.IS_FOLLOWER_OF;
    private int followersPageNumber = 1;
    private Relation[] followersArray = {new Relation(
                                             profileHandle, new Handle("conr", "notacult.social"),
                                             followersRelationType, followersPageNumber),
                                 new Relation(
                                     profileHandle, new Handle("UnderscoreTalk", "mastodon.social"), followersRelationType, followersPageNumber),
                                 new Relation(
                                     profileHandle, new Handle("senor_massage", "mastodon.social"), followersRelationType, followersPageNumber),
                                 new Relation(
                                     profileHandle, new Handle("zachwood", "mastodon.social"), followersRelationType, followersPageNumber),
                                 new Relation(
                                     profileHandle, new Handle("Muzaffaralam", "c.im"), followersRelationType, followersPageNumber),
                                 new Relation(
                                     profileHandle, new Handle("stoom", "mastodon.social"), followersRelationType, followersPageNumber),
                                 new Relation(
                                     profileHandle, new Handle("JanaJaja1002", "mastodon.social"), followersRelationType, followersPageNumber),
                                 new Relation(
                                     profileHandle, new Handle("Lilalaunebaer", "sueden.social"), followersRelationType, followersPageNumber),
                                 new Relation(
                                     profileHandle, new Handle("airisdamon", "mastodon.social"), followersRelationType, followersPageNumber),
                                 new Relation(
                                     profileHandle, new Handle("Rahul355", "mastodon.social"), followersRelationType, followersPageNumber),
                                 new Relation(
                                     profileHandle, new Handle("kesch", "mastodon.social"), followersRelationType, followersPageNumber),
                                 new Relation(
                                     profileHandle, new Handle("vassie", "mastodon.social"), followersRelationType, followersPageNumber)};
    private int followingRelationType = Relation.IS_FOLLOWED_BY;
    private int followingPageNumber = 1;
    private Relation[] followingArray = {new Relation(
                                             profileHandle, new Handle("ashfurrow", "masto.ashfurrow.com"),
                                             followingRelationType, followingPageNumber),
                                 new Relation(
                                     profileHandle, new Handle("danarel", "fosstodon.org"), followingRelationType, followingPageNumber),
                                 new Relation(
                                     profileHandle, new Handle("taviso", "mastodon.sdf.org"), followingRelationType, followingPageNumber),
                                 new Relation(
                                     profileHandle, new Handle("ThomasWaldmann", "chaos.social"), followingRelationType, followingPageNumber),
                                 new Relation(
                                     profileHandle, new Handle("akryum", "mastodon.social"), followingRelationType, followingPageNumber),
                                 new Relation(
                                     profileHandle, new Handle("vivaldibrowser", "mastodon.online"), followingRelationType, followingPageNumber),
                                 new Relation(
                                     profileHandle, new Handle("NotFrauKadse", "mastodon.social"), followingRelationType, followingPageNumber),
                                 new Relation(
                                     profileHandle, new Handle("muffinista", "mastodon.lol"), followingRelationType, followingPageNumber),
                                 new Relation(
                                     profileHandle, new Handle("camilabrun", "mastodon.art"), followingRelationType, followingPageNumber),
                                 new Relation(
                                     profileHandle, new Handle("jonathan", "charade.social"), followingRelationType, followingPageNumber),
                                 new Relation(
                                     profileHandle, new Handle("a2_4am", "mastodon.social"), followingRelationType, followingPageNumber),
                                 new Relation(
                                     profileHandle, new Handle("_astronoMay", "mastodon.online"), followingRelationType, followingPageNumber)};

    public void testRelationSetHandleConstructor() {
        RelationSet relationSetObj;

        relationSetObj = new RelationSet(profileHandle, followersRelationType, followersPageNumber);

        assertEquals(relationSetObj.getProfileHandle().getUsername(), profileHandle.getUsername());
        assertEquals(relationSetObj.getProfileHandle().getInstance(), profileHandle.getInstance());
        assertEquals(relationSetObj.getRelationType(), followersRelationType);
        assertEquals(relationSetObj.getRelationPageNumber(), followersPageNumber);
    }

    public void testRelationSetUsernamePlusHandleConstructor() {
        RelationSet relationSetObj;

        relationSetObj = new RelationSet(profileUsername, profileInstance, followersRelationType, followersPageNumber);

        assertEquals(relationSetObj.getProfileHandle().getUsername(), profileHandle.getUsername());
        assertEquals(relationSetObj.getProfileHandle().getInstance(), profileHandle.getInstance());
        assertEquals(relationSetObj.getRelationType(), followersRelationType);
        assertEquals(relationSetObj.getRelationPageNumber(), followersPageNumber);
    }

    public void testRelationSetInitializingSetAndUsernamePlusHandleConstructor() {
        RelationSet firstRelationSetObj;
        RelationSet secondRelationSetObj;

        firstRelationSetObj = new RelationSet(profileUsername, profileInstance, followersRelationType, followersPageNumber);

        for (int arrayIndex = 0; arrayIndex < followersArray.length; arrayIndex++) {
            firstRelationSetObj.add(followersArray[arrayIndex]);
        }

        secondRelationSetObj = new RelationSet(firstRelationSetObj, profileUsername, profileInstance, followersRelationType, followersPageNumber);

        assertEquals(secondRelationSetObj.getProfileHandle().getUsername(), profileHandle.getUsername());
        assertEquals(secondRelationSetObj.getProfileHandle().getInstance(), profileHandle.getInstance());
        assertEquals(secondRelationSetObj.getRelationType(), followersRelationType);
        assertEquals(secondRelationSetObj.getRelationPageNumber(), followersPageNumber);

        for (int arrayIndex = 0; arrayIndex < followersArray.length; arrayIndex++) {
            assertTrue(secondRelationSetObj.contains(followersArray[arrayIndex]));
        }
    }

    public void testRelationSetIntiailizingSetAndUsernamePlusHandleConstructor() {
        RelationSet firstRelationSetObj;
        RelationSet secondRelationSetObj;

        firstRelationSetObj = new RelationSet(profileUsername, profileInstance, followersRelationType, followersPageNumber);

        for (int arrayIndex = 0; arrayIndex < followersArray.length; arrayIndex++) {
            firstRelationSetObj.add(followersArray[arrayIndex]);
        }

        secondRelationSetObj = new RelationSet(firstRelationSetObj, profileUsername, profileInstance, followersRelationType, followersPageNumber);

        assertEquals(secondRelationSetObj.getProfileHandle().getUsername(), profileHandle.getUsername());
        assertEquals(secondRelationSetObj.getProfileHandle().getInstance(), profileHandle.getInstance());
        assertEquals(secondRelationSetObj.getRelationType(), followersRelationType);
        assertEquals(secondRelationSetObj.getRelationPageNumber(), followersPageNumber);

        for (int arrayIndex = 0; arrayIndex < followersArray.length; arrayIndex++) {
            assertTrue(secondRelationSetObj.contains(followersArray[arrayIndex]));
        }
    }

    public void testAddRelationToSetAgainstSizeOfSetAndClearSet() {
        RelationSet relationSetObj;

        relationSetObj = new RelationSet(profileHandle, followersRelationType, followersPageNumber);

        assertEquals(relationSetObj.size(), 0);

        for (int arrayIndex = 0; arrayIndex < followersArray.length; arrayIndex++) {
            relationSetObj.add(followersArray[arrayIndex]);
        }

        assertEquals(relationSetObj.size(), followersArray.length);
        relationSetObj.clear();
        assertEquals(relationSetObj.size(), 0);
    }

    public void testRemoveFromSetAgainstIteratorAndContainsAndIsEmpty() {
        RelationSet relationSetObj;

        relationSetObj = new RelationSet(profileHandle, followersRelationType, followersPageNumber);

        assertTrue(relationSetObj.isEmpty());

        for (int arrayIndex = 0; arrayIndex < followersArray.length; arrayIndex++) {
            relationSetObj.add(followersArray[arrayIndex]);
        }

        Iterator<Relation> relationIterator = relationSetObj.iterator();

        while (relationIterator.hasNext()) {
            Relation relationObj = relationIterator.next();
            assertTrue(relationSetObj.contains(relationObj));
        }

        for (int arrayIndex = 0; arrayIndex < followersArray.length; arrayIndex++) {
            relationSetObj.remove(followersArray[arrayIndex]);
        }

        assertTrue(relationSetObj.isEmpty());
    }

    public void testCloneAgainstAddAndSize() {
        RelationSet firstRelationSetObj;
        RelationSet secondRelationSetObj;

        firstRelationSetObj = new RelationSet(profileHandle, followersRelationType, followersPageNumber);

        for (int arrayIndex = 0; arrayIndex < followersArray.length; arrayIndex++) {
            firstRelationSetObj.add(followersArray[arrayIndex]);
        }

        secondRelationSetObj = firstRelationSetObj.clone();

        assertEquals(secondRelationSetObj.getProfileHandle().getUsername(), profileHandle.getUsername());
        assertEquals(secondRelationSetObj.getProfileHandle().getInstance(), profileHandle.getInstance());
        assertEquals(secondRelationSetObj.getRelationType(), followersRelationType);
        assertEquals(secondRelationSetObj.getRelationPageNumber(), followersPageNumber);

        for (int arrayIndex = 0; arrayIndex < followersArray.length; arrayIndex++) {
            assertTrue(secondRelationSetObj.contains(followersArray[arrayIndex]));
        }
    }
}
