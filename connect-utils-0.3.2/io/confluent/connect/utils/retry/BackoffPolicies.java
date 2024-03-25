//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.confluent.connect.utils.retry;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class BackoffPolicies {
    private static final BackoffPolicy NO_BACKOFF = new NoBackoffPolicy();

    public BackoffPolicies() {
    }

    public static BackoffPolicy noBackoff() {
        return NO_BACKOFF;
    }

    public static BackoffPolicy constantBackoff(Duration backoffDuration) {
        Objects.requireNonNull(backoffDuration);
        return new ConstantBackoffPolicy(backoffDuration.toMillis(), TimeUnit.MILLISECONDS);
    }

    public static BackoffPolicy exponentialJitter(Duration initialBackoff) {
        Objects.requireNonNull(initialBackoff);
        return new ExponentialJitterBackoffPolicy(initialBackoff.toMillis(), TimeUnit.MILLISECONDS);
    }

    public static BackoffPolicy exponentialJitter(Duration initialBackoff, Duration maxRetryTime) {
        Objects.requireNonNull(initialBackoff);
        Objects.requireNonNull(maxRetryTime);
        return new ExponentialJitterBackoffPolicy(initialBackoff.toMillis(), maxRetryTime.toMillis(), TimeUnit.MILLISECONDS);
    }
}
