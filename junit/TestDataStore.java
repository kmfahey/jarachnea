package jarachnea.junit;

import java.net.MalformedURLException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import junit.framework.TestCase;

import org.ini4j.Ini;

import jarachnea.DataStore;
import jarachnea.Instance;
import jarachnea.ProcessingException;
import jarachnea.Handle;
import jarachnea.Relation;
import jarachnea.RelationSet;
import jarachnea.Profile;

public final class TestDataStore extends TestCase {

    private static final Instance TEST_MALFUNCTIONING_INSTANCE = new Instance("100club.social", Instance.MALFUNCTIONING);
    private static final Instance TEST_SUSPENDED_INSTANCE = new Instance("anime.website", Instance.SUSPENDED);
    private static final Instance TEST_UNPARSEABLE_INSTANCE = new Instance("56k.space", Instance.UNPARSEABLE);

    private static final Handle TEST_HANDLE_WITHOUT_HANDLE_ID = new Handle("Gargron", "mastodon.social");
    private static final Handle TEST_HANDLE_WITH_HANDLE_ID = new Handle(71790, "Gargron", "mastodon.social");

    private static final String INI_FILE_PATH = "/home/kmfahey/Workspace/jarachnea_mysql_secrets.ini";

    private int profileHandleId = 71790;
    private String profileUsername = "Gargron";
    private String profileInstance = "mastodon.social";
    private Handle profileHandle = new Handle(profileHandleId, profileUsername, profileInstance);
    private Relation[] followersArray = {new Relation(
                                             profileHandle, new Handle(4637, "batstonight", "nitecrew.rip"), Relation.IS_FOLLOWER_OF, 1),
                                         new Relation(
                                             profileHandle, new Handle(76226, "Mage4636", "mastodon.social"), Relation.IS_FOLLOWER_OF, 1),
                                         new Relation(
                                             profileHandle, new Handle(68486, "Canard691monpote", "mastodon.social"), Relation.IS_FOLLOWER_OF, 1),
                                         new Relation(
                                             profileHandle, new Handle(63589, "Machibito", "mastodon.social"), Relation.IS_FOLLOWER_OF, 1),
                                         new Relation(
                                             profileHandle, new Handle(55932, "amzin", "mastodon.social"), Relation.IS_FOLLOWER_OF, 1),
                                         new Relation(
                                             profileHandle, new Handle(53929, "Jusland", "mastodon.social"), Relation.IS_FOLLOWER_OF, 1),
                                         new Relation(
                                             profileHandle, new Handle(51154, "Lusos", "mastodon.social"), Relation.IS_FOLLOWER_OF, 1),
                                         new Relation(
                                             profileHandle, new Handle(50824, "immortal_fungi", "mastodon.social"), Relation.IS_FOLLOWER_OF, 1),
                                         new Relation(
                                             profileHandle, new Handle(49439, "ahyanzyenu", "mastodon.social"), Relation.IS_FOLLOWER_OF, 1),
                                         new Relation(
                                             profileHandle, new Handle(47336, "Xasannuuriye1", "mastodon.social"), Relation.IS_FOLLOWER_OF, 1),
                                         new Relation(
                                             profileHandle, new Handle(39032, "kray", "mastodon.online"), Relation.IS_FOLLOWER_OF, 1),
                                         new Relation(
                                             profileHandle, new Handle(37965, "ahyanzyenu", "mastodon.online"), Relation.IS_FOLLOWER_OF, 1),
                                         new Relation(
                                             profileHandle, new Handle(77403, "amelly", "mastodon.social"), Relation.IS_FOLLOWER_OF, 2),
                                         new Relation(
                                             profileHandle, new Handle(70356, "meyperopelog", "mastodon.social"), Relation.IS_FOLLOWER_OF, 2),
                                         new Relation(
                                             profileHandle, new Handle(71497, "vicky101", "mastodon.social"), Relation.IS_FOLLOWER_OF, 2),
                                         new Relation(
                                             profileHandle, new Handle(64926, "Unistar", "mastodon.social"), Relation.IS_FOLLOWER_OF, 2),
                                         new Relation(
                                             profileHandle, new Handle(64396, "JonnyFW", "mastodon.social"), Relation.IS_FOLLOWER_OF, 2),
                                         new Relation(
                                             profileHandle, new Handle(74883, "CDNAllThings", "mastodon.social"), Relation.IS_FOLLOWER_OF, 2),
                                         new Relation(
                                             profileHandle, new Handle(59214, "Stefan_S_from_H", "mastodon.social"), Relation.IS_FOLLOWER_OF, 2),
                                         new Relation(
                                             profileHandle, new Handle(54781, "keramikbruckner", "mastodon.social"), Relation.IS_FOLLOWER_OF, 2),
                                         new Relation(
                                             profileHandle, new Handle(51845, "bromitlon", "mastodon.social"), Relation.IS_FOLLOWER_OF, 2),
                                         new Relation(
                                             profileHandle, new Handle(77531, "Treasureisland3", "mastodon.social"), Relation.IS_FOLLOWER_OF, 2),
                                         new Relation(
                                             profileHandle, new Handle(19235, "liminal", "kolektiva.social"), Relation.IS_FOLLOWER_OF, 2),
                                         new Relation(
                                             profileHandle, new Handle(77941, "indiabiz", "mastodon.social"), Relation.IS_FOLLOWER_OF, 2),
                                         new Relation(
                                             profileHandle, new Handle(48971, "DaniFrantz", "mastodon.social"), Relation.IS_FOLLOWER_OF, 3),
                                         new Relation(
                                             profileHandle, new Handle(37342, "dongphucphuongthao", "mastodon.online"), Relation.IS_FOLLOWER_OF, 3),
                                         new Relation(
                                             profileHandle, new Handle(46422, "fujifilmfbsg", "mastodon.social"), Relation.IS_FOLLOWER_OF, 3),
                                         new Relation(
                                             profileHandle, new Handle(13158, "crawlchange", "c.im"), Relation.IS_FOLLOWER_OF, 3),
                                         new Relation(
                                             profileHandle, new Handle(60569, "J49", "mastodon.social"), Relation.IS_FOLLOWER_OF, 3),
                                         new Relation(
                                             profileHandle, new Handle(39710, "rikkitikkitawi", "mastodon.online"), Relation.IS_FOLLOWER_OF, 3),
                                         new Relation(
                                             profileHandle, new Handle(4638, "jojo", "nitecrew.rip"), Relation.IS_FOLLOWER_OF, 3),
                                         new Relation(
                                             profileHandle, new Handle(11773, "Timbae", "aus.social"), Relation.IS_FOLLOWER_OF, 3),
                                         new Relation(
                                             profileHandle, new Handle(96008, "tobbsn", "post.lurk.org"), Relation.IS_FOLLOWER_OF, 3),
                                         new Relation(
                                             profileHandle, new Handle(57948, "photocopysuncorp", "mastodon.social"), Relation.IS_FOLLOWER_OF, 3),
                                         new Relation(
                                             profileHandle, new Handle(12121, "josh", "xerg.ga"), Relation.IS_FOLLOWER_OF, 3)};
    private Relation[] followingArray = {new Relation(
                                             profileHandle, new Handle(39219, "_astronoMay", "mastodon.online"), Relation.IS_FOLLOWED_BY, 1),
                                         new Relation(
                                             profileHandle, new Handle(58548, "mika_", "mastodon.social"), Relation.IS_FOLLOWED_BY, 1),
                                         new Relation(
                                             profileHandle, new Handle(30234, "muffinista", "mastodon.lol"), Relation.IS_FOLLOWED_BY, 1),
                                         new Relation(
                                             profileHandle, new Handle(67304, "a2_4am", "mastodon.social"), Relation.IS_FOLLOWED_BY, 1),
                                         new Relation(
                                             profileHandle, new Handle(68604, "mconley", "mastodon.social"), Relation.IS_FOLLOWED_BY, 1),
                                         new Relation(
                                             profileHandle, new Handle(31314, "camilabrun", "mastodon.art"), Relation.IS_FOLLOWED_BY, 1),
                                         new Relation(
                                             profileHandle, new Handle(59624, "NotFrauKadse", "mastodon.social"), Relation.IS_FOLLOWED_BY, 1),
                                         new Relation(
                                             profileHandle, new Handle(93893, "stephenserjeant", "koyu.space"), Relation.IS_FOLLOWED_BY, 1),
                                         new Relation(
                                             profileHandle, new Handle(39459, "vivaldibrowser", "mastodon.online"), Relation.IS_FOLLOWED_BY, 1),
                                         new Relation(
                                             profileHandle, new Handle(46167, "akryum", "mastodon.social"), Relation.IS_FOLLOWED_BY, 1),
                                         new Relation(
                                             profileHandle, new Handle(75599, "sundogplanets", "mastodon.social"), Relation.IS_FOLLOWED_BY, 2),
                                         new Relation(
                                             profileHandle, new Handle(79780, "stefan", "det.social"), Relation.IS_FOLLOWED_BY, 2),
                                         new Relation(
                                             profileHandle, new Handle(576, "nyrath", "qoto.org"), Relation.IS_FOLLOWED_BY, 2),
                                         new Relation(
                                             profileHandle, new Handle(77358, "alexoak", "mastodon.social"), Relation.IS_FOLLOWED_BY, 2),
                                         new Relation(
                                             profileHandle, new Handle(34959, "alstev", "scholar.social"), Relation.IS_FOLLOWED_BY, 2),
                                         new Relation(
                                             profileHandle, new Handle(92410, "zdfmagazin", "edi.social"), Relation.IS_FOLLOWED_BY, 2),
                                         new Relation(
                                             profileHandle, new Handle(78621, "sskylar", "mastodon.social"), Relation.IS_FOLLOWED_BY, 2),
                                         new Relation(
                                             profileHandle, new Handle(30489, "ChrisWere", "toot.wales"), Relation.IS_FOLLOWED_BY, 2),
                                         new Relation(
                                             profileHandle, new Handle(59302, "tttylee", "mastodon.social"), Relation.IS_FOLLOWED_BY, 2),
                                         new Relation(
                                             profileHandle, new Handle(53682, "danielgamage", "mastodon.social"), Relation.IS_FOLLOWED_BY, 2),
                                         new Relation(
                                             profileHandle, new Handle(52699, "oakstudios", "mastodon.social"), Relation.IS_FOLLOWED_BY, 2),
                                         new Relation(
                                             profileHandle, new Handle(49125, "biscuitcats", "mastodon.social"), Relation.IS_FOLLOWED_BY, 2),
                                         new Relation(
                                             profileHandle, new Handle(100803, "color", "mstdn.social"), Relation.IS_FOLLOWED_BY, 3),
                                         new Relation(
                                             profileHandle, new Handle(68984, "ummjackson", "mastodon.social"), Relation.IS_FOLLOWED_BY, 3),
                                         new Relation(
                                             profileHandle, new Handle(94828, "NGIZero", "mastodon.xyz"), Relation.IS_FOLLOWED_BY, 3),
                                         new Relation(
                                             profileHandle, new Handle(39256, "AdiFoord", "mastodon.online"), Relation.IS_FOLLOWED_BY, 3),
                                         new Relation(
                                             profileHandle, new Handle(23463, "jscaux", "scipost.social"), Relation.IS_FOLLOWED_BY, 3),
                                         new Relation(
                                             profileHandle, new Handle(92409, "janboehm", "edi.social"), Relation.IS_FOLLOWED_BY, 3),
                                         new Relation(
                                             profileHandle, new Handle(48427, "whyvinca", "mastodon.social"), Relation.IS_FOLLOWED_BY, 3),
                                         new Relation(
                                             profileHandle, new Handle(48862, "Yukari", "mastodon.social"), Relation.IS_FOLLOWED_BY, 3),
                                         new Relation(
                                             profileHandle, new Handle(12455, "EU_Commission", "social.network.europa.eu"),
                                             Relation.IS_FOLLOWED_BY, 3),
                                         new Relation(
                                             profileHandle, new Handle(12442, "EDPS_supervisor", "social.network.europa.eu"),
                                             Relation.IS_FOLLOWED_BY, 3),
                                         new Relation(
                                             profileHandle, new Handle(60160, "glynmoody", "mastodon.social"), Relation.IS_FOLLOWED_BY, 3),
                                         new Relation(
                                             profileHandle, new Handle(72263, "PCMag", "mastodon.social"), Relation.IS_FOLLOWED_BY, 3)};
    private Profile profileObj;
    private HashMap<Integer, RelationSet> followingMap;
    private HashMap<Integer, RelationSet> followersMap;

