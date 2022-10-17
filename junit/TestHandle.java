package jarachnea.junit;

import java.net.URL;
import java.net.MalformedURLException;
import junit.framework.TestCase;

import jarachnea.Handle;
import jarachnea.ProcessingException;


public final class TestHandle extends TestCase {
    private String instance = "mastodon.social";
    private String username = "Gargron";
    private String profileURLString = "https://mastodon.social/@Gargron";
    private String followersURLString = "https://mastodon.social/users/Gargron/followers?page=1";
    private String followingURLString = "https://mastodon.social/users/Gargron/following?page=1";

    private Handle handleObj;

    public void testHandleConstructorWithHandleString() throws ProcessingException {
        handleObj = new Handle("@" + username + "@" + instance);

        assertEquals(handleObj.getHandleId(), -1);
        assertEquals(username, handleObj.getUsername());
        assertEquals(instance, handleObj.getInstance());
    }

    public void testHandleConstructorWithHandleIdPlusUsernamePlusInstance() {
        handleObj = new Handle(1, username, instance);

        assertEquals(handleObj.getHandleId(), 1);
        assertEquals(handleObj.getUsername(), username);
        assertEquals(handleObj.getInstance(), instance);
    }

    public void testHandleConstructorWithUsernamePlusInstance() {
        handleObj = new Handle(username, instance);

        assertEquals(handleObj.getHandleId(), -1);
        assertEquals(handleObj.getUsername(), username);
        assertEquals(handleObj.getInstance(), instance);
    }

    public void testGetHandleIdSetHandleId() {
        handleObj = new Handle(username, instance);

        assertEquals(handleObj.getHandleId(), -1);
        handleObj.setHandleId(1);
        assertEquals(handleObj.getHandleId(), 1);
    }

    public void testHandleToHandle() {
        handleObj = new Handle(username, instance);

        assertEquals(handleObj.toHandle(), "@" + username + "@" + instance);
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
