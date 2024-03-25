//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.confluent.connect.utils.retry;

import java.time.Duration;
import java.util.Objects;

public class RetryTimeoutExceeded extends RetryPolicyException {
    private final Duration timeout;

    public RetryTimeoutExceeded(String msg, Duration timeout, Exception lastException) {
        super(msg, lastException);
        this.timeout = (Duration)Objects.requireNonNull(timeout);
    }

    public Duration timeout() {
        return this.timeout;
    }
}