    private String host;
    private String username;
    private String password;
    private String database;
    private DataStore dataStoreObj;
    private String table;

    public void setUp() throws SQLException, IOException {
        File iniFileObj;
        Ini iniObj;

        iniFileObj = new File(INI_FILE_PATH);
        iniObj = new Ini(iniFileObj);

        host = iniObj.get("Testing").get("host");
        username = iniObj.get("Testing").get("username");
        password = iniObj.get("Testing").get("password");
        database = iniObj.get("Testing").get("database");

        profileObj = new Profile(profileHandle, true, "Patreon\n\n     [ https://www.  patreon.com/mastodon  ](https://www.patreon.com/mastodon)\n\n"
                                                      + "Owner\n\nFounder, CEO and lead developer  [ @  Mastodon\n](https://mastodon.social/@Mastodo"
                                                      + "n) , Germany.\n\nJoined Mar 2016\n", followingArray, followersArray);
        followingMap = profileObj.getFollowingMap();
        followersMap = profileObj.getFollowersMap();

        dataStoreObj = new DataStore(host, username, password, database);
    }

    public void tearDown() throws SQLException {
        dataStoreObj.clearTable("bad_instances");
        dataStoreObj.clearTable("handles");
        dataStoreObj.clearTable("relations");
        dataStoreObj.clearTable("profiles");
    }

