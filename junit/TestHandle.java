package jarachnea.junit;

import java.net.URL;
import java.net.MalformedURLException;
import junit.framework.TestCase;

import jarachnea.Handle;
import jarachnea.ProcessingException;


public class TestHandle extends TestCase {
    String instance = "mastodon.social";
    String username = "Gargron";
    String profileURLString = "https://mastodon.social/@Gargron";
    String followersURLString = "https://mastodon.social/users/Gargron/followers?page=1";
    String followingURLString = "https://mastodon.social/users/Gargron/following?page=1";

    Handle handleObj;

    public void testHandleConstructorWithHandleString() throws ProcessingException {
        handleObj = new Handle("@"+username+"@"+instance);

        assertEquals(username, handleObj.username);
        assertEquals(instance, handleObj.instance);
    }

    public void testHandleConstructorWithUsernamePlusInstance() {
        handleObj = new Handle(username, instance);

        assertEquals(username, handleObj.username);
        assertEquals(instance, handleObj.instance);
    }

    public void testHandleToHandle() {
        handleObj = new Handle(username, instance);

        assertEquals(handleObj.toHandle(), "@"+username+"@"+instance);
    }

    public void testProfileURL() throws ProcessingException, MalformedURLException {
        handleObj = new Handle(username, instance);

        assertEquals(handleObj.toProfileURL(), new URL(profileURLString));
    }

    public void testFollowersURL() throws ProcessingException, MalformedURLException {
        handleObj = new Handle(username, instance);

        assertEquals(handleObj.toFollowersPage1URL(), new URL(followersURLString));
    }

    public void testFollowingURL() throws ProcessingException, MalformedURLException {
        handleObj = new Handle(username, instance);

        assertEquals(handleObj.toFollowingPage1URL(), new URL(followingURLString));
    }

    public void testHandleEquality() {
        handleObj = new Handle(username, instance);

        Handle otherHandleObj;
    }
}
