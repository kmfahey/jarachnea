package jarachnea;

import java.net.URL;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.HashMap;


public final class Profile {
    public Handle profileHandle;
    public URL profileURL;
    public HashMap<Integer,RelationSet> followingMap;
    public HashMap<Integer,RelationSet> followersMap;

    public Profile(final Handle handleObj) throws MalformedURLException {
        profileHandle = handleObj;
        profileURL = new URL("https://"+handleObj.instance+"/@"+handleObj.username);
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
        if (! profileURLString.matches("^https://[A-Za-z0-9._]+\\.[a-z]+/@[A-Za-z0-9_.]+$")) {
            throw new ParseException("URL "+profileURLString+" does not parse as a standard Mastodon web frontend profile URL", 0);
        } else {
            profileURLParts = profileURLString.split("[@/]");
            profileInstance = profileURLParts[2];
            profileUsername = profileURLParts[4];
            profileHandle = new Handle(profileUsername, profileInstance);
        }
    }

    public URL getProfileURL() {
        return profileURL;
    }

    public URL getFollowingURL() throws MalformedURLException {
        return getFollowingURL(1);
    }

    public URL getFollowingURL(final int pageNo) throws MalformedURLException {
        return new URL("https://"+profileHandle.instance+"/users/"+profileHandle.username+"/following?page="+pageNo);
    }

    public URL getFollowersURL() throws MalformedURLException {
        return getFollowersURL(1);
    }

    public URL getFollowersURL(final int pageNo) throws MalformedURLException {
        return new URL("https://"+profileHandle.instance+"/users/"+profileHandle.username+"/followers?page="+pageNo);
    }

    public int addRelationSet(final RelationSet relationSetObj) {
        if (relationSetObj.relationType == Relation.IS_FOLLOWER_OF) {
            followersMap.put(relationSetObj.relationPageNumber, relationSetObj);
        } else {
            followingMap.put(relationSetObj.relationPageNumber, relationSetObj);
        }

        return relationSetObj.sizeOfSet();
    }
}
