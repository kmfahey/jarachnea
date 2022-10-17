package jarachnea.junit;

import java.net.URL;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.ArrayList;

import junit.framework.TestCase;

import jarachnea.Handle;
import jarachnea.Profile;
import jarachnea.Relation;
import jarachnea.RelationSet;


public final class TestProfile extends TestCase {
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

    private String instance = "mastodon.social";
    private String username = "Gargron";
    private String profileUrlString = "https://mastodon.social/@Gargron";

    public void testProfileConstructorWithHandleAndMaps() throws MalformedURLException {
        ArrayList<HashMap<Integer, RelationSet>> relationsMapList;
        Relation[][] relationsArrays = {followingArray, followersArray};
        Handle handleObj;
        Profile profileObj;
        URL urlObj;

        handleObj = new Handle(username, instance);

        urlObj = new URL(profileUrlString);
        profileObj = new Profile(handleObj, followingArray, followersArray);

        relationsMapList = new ArrayList<HashMap<Integer, RelationSet>>();
        relationsMapList.add(profileObj.getFollowingMap());
        relationsMapList.add(profileObj.getFollowersMap());

        assertEquals(profileObj.getProfileURL(), urlObj);
        assertEquals(profileObj.getProfileConsidered(), false);
        assertEquals(profileObj.getProfileSnippet(), "");
        assertEquals(profileObj.getFollowersMap().size(), 1);
        assertEquals(profileObj.getFollowingMap().size(), 1);

        for (int outerIndex = 0; outerIndex < relationsArrays.length; outerIndex++) {
            HashMap<Integer, RelationSet> relationsMap;

            relationsMap = relationsMapList.get(outerIndex);

            for (int innerIndex = 0; innerIndex < relationsArrays[outerIndex].length; innerIndex++) {
                Relation relationObj;

                relationObj = relationsArrays[outerIndex][innerIndex];

                assertTrue(relationsMap.get(relationObj.getRelationPageNumber()).contains(relationObj));
            }
        }
    }

    public void testProfileConstructorWithHandle() throws MalformedURLException {
        Handle handleObj;
        Profile profileObj;
        URL urlObj;

        handleObj = new Handle(username, instance);

        urlObj = new URL(profileUrlString);
        profileObj = new Profile(handleObj);

        assertEquals(profileObj.getProfileURL(), urlObj);
        assertEquals(profileObj.getProfileConsidered(), false);
        assertEquals(profileObj.getProfileSnippet(), "");
        assertEquals(profileObj.getFollowersMap().size(), 0);
        assertEquals(profileObj.getFollowingMap().size(), 0);
    }

    public void testProfileConstructorWithURLAndMaps() throws MalformedURLException, ParseException {
        ArrayList<HashMap<Integer, RelationSet>> relationsMapList;
        Relation[][] relationsArrays = {followingArray, followersArray};
        URL urlObj;
        Profile profileObj;
        Handle handleObj = new Handle(username, instance);

        urlObj = new URL(profileUrlString);
        profileObj = new Profile(urlObj, followingArray, followersArray);

        relationsMapList = new ArrayList<HashMap<Integer, RelationSet>>();
        relationsMapList.add(profileObj.getFollowingMap());
        relationsMapList.add(profileObj.getFollowersMap());

        assertEquals(profileObj.getProfileHandle().toHandle(), handleObj.toHandle());
        assertEquals(profileObj.getProfileConsidered(), false);
        assertEquals(profileObj.getProfileSnippet(), "");
        assertEquals(profileObj.getFollowersMap().size(), 1);
        assertEquals(profileObj.getFollowingMap().size(), 1);

        for (int outerIndex = 0; outerIndex < relationsArrays.length; outerIndex++) {
            HashMap<Integer, RelationSet> relationsMap;

            relationsMap = relationsMapList.get(outerIndex);

            for (int innerIndex = 0; innerIndex < relationsArrays[outerIndex].length; innerIndex++) {
                Relation relationObj;

                relationObj = relationsArrays[outerIndex][innerIndex];

                assertTrue(relationsMap.get(relationObj.getRelationPageNumber()).contains(relationObj));
            }
        }
    }

