package jarachnea;

import java.net.URL;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.HashMap;


public final class Profile {
    private Handle profileHandle;
    private URL profileURL;
    private int profileHandleId;
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

    public int getProfileHandleId() {
        return profileHandleId;
    }

    public boolean getProfileConsidered() {
        return profileConsidered
    }

    public HashMap<Integer, RelationSet> getFollowingMap() {
        return followingMap;
    }

    public HashMap<Integer, RelationSet> getFollowersMap() {
        return followersMap;
    }


    public Profile(final String profileUsernameString, final String profileInstanceString, final int profileHandleIdInt,
                   final boolean profileConsideredBoolean, final String profileSnippetString) throws MalformedURLException {
        profileHandle = new Handle(profileUsernameString, profileInstanceString);
        profileHandleId = profileHandleIdInt;
        profileConsidered = profileConsideredBoolean;
        profileSnippet = profileSnippetString;
        profileURL = new URL("https://" + handleObj.getInstance() + "/@" + handleObj.getUsername());
        followingMap = new HashMap<Integer, RelationSet>();
        followersMap = new HashMap<Integer, RelationSet>();
    }

    public Profile(final Handle handleObj) throws MalformedURLException {
        profileHandle = handleObj;
        profileURL = new URL("https://" + handleObj.getInstance() + "/@" + handleObj.getUsername());
        followingMap = new HashMap<>();
        followersMap = new HashMap<>();
    }

    public Profile(final URL profileURLObj) throws ParseException {
        String profileURLString;
        String[] profileURLParts;
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
    }

    public URL getFollowingURL() throws MalformedURLException {
        return getFollowingURL(1);
    }

    public URL getFollowingURL(final int pageNo) throws MalformedURLException {
        return new URL("https://" + profileHandle.getInstance() + "/users/" + profileHandle.getUsername() + "/following?page=" + pageNo);
    }

    public URL getFollowersURL() throws MalformedURLException {
        return getFollowersURL(1);
    }

    public URL getFollowersURL(final int pageNo) throws MalformedURLException {
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