    public void testConstructor() {
        assertEquals(dataStoreObj.getHost(), host);
        assertEquals(dataStoreObj.getUsername(), username);
        assertEquals(dataStoreObj.getPassword(), password);
        assertEquals(dataStoreObj.getDatabase(), database);
    }

    public void testProfileStoreAndRetrieve() throws SQLException, MalformedURLException, ProcessingException {
        ArrayList<HashMap<Integer, RelationSet>> relationsListBefore;
        ArrayList<HashMap<Integer, RelationSet>> relationsListAfter;
        Profile profileObjAfter;

        dataStoreObj.storeProfile(profileObj);
        profileObjAfter = dataStoreObj.retrieveProfile(profileObj.getProfileHandle());

        assertEquals(profileObj.getProfileHandle().getHandleId(), profileObjAfter.getProfileHandle().getHandleId());
        assertEquals(profileObj.getProfileHandle().getUsername(), profileObjAfter.getProfileHandle().getUsername());
        assertEquals(profileObj.getProfileHandle().getInstance(), profileObjAfter.getProfileHandle().getInstance());

        relationsListBefore = new ArrayList<HashMap<Integer, RelationSet>>();
        relationsListBefore.add(profileObj.getFollowingMap());
        relationsListBefore.add(profileObj.getFollowersMap());

        relationsListAfter = new ArrayList<HashMap<Integer, RelationSet>>();
        relationsListAfter.add(profileObjAfter.getFollowingMap());
        relationsListAfter.add(profileObjAfter.getFollowersMap());

        for (int outerIndex = 0; outerIndex < relationsListBefore.size(); outerIndex++) {
            HashMap<Integer, RelationSet> relationBefore;
            HashMap<Integer, RelationSet> relationAfter;

            relationBefore = relationsListBefore.get(outerIndex);
            relationAfter = relationsListAfter.get(outerIndex);

            for (int innerIndex = 1; innerIndex <= relationBefore.size(); innerIndex++) {
                RelationSet relationSetBefore;
                RelationSet relationSetAfter;
                Iterator relationSetBeforeIter;

                relationSetBefore = relationBefore.get(innerIndex);
                relationSetAfter = relationAfter.get(innerIndex);
                relationSetBeforeIter = relationSetBefore.iterator();

                while (relationSetBeforeIter.hasNext()) {
                    Relation relationBeforeObj;
                    Iterator relationSetAfterIter;
                    HashSet<Boolean> booleanSet;

                    booleanSet = new HashSet<Boolean>();
                    relationBeforeObj = (Relation) relationSetBeforeIter.next();
                    relationSetAfterIter = relationSetAfter.iterator();

                    while (relationSetAfterIter.hasNext()) {
                        Relation relationAfterObj;

                        relationAfterObj = (Relation) relationSetAfterIter.next();
                        booleanSet.add(relationBeforeObj.getProfileHandle().getHandleId() == relationBeforeObj.getProfileHandle().getHandleId()
                                       && relationBeforeObj.getProfileHandle().getUsername().equals(relationBeforeObj.getProfileHandle().getUsername())
                                       && relationBeforeObj.getProfileHandle().getInstance().equals(relationBeforeObj.getProfileHandle().getInstance())
                                       && relationBeforeObj.getRelationHandle().getHandleId() == relationBeforeObj.getRelationHandle().getHandleId()
                                       && relationBeforeObj.getRelationHandle().getUsername().equals(relationBeforeObj.getRelationHandle().getUsername())
                                       && relationBeforeObj.getRelationHandle().getInstance().equals(relationBeforeObj.getRelationHandle().getInstance())
                                       && relationBeforeObj.getRelationType() == relationBeforeObj.getRelationType()
                                       && relationBeforeObj.getRelationPageNumber() == relationBeforeObj.getRelationPageNumber());
                    }

                    assertTrue(booleanSet.contains(true));
                }
            }
        }
    }