    public void testProfileConstructorWithURL() throws MalformedURLException, ParseException {
        URL urlObj;
        Profile profileObj;
        Handle handleObj = new Handle(username, instance);

        urlObj = new URL(profileUrlString);
        profileObj = new Profile(urlObj);

        assertEquals(profileObj.getProfileHandle().toHandle(), handleObj.toHandle());
        assertEquals(profileObj.getProfileConsidered(), false);
        assertEquals(profileObj.getProfileSnippet(), "");
        assertEquals(profileObj.getFollowersMap().size(), 0);
        assertEquals(profileObj.getFollowingMap().size(), 0);
    }

    public void testProfileConstructorWithHandleConsideredAndSnippetAndMaps() throws MalformedURLException {
        ArrayList<HashMap<Integer, RelationSet>> relationsMapList;
        Relation[][] relationsArrays = {followingArray, followersArray};
        Profile profileObj;
        Handle handleObj;
        boolean consideredBoolean;
        String snippetString;

        handleObj = new Handle(1, "Gargron", "mastodon.social");
        consideredBoolean = true;
        snippetString = "Patreon\n\n     [ https://www.  patreon.com/mastodon  ](https://www.patreon.com/mastodon)\n\nOwner\n\nFounder, CEO"
                        + "and lead developer  [ @  Mastodon\n](https://mastodon.social/@Mastodon) , Germany.\n\nJoined Mar 2016\n";

        profileObj = new Profile(handleObj, consideredBoolean, snippetString, followingArray, followersArray);

        relationsMapList = new ArrayList<HashMap<Integer, RelationSet>>();
        relationsMapList.add(profileObj.getFollowingMap());
        relationsMapList.add(profileObj.getFollowersMap());

        assertEquals(profileObj.getProfileHandle().getHandleId(), 1);
        assertEquals(profileObj.getProfileHandle().getUsername(), handleObj.getUsername());
        assertEquals(profileObj.getProfileHandle().getInstance(), handleObj.getInstance());
        assertEquals(profileObj.getProfileConsidered(), consideredBoolean);
        assertEquals(profileObj.getProfileSnippet(), snippetString);

        for (int outerIndex = 0; outerIndex < relationsArrays.length; outerIndex++) {
            HashMap<Integer, RelationSet> relationsMap;

            relationsMap = relationsMapList.get(outerIndex);

            for (int innerIndex = 0; innerIndex < relationsArrays[outerIndex].length; innerIndex++) {
                Relation relationObj;

                relationObj = relationsArrays[outerIndex][innerIndex];

                assertTrue(relationsMap.get(relationObj.getRelationPageNumber()).contains(relationObj));
            }
        }
    }

    public void testProfileConstructorWithHandleConsideredAndSnippet() throws MalformedURLException {
        Profile profileObj;
        Handle handleObj;
        boolean consideredBoolean;
        String snippetString;

        handleObj = new Handle(1, "Gargron", "mastodon.social");
        consideredBoolean = true;
        snippetString = "Patreon\n\n     [ https://www.  patreon.com/mastodon  ](https://www.patreon.com/mastodon)\n\nOwner\n\nFounder, CEO"
                        + "and lead developer  [ @  Mastodon\n](https://mastodon.social/@Mastodon) , Germany.\n\nJoined Mar 2016\n";

        profileObj = new Profile(handleObj, consideredBoolean, snippetString);

        assertEquals(profileObj.getProfileHandle().getHandleId(), 1);
        assertEquals(profileObj.getProfileHandle().getUsername(), handleObj.getUsername());
        assertEquals(profileObj.getProfileHandle().getInstance(), handleObj.getInstance());
        assertEquals(profileObj.getProfileConsidered(), consideredBoolean);
        assertEquals(profileObj.getProfileSnippet(), snippetString);
    }

