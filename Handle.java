package jarachnea;

import java.net.URL;
import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class Handle {

    private final Pattern handleRegex = Pattern.compile("^@([A-Za-z0-9_.-]+)@([A-Za-z0-9_.-]+\\.[a-z]+)$");

    private int handleId;
    private String username;
    private String instance;

    public int getHandleId() {
        return handleId;
    }

    public void setHandleId(final int handleIdInt) {
        handleId = handleIdInt;
    }

    public String getUsername() {
        return username;
    }

    public String getInstance() {
        return instance;
    }

    public Handle(final int handleIdInt, final String usernameString, final String instanceString) {
        handleId = handleIdInt;
        username = usernameString;
        instance = instanceString;
    }

    public Handle(final String usernameString, final String instanceString) {
        handleId = -1;
        username = usernameString;
        instance = instanceString;
    }

    public Handle(final String handleString) throws ProcessingException {
        handleId = -1;
        Matcher handleMatcher;

        handleMatcher = handleRegex.matcher(handleString);

        if (!handleMatcher.matches()) {
            throw new ProcessingException("unable to detect username and instance from handle string '" + handleString + "'");
        }

        this.username = handleMatcher.group(1);
        this.instance = handleMatcher.group(2);
    }

    public String toHandle() {
        return "@" + username + "@" + instance;
    }

    public URL toProfileURL() throws ProcessingException {
        String profileURLString;

        profileURLString = "https://" + instance + "/@" + username;

        try {
            return new URL(profileURLString);
        } catch (MalformedURLException exceptionObj) {
            throw new ProcessingException("unable to form valid profile URL, this was the result: " + profileURLString);
        }
    }

    public URL toFollowersPage1URL() throws ProcessingException {
        String followersURLString;

        followersURLString = "https://" + instance + "/users/" + username + "/followers?page=1";

        try {
            return new URL(followersURLString);
        } catch (MalformedURLException exceptionObj) {
            throw new ProcessingException("unable to form valid followers URL, this was the result: " + followersURLString);
        }
    }

    public URL toFollowingPage1URL() throws ProcessingException {
        String followingURLString;

        followingURLString = "https://" + instance + "/users/" + username + "/following?page=1";

        try {
            return new URL(followingURLString);
        } catch (MalformedURLException exceptionObj) {
            throw new ProcessingException("unable to form valid following URL, this was the result: " + followingURLString);
        }
    }

    public boolean equals(final Handle otherHandleObj) {
        return username.equals(otherHandleObj.username) && instance.equals(otherHandleObj.instance);
    }
}
