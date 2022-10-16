package jarachnea.junit;

import java.lang.Thread;
import java.util.Date;

import junit.framework.TestCase;

import jarachnea.Instance;


public final class TestInstance extends TestCase {
    private final String INSTANCE_HOSTNAME = "mastodon.social";
    private final int INSTANCE_STATUS = Instance.IN_GOOD_STANDING;

    private long SLEEP_DURATION_IN_MILLISECONDS = 3_000L;
    private int RATE_LIMIT_IN_SECONDS = 5;

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
}