    public void testProfileConstructorWithUsernameInstanceConsideredAndSnippetAndMaps() throws MalformedURLException {
        ArrayList<HashMap<Integer, RelationSet>> relationsMapList;
        Relation[][] relationsArrays = {followingArray, followersArray};
        Profile profileObj;
        String usernameString;
        String instanceString;
        String snippetString;
        boolean consideredBoolean;

        usernameString = "Gargron";
        instanceString = "mastodon.social";
        consideredBoolean = true;
        snippetString = "Patreon\n\n     [ https://www.  patreon.com/mastodon  ](https://www.patreon.com/mastodon)\n\nOwner\n\nFounder, CEO"
                        + "and lead developer  [ @  Mastodon\n](https://mastodon.social/@Mastodon) , Germany.\n\nJoined Mar 2016\n";

        profileObj = new Profile(usernameString, instanceString, consideredBoolean, snippetString, followingArray, followersArray);

        relationsMapList = new ArrayList<HashMap<Integer, RelationSet>>();
        relationsMapList.add(profileObj.getFollowingMap());
        relationsMapList.add(profileObj.getFollowersMap());

        assertEquals(profileObj.getProfileHandle().getHandleId(), -1);
        assertEquals(profileObj.getProfileHandle().getUsername(), usernameString);
        assertEquals(profileObj.getProfileHandle().getInstance(), instanceString);
        assertEquals(profileObj.getProfileConsidered(), consideredBoolean);
        assertEquals(profileObj.getProfileSnippet(), snippetString);

        for (int outerIndex = 0; outerIndex < relationsArrays.length; outerIndex++) {
            HashMap<Integer, RelationSet> relationsMap;

            relationsMap = relationsMapList.get(outerIndex);

            for (int innerIndex = 0; innerIndex < relationsArrays[outerIndex].length; innerIndex++) {
                Relation relationObj;

                relationObj = relationsArrays[outerIndex][innerIndex];

                assertTrue(relationsMap.get(relationObj.getRelationPageNumber()).contains(relationObj));
            }
        }
    }

    public void testProfileConstructorWithUsernameInstanceConsideredAndSnippet() throws MalformedURLException {
        Profile profileObj;
        String usernameString;
        String instanceString;
        String snippetString;
        boolean consideredBoolean;

        usernameString = "Gargron";
        instanceString = "mastodon.social";
        consideredBoolean = true;
        snippetString = "Patreon\n\n     [ https://www.  patreon.com/mastodon  ](https://www.patreon.com/mastodon)\n\nOwner\n\nFounder, CEO"
                        + "and lead developer  [ @  Mastodon\n](https://mastodon.social/@Mastodon) , Germany.\n\nJoined Mar 2016\n";

        profileObj = new Profile(usernameString, instanceString, consideredBoolean, snippetString);

        assertEquals(profileObj.getProfileHandle().getHandleId(), -1);
        assertEquals(profileObj.getProfileHandle().getUsername(), usernameString);
        assertEquals(profileObj.getProfileHandle().getInstance(), instanceString);
        assertEquals(profileObj.getProfileConsidered(), consideredBoolean);
        assertEquals(profileObj.getProfileSnippet(), snippetString);
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

        for (int arrayIndex = 0; arrayIndex < followersArray.length; arrayIndex++) {
            followersRelationSetObj.add(followersArray[arrayIndex]);
        }

        profileObj.addRelationSet(followersRelationSetObj);

        assertTrue(profileObj.getFollowersMap().get(followersPageNumber) == followersRelationSetObj);

        followingRelationSetObj = new RelationSet(profileHandle, followingRelationType, followingPageNumber);

        for (int arrayIndex = 0; arrayIndex < followersArray.length; arrayIndex++) {
            followingRelationSetObj.add(followingArray[arrayIndex]);
        }

        profileObj.addRelationSet(followingRelationSetObj);

        assertTrue(profileObj.getFollowingMap().get(followingPageNumber) == followingRelationSetObj);
    }
}
