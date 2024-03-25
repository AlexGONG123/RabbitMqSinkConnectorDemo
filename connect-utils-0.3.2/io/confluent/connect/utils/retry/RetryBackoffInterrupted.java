//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.confluent.connect.utils.retry;

import java.time.Duration;
import java.util.Objects;

public class RetryBackoffInterrupted extends RetryPolicyException {
    private final Duration elapsedTime;

    public RetryBackoffInterrupted(String msg, Duration elapsedTime, Exception lastException) {
        super(msg, lastException);
        this.elapsedTime = (Duration)Objects.requireNonNull(elapsedTime);
    }

    public Duration elapsedTime() {
        return this.elapsedTime;
    }
}