    public void testRelationStoreAndRetrieve() throws SQLException {
        ArrayList<HashMap<Integer, RelationSet>> relationsList;

        relationsList = new ArrayList<HashMap<Integer, RelationSet>>();
        relationsList.add(followingMap);
        relationsList.add(followersMap);

        for (int outerIndex = 0; outerIndex < relationsList.size(); outerIndex++) {
            for (int innerIndex = 1; innerIndex <= relationsList.get(outerIndex).size(); innerIndex++) {
                RelationSet relationSetBefore;
                RelationSet relationSetAfter;
                Iterator relationSetBeforeIter;

                relationSetBefore = relationsList.get(outerIndex).get(innerIndex);
                dataStoreObj.storeRelationSet(relationSetBefore);
                relationSetAfter = dataStoreObj.retrieveRelationSet(relationSetBefore.getProfileHandle(), relationSetBefore.getRelationType(), relationSetBefore.getRelationPageNumber());
                relationSetBeforeIter = relationSetBefore.iterator();

                while (relationSetBeforeIter.hasNext()) {
                    Relation relationBeforeObj;
                    Iterator relationSetAfterIter;
                    HashSet<Boolean> booleanSet;

                    booleanSet = new HashSet<Boolean>();
                    relationBeforeObj = (Relation) relationSetBeforeIter.next();
                    relationSetAfterIter = relationSetAfter.iterator();

                    while (relationSetAfterIter.hasNext()) {
                        Relation relationAfterObj;

                        relationAfterObj = (Relation) relationSetAfterIter.next();
                        booleanSet.add(relationBeforeObj.getProfileHandle().getHandleId() == relationBeforeObj.getProfileHandle().getHandleId()
                                       && relationBeforeObj.getProfileHandle().getUsername().equals(relationBeforeObj.getProfileHandle().getUsername())
                                       && relationBeforeObj.getProfileHandle().getInstance().equals(relationBeforeObj.getProfileHandle().getInstance())
                                       && relationBeforeObj.getRelationHandle().getHandleId() == relationBeforeObj.getRelationHandle().getHandleId()
                                       && relationBeforeObj.getRelationHandle().getUsername().equals(relationBeforeObj.getRelationHandle().getUsername())
                                       && relationBeforeObj.getRelationHandle().getInstance().equals(relationBeforeObj.getRelationHandle().getInstance())
                                       && relationBeforeObj.getRelationType() == relationBeforeObj.getRelationType()
                                       && relationBeforeObj.getRelationPageNumber() == relationBeforeObj.getRelationPageNumber());
                    }

                    assertTrue(booleanSet.contains(true));
                }
            }
        }
    }

