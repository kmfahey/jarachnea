package jarachnea.junit;

import junit.framework.TestCase;

import jarachnea.Instance;


public final class TestInstance extends TestCase {
    private static final String INSTANCE_HOSTNAME = "mastodon.social";
    private static final int INSTANCE_STATUS = Instance.IN_GOOD_STANDING;

    private static final long SLEEP_DURATION_IN_MILLISECONDS = 3_000L;
    private static final int RATE_LIMIT_IN_SECONDS = 5;

    public void testInstanceConstructor() {
        Instance instanceObj;

        instanceObj = new Instance(INSTANCE_HOSTNAME, INSTANCE_STATUS);

        assertEquals(instanceObj.getInstanceHostname(), INSTANCE_HOSTNAME);
        assertEquals(instanceObj.getInstanceStatus(), INSTANCE_STATUS);
    }

    public void testInstanceRateLimitManagement() throws InterruptedException {
        Instance instanceObj;

        instanceObj = new Instance(INSTANCE_HOSTNAME, INSTANCE_STATUS);
        instanceObj.setRateLimit(RATE_LIMIT_IN_SECONDS);

        Thread.sleep(SLEEP_DURATION_IN_MILLISECONDS);

        assertFalse(instanceObj.rateLimitExpiredYet());

        Thread.sleep(SLEEP_DURATION_IN_MILLISECONDS);

        assertTrue(instanceObj.rateLimitExpiredYet());
    }

    public void testGetInstanceStatusString() {
        Instance instanceObj;

        instanceObj = new Instance(INSTANCE_HOSTNAME, Instance.SUSPENDED);
        assertEquals(instanceObj.getInstanceStatusString(), "SUSPENDED");
        instanceObj = new Instance(INSTANCE_HOSTNAME, Instance.MALFUNCTIONING);
        assertEquals(instanceObj.getInstanceStatusString(), "MALFUNCTIONING");
        instanceObj = new Instance(INSTANCE_HOSTNAME, Instance.UNPARSEABLE);
        assertEquals(instanceObj.getInstanceStatusString(), "UNPARSEABLE");
        instanceObj = new Instance(INSTANCE_HOSTNAME, Instance.IN_GOOD_STANDING);
        assertEquals(instanceObj.getInstanceStatusString(), "IN_GOOD_STANDING");
    }
}
