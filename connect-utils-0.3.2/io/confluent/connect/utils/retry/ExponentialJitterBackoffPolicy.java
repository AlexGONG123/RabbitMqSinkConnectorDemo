//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.confluent.connect.utils.retry;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

class ExponentialJitterBackoffPolicy implements BackoffPolicy {
    protected static final long MAX_RETRY_TIME_MS;
    private final long initialRetryBackoffMillis;
    private final long maxRetryTimeMillis;

    public ExponentialJitterBackoffPolicy(long initialRetryBackoff, TimeUnit unit) {
        this(initialRetryBackoff, unit.convert(MAX_RETRY_TIME_MS, TimeUnit.MILLISECONDS), unit);
    }

    public ExponentialJitterBackoffPolicy(long initialRetryBackoff, long maxRetryTime, TimeUnit unit) {
        if (initialRetryBackoff < 0L) {
            throw new IllegalArgumentException("The initial retry backoff time may not be negative");
        } else if (maxRetryTime < initialRetryBackoff) {
            throw new IllegalArgumentException("The max retry time must be larger than initial backoff");
        } else {
            this.initialRetryBackoffMillis = unit.toMillis(initialRetryBackoff);
            this.maxRetryTimeMillis = unit.toMillis(maxRetryTime);
        }
    }

    public long computeBackoffMillis(int attemptNumber) {
        if (attemptNumber < 0) {
            return this.initialRetryBackoffMillis;
        } else {
            long maxRetryTime = computeRetryWaitTimeInMillis(attemptNumber, this.initialRetryBackoffMillis, this.maxRetryTimeMillis);
            return maxRetryTime == 0L ? 0L : ThreadLocalRandom.current().nextLong(0L, maxRetryTime);
        }
    }

    public String type() {
        return String.format("exponential (%s-%s ms)", this.initialRetryBackoffMillis, this.maxRetryTimeMillis);
    }

    public static long computeRetryWaitTimeInMillis(int retryAttempts, long initialRetryBackoffMs, long maxRetryMs) {
        if (initialRetryBackoffMs < 0L) {
            return 0L;
        } else if (retryAttempts <= 0) {
            return initialRetryBackoffMs;
        } else if (maxRetryMs < initialRetryBackoffMs) {
            throw new IllegalArgumentException("The maximum retry backoff time must be larger than the initial time");
        } else if (retryAttempts >= 64) {
            return maxRetryMs;
        } else {
            if (initialRetryBackoffMs == 0L) {
                initialRetryBackoffMs = 1L;
            }

            long result = initialRetryBackoffMs << retryAttempts;
            return result <= 0L ? maxRetryMs : Math.min(maxRetryMs, result);
        }
    }

    static {
        MAX_RETRY_TIME_MS = TimeUnit.HOURS.toMillis(24L);
    }
}