    public void testInstanceStoreAndRetrieve() throws SQLException, ProcessingException {
        HashMap<String, Instance> instanceMap;
        Instance malfunctioningInstanceObj;
        Instance suspendedInstanceObj;
        Instance unparseableInstanceObj;
        boolean executionOutcome;

        table = "bad_instances";

        dataStoreObj.storeInstance(TEST_MALFUNCTIONING_INSTANCE);
        dataStoreObj.storeInstance(TEST_SUSPENDED_INSTANCE);
        dataStoreObj.storeInstance(TEST_UNPARSEABLE_INSTANCE);

        instanceMap = dataStoreObj.retrieveBadInstances();

        malfunctioningInstanceObj = instanceMap.get(TEST_MALFUNCTIONING_INSTANCE.getInstanceHostname());
        unparseableInstanceObj = instanceMap.get(TEST_UNPARSEABLE_INSTANCE.getInstanceHostname());
        suspendedInstanceObj = instanceMap.get(TEST_SUSPENDED_INSTANCE.getInstanceHostname());

        assertEquals(malfunctioningInstanceObj.getInstanceHostname(), TEST_MALFUNCTIONING_INSTANCE.getInstanceHostname());
        assertEquals(malfunctioningInstanceObj.getInstanceStatus(), TEST_MALFUNCTIONING_INSTANCE.getInstanceStatus());
        assertEquals(unparseableInstanceObj.getInstanceHostname(), TEST_UNPARSEABLE_INSTANCE.getInstanceHostname());
        assertEquals(unparseableInstanceObj.getInstanceStatus(), TEST_UNPARSEABLE_INSTANCE.getInstanceStatus());
        assertEquals(suspendedInstanceObj.getInstanceHostname(), TEST_SUSPENDED_INSTANCE.getInstanceHostname());
        assertEquals(suspendedInstanceObj.getInstanceStatus(), TEST_SUSPENDED_INSTANCE.getInstanceStatus());
    }

