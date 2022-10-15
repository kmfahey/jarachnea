package jarachnea.junit;

import java.net.*;
import java.text.*;

import junit.framework.*;

import jarachnea.*;

public class TestProfile extends TestCase {
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

    String instance = "mastodon.social";
    String username = "Gargron";
    String profileUrlString = "https://mastodon.social/@Gargron";

    public void testProfileConstructorWithHandle() {
        Handle handleObj;
        Profile profileObj;
        URL urlObj;

        handleObj = new Handle(username, instance);

        try {
            urlObj = new URL(profileUrlString);
            profileObj = new Profile(handleObj);
        } catch (MalformedURLException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        if (! (profileObj == null)) {
            assertEquals(profileObj.profileURL, urlObj);
        }
    }

    public void testProfileConstructorWithURL() {
        URL URLObj;
        Profile profileObj;
        Handle handleObj = new Handle(username, instance);

        try {
            URLObj = new URL(profileUrlString);
            profileObj = new Profile(URLObj);
        } catch (MalformedURLException | ParseException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        if (! (profileObj == null)) {
            assertEquals(profileObj.profileHandle.toHandle(), handleObj.toHandle());
        }
    }

    public void testProfileGenerateProfileURL() {
        Handle handleObj;
        Profile profileObj;
        URL profileURLObj;

        handleObj = new Handle(username, instance);
        try {
            profileObj = new Profile(handleObj);
        } catch (MalformedURLException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }
        profileURLObj = profileObj.getProfileURL();

        assertEquals(profileURLObj.toString(), profileUrlString);
    }

    public void testAddRelationSet() {
        RelationSet followersRelationSetObj;
        RelationSet followingRelationSetObj;
        Handle handleObj;
        Profile profileObj;
        URL urlObj;

        handleObj = new Handle(profileUsername, profileInstance);

        try {
            urlObj = new URL(profileUrlString);
            profileObj = new Profile(handleObj);
        } catch (MalformedURLException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        followersRelationSetObj = new RelationSet(profileHandle, followersRelationType, followersPageNumber);

        for (int arrayIndex = 0; arrayIndex < 12; arrayIndex++) {
            followersRelationSetObj.addToSet(followersArray[arrayIndex]);
        }

        profileObj.addRelationSet(followersRelationSetObj);

        assertTrue(profileObj.followersMap.get(followersPageNumber) == followersRelationSetObj);

        followingRelationSetObj = new RelationSet(profileHandle, followingRelationType, followingPageNumber);

        for (int arrayIndex = 0; arrayIndex < 12; arrayIndex++) {
            followingRelationSetObj.addToSet(followingArray[arrayIndex]);
        }

        profileObj.addRelationSet(followingRelationSetObj);

        assertTrue(profileObj.followingMap.get(followingPageNumber) == followingRelationSetObj);
    }
}
