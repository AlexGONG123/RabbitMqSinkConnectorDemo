//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.confluent.connect.utils.retry;

import java.util.concurrent.TimeUnit;

class ConstantBackoffPolicy implements BackoffPolicy {
    private final long backoffInMillis;

    public ConstantBackoffPolicy(long backoffTime, TimeUnit unit) {
        if (backoffTime <= 0L) {
            throw new IllegalArgumentException("The initial backoff time must be positive");
        } else {
            this.backoffInMillis = unit.toMillis(backoffTime);
        }
    }

    public long computeBackoffMillis(int attemptNumber) {
        return this.backoffInMillis;
    }

    public String type() {
        return "constant (" + this.backoffInMillis + " ms)";
    }
}
