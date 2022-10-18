package jarachnea;

import java.net.URL;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.HashMap;


public final class Profile {
    private Handle profileHandle;
    private URL profileURL;
    private boolean profileConsidered;
    private String profileSnippet;
    private HashMap<Integer, RelationSet> followingMap;
    private HashMap<Integer, RelationSet> followersMap;

    public Handle getProfileHandle() {
        return profileHandle;
    }

    public URL getProfileURL() {
        return profileURL;
    }

    public boolean getProfileConsidered() {
        return profileConsidered;
    }

    public String getProfileSnippet() {
        return profileSnippet;
    }

    public HashMap<Integer, RelationSet> getFollowingMap() {
        return followingMap;
    }

    public HashMap<Integer, RelationSet> getFollowersMap() {
        return followersMap;
    }

    public Profile(final Handle profileHandleObj, final boolean profileConsideredBoolean, final String profileSnippetString,
                   final Relation[] followingRelations, final Relation[] followersRelations) throws MalformedURLException {
        this(profileHandleObj, profileConsideredBoolean, profileSnippetString);
        loadRelationsArrays(followingRelations, followersRelations);
    }

    public Profile(final Handle profileHandleObj, final boolean profileConsideredBoolean, final String profileSnippetString) throws MalformedURLException {
        profileHandle = profileHandleObj;
        profileConsidered = profileConsideredBoolean;
        profileSnippet = profileSnippetString;
        profileURL = new URL("https://" + profileHandle.getUsername() + "/@" + profileHandle.getInstance());
        followingMap = new HashMap<Integer, RelationSet>();
        followersMap = new HashMap<Integer, RelationSet>();
    }

    public Profile(final String profileUsernameString, final String profileInstanceString, final boolean profileConsideredBoolean,
                   final String profileSnippetString, final Relation[] followingRelations, final Relation[] followersRelations
                   ) throws MalformedURLException {
        this(profileUsernameString, profileInstanceString, profileConsideredBoolean, profileSnippetString);
        loadRelationsArrays(followingRelations, followersRelations);
    }

    public Profile(final String profileUsernameString, final String profileInstanceString,
                   final boolean profileConsideredBoolean, final String profileSnippetString) throws MalformedURLException {
        profileHandle = new Handle(profileUsernameString, profileInstanceString);
        profileConsidered = profileConsideredBoolean;
        profileSnippet = profileSnippetString;
        profileURL = new URL("https://" + profileInstanceString + "/@" + profileUsernameString);
        followingMap = new HashMap<Integer, RelationSet>();
        followersMap = new HashMap<Integer, RelationSet>();
    }

    public Profile(final Handle handleObj, final Relation[] followingRelations, final Relation[] followersRelations) throws MalformedURLException {
        this(handleObj);
        loadRelationsArrays(followingRelations, followersRelations);
    }

    public Profile(final Handle handleObj) throws MalformedURLException {
        profileHandle = handleObj;
        profileURL = new URL("https://" + handleObj.getInstance() + "/@" + handleObj.getUsername());
        profileConsidered = false;
        profileSnippet = "";
        followingMap = new HashMap<>();
        followersMap = new HashMap<>();
    }

    public Profile(final URL profileURLObj, final Relation[] followingRelations, final Relation[] followersRelations) throws ParseException {
        this(profileURLObj);
        loadRelationsArrays(followingRelations, followersRelations);
    }

    public Profile(final URL profileURLObj) throws ParseException {
        String[] profileURLParts;
        String profileURLString;
        String profileInstance;
        String profileUsername;

        profileURL = profileURLObj;
        profileURLString = profileURL.toString();
        if (!profileURLString.matches("^https://[A-Za-z0-9._]+\\.[a-z]+/@[A-Za-z0-9_.]+$")) {
            throw new ParseException("URL " + profileURLString + " does not parse as a standard Mastodon web frontend profile URL", 0);
        } else {
            profileURLParts = profileURLString.split("[@/]");
            profileInstance = profileURLParts[2];
            profileUsername = profileURLParts[4];
            profileHandle = new Handle(profileUsername, profileInstance);
        }
        profileConsidered = false;
        profileSnippet = "";
        followingMap = new HashMap<Integer, RelationSet>();
        followersMap = new HashMap<Integer, RelationSet>();
    }

    private void loadRelationsArrays(final Relation[] followingRelations, final Relation[] followersRelations) {
        Relation[][] relationsArrays = {followingRelations, followersRelations};

        for (int outerIndex = 0; outerIndex < relationsArrays.length; outerIndex++) {
            HashMap<Integer, RelationSet> relationsMap = (outerIndex == 0) ? followingMap : followersMap;

            for (int innerIndex = 0; innerIndex < relationsArrays[outerIndex].length; innerIndex++) {
                RelationSet relationSetObj;
                Relation relationObj;
                int relationPageNumber;

                relationObj = relationsArrays[outerIndex][innerIndex];
                relationPageNumber = relationObj.getRelationPageNumber();

                if (relationsMap.containsKey(relationPageNumber)) {
                    relationSetObj = relationsMap.get(relationPageNumber);
                } else {
                    relationSetObj = new RelationSet(profileHandle, relationObj.getRelationType(), relationPageNumber);
                    relationsMap.put(relationPageNumber, relationSetObj);
                }

                relationSetObj.add(relationsArrays[outerIndex][innerIndex]);
            }
        }
    }

    public URL getFollowingURL() throws MalformedURLException {
        return getFollowingURL(1);
    }

    public URL getFollowingURL(final int pageNo) throws MalformedURLException {
        System.out.println("generating following url");
        return new URL("https://" + profileHandle.getInstance() + "/users/" + profileHandle.getUsername() + "/following?page=" + pageNo);
    }

    public URL getFollowersURL() throws MalformedURLException {
        return getFollowersURL(1);
    }

    public URL getFollowersURL(final int pageNo) throws MalformedURLException {
        System.out.println("generating following url");
        return new URL("https://" + profileHandle.getInstance() + "/users/" + profileHandle.getUsername() + "/followers?page=" + pageNo);
    }

    public int addRelationSet(final RelationSet relationSetObj) {
        if (relationSetObj.getRelationType() == Relation.IS_FOLLOWER_OF) {
            followersMap.put(relationSetObj.getRelationPageNumber(), relationSetObj);
        } else {
            followingMap.put(relationSetObj.getRelationPageNumber(), relationSetObj);
        }

        return relationSetObj.size();
    }
}