    public void testHandleStoreAndRetrieve() throws SQLException {
        ArrayList<Handle> handleList;
        Handle retrievedHandle;
        Handle testHandleWithRemovedHandleId;

        dataStoreObj.storeHandle(TEST_HANDLE_WITHOUT_HANDLE_ID);

        handleList = dataStoreObj.retrieveUnfetchedHandlesFromHandlesVsProfiles();

        retrievedHandle = handleList.get(0);

        assertEquals(retrievedHandle.getUsername(), TEST_HANDLE_WITHOUT_HANDLE_ID.getUsername());
        assertEquals(retrievedHandle.getInstance(), TEST_HANDLE_WITHOUT_HANDLE_ID.getInstance());

        dataStoreObj.clearTable("handles");

        dataStoreObj.storeHandle(TEST_HANDLE_WITH_HANDLE_ID);

        handleList = dataStoreObj.retrieveUnfetchedHandlesFromHandlesVsProfiles();

        retrievedHandle = handleList.get(0);

        assertEquals(retrievedHandle.getHandleId(), TEST_HANDLE_WITH_HANDLE_ID.getHandleId());
        assertEquals(retrievedHandle.getUsername(), TEST_HANDLE_WITH_HANDLE_ID.getUsername());
        assertEquals(retrievedHandle.getInstance(), TEST_HANDLE_WITH_HANDLE_ID.getInstance());

        dataStoreObj.clearTable("handles");

        testHandleWithRemovedHandleId = new Handle(TEST_HANDLE_WITH_HANDLE_ID.getUsername(), TEST_HANDLE_WITH_HANDLE_ID.getInstance());

        dataStoreObj.storeHandle(testHandleWithRemovedHandleId);

        assertTrue(testHandleWithRemovedHandleId.getHandleId() != -1);
    }
}
