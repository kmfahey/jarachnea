package jarachnea.junit;

import java.util.*;

import junit.framework.*;

import jarachnea.*;

public class TestRelationSet extends TestCase {
    String profileUsername = "Gargron";
    String profileInstance = "mastodon.social";
    Handle profileHandle = new Handle(profileUsername, profileInstance);
    int followersRelationType = Relation.IS_FOLLOWER_OF;
    int followersPageNumber = 1;
    Relation[] followersArray = {new Relation(profileHandle, new Handle("conr", "notacult.social"), followersRelationType, followersPageNumber),
                                 new Relation(profileHandle, new Handle("UnderscoreTalk", "mastodon.social"), followersRelationType, followersPageNumber),
                                 new Relation(profileHandle, new Handle("senor_massage", "mastodon.social"), followersRelationType, followersPageNumber),
                                 new Relation(profileHandle, new Handle("zachwood", "mastodon.social"), followersRelationType, followersPageNumber),
                                 new Relation(profileHandle, new Handle("Muzaffaralam", "c.im"), followersRelationType, followersPageNumber),
                                 new Relation(profileHandle, new Handle("stoom", "mastodon.social"), followersRelationType, followersPageNumber),
                                 new Relation(profileHandle, new Handle("JanaJaja1002", "mastodon.social"), followersRelationType, followersPageNumber),
                                 new Relation(profileHandle, new Handle("Lilalaunebaer", "sueden.social"), followersRelationType, followersPageNumber),
                                 new Relation(profileHandle, new Handle("airisdamon", "mastodon.social"), followersRelationType, followersPageNumber),
                                 new Relation(profileHandle, new Handle("Rahul355", "mastodon.social"), followersRelationType, followersPageNumber),
                                 new Relation(profileHandle, new Handle("kesch", "mastodon.social"), followersRelationType, followersPageNumber),
                                 new Relation(profileHandle, new Handle("vassie", "mastodon.social"), followersRelationType, followersPageNumber)};
    int followingRelationType = Relation.IS_FOLLOWED_BY;
    int followingPageNumber = 1;
    Relation[] followingArray = {new Relation(profileHandle, new Handle("ashfurrow", "masto.ashfurrow.com"), followingRelationType, followingPageNumber),
                                 new Relation(profileHandle, new Handle("danarel", "fosstodon.org"), followingRelationType, followingPageNumber),
                                 new Relation(profileHandle, new Handle("taviso", "mastodon.sdf.org"), followingRelationType, followingPageNumber),
                                 new Relation(profileHandle, new Handle("ThomasWaldmann", "chaos.social"), followingRelationType, followingPageNumber),
                                 new Relation(profileHandle, new Handle("akryum", "mastodon.social"), followingRelationType, followingPageNumber),
                                 new Relation(profileHandle, new Handle("vivaldibrowser", "mastodon.online"), followingRelationType, followingPageNumber),
                                 new Relation(profileHandle, new Handle("NotFrauKadse", "mastodon.social"), followingRelationType, followingPageNumber),
                                 new Relation(profileHandle, new Handle("muffinista", "mastodon.lol"), followingRelationType, followingPageNumber),
                                 new Relation(profileHandle, new Handle("camilabrun", "mastodon.art"), followingRelationType, followingPageNumber),
                                 new Relation(profileHandle, new Handle("jonathan", "charade.social"), followingRelationType, followingPageNumber),
                                 new Relation(profileHandle, new Handle("a2_4am", "mastodon.social"), followingRelationType, followingPageNumber),
                                 new Relation(profileHandle, new Handle("_astronoMay", "mastodon.online"), followingRelationType, followingPageNumber)};

    public void testRelationSetHandleConstructor() {
        RelationSet relationSetObj;

        relationSetObj = new RelationSet(profileHandle, followersRelationType, followersPageNumber);

        assertEquals(relationSetObj.profileHandle.username, profileHandle.username);
        assertEquals(relationSetObj.profileHandle.instance, profileHandle.instance);
    }

    public void testRelationSetUsernamePlusHandleConstructor() {
        RelationSet relationSetObj;

        relationSetObj = new RelationSet(profileUsername, profileInstance, followersRelationType, followersPageNumber);

        assertEquals(relationSetObj.profileHandle.username, profileHandle.username);
        assertEquals(relationSetObj.profileHandle.instance, profileHandle.instance);
    }

    public void testAddRelationToSetAgainstSizeOfSetAndClearSet() {
        RelationSet relationSetObj;

        relationSetObj = new RelationSet(profileHandle, followersRelationType, followersPageNumber);

        assertEquals(relationSetObj.sizeOfSet(), 0);

        for (int arrayIndex = 0; arrayIndex < 12; arrayIndex++) {
            relationSetObj.addToSet(followersArray[arrayIndex]);
        }

        assertEquals(relationSetObj.sizeOfSet(), 12);
        relationSetObj.clearSet();
        assertEquals(relationSetObj.sizeOfSet(), 0);
    }

    public void testRemoveFromSetAgainstIteratorAndContainsAndIsEmpty() {
        RelationSet relationSetObj;

        relationSetObj = new RelationSet(profileHandle, followersRelationType, followersPageNumber);

        assertTrue(relationSetObj.setIsEmpty());

        for (int arrayIndex = 0; arrayIndex < 12; arrayIndex++) {
            relationSetObj.addToSet(followersArray[arrayIndex]);
        }

        Iterator<Relation> relationIterator = relationSetObj.setIterator();

        while (relationIterator.hasNext()) {
            Relation relationObj = relationIterator.next();
            assertTrue(relationSetObj.setContains(relationObj));
        }

        for (int arrayIndex = 0; arrayIndex < 12; arrayIndex++) {
            relationSetObj.removeFromSet(followersArray[arrayIndex]);
        }

        assertTrue(relationSetObj.setIsEmpty());
    }
}
