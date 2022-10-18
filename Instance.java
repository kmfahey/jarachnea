package jarachnea;

import java.util.Date;


public final class Instance {

    public static final int IN_GOOD_STANDING = 0;
    public static final int SUSPENDED = 1;
    public static final int MALFUNCTIONING = 2;
    public static final int UNPARSEABLE = 3;

    private static final int DEFAULT_RATE_LIMIT_DOWNTIME = 300;
    private static final long MILLISECONDS_IN_A_SECOND = 1_000L;

    private Date rateLimitExpiresDate = null;

    private String instanceHostname;
    private int instanceStatus;

    public String getInstanceHostname() {
        return instanceHostname;
    }

    public int getInstanceStatus() {
        return instanceStatus;
    }

    public void setInstanceStatus(final int statusFlag) {
        instanceStatus = statusFlag;
    }

    public String getInstanceStatusString() {
        String instanceStatusStr;

        if (instanceStatus == Instance.SUSPENDED) {
            instanceStatusStr = "SUSPENDED";
        } else if (instanceStatus == Instance.MALFUNCTIONING) {
            instanceStatusStr = "MALFUNCTIONING";
        } else if (instanceStatus == Instance.UNPARSEABLE) {
            instanceStatusStr = "UNPARSEABLE";
        } else {
            instanceStatusStr = "IN_GOOD_STANDING";
        }

        return instanceStatusStr;
    }

    public Instance(final String instanceHostnameString, final int instanceStatusFlag) {
        instanceHostname = instanceHostnameString;
        instanceStatus = instanceStatusFlag;
    }

    public void setRateLimit(final int limitDowntimeLength) {
        Date nowDate;
        Date rateLimitExpiryDate;

        nowDate = new Date();
        rateLimitExpiresDate = new Date(nowDate.getTime() + (long) limitDowntimeLength * MILLISECONDS_IN_A_SECOND);
    }

    public void setRateLimit() {
        setRateLimit(DEFAULT_RATE_LIMIT_DOWNTIME);
    }

    public boolean rateLimitExpiredYet() {
        float remainingSeconds;

        if (rateLimitExpiresDate == null) {
            return true;
        }

        remainingSeconds = rateLimitRemainingSeconds();
        return remainingSeconds <= 0;
    }

    public float rateLimitRemainingSeconds() {
        Date nowDate;
        float remainingSeconds;

        nowDate = new Date();
        if (rateLimitExpiresDate == null) {
            return 0.0F;
        }
        remainingSeconds = rateLimitExpiresDate.getTime() - nowDate.getTime();

        return remainingSeconds;
    }
}
