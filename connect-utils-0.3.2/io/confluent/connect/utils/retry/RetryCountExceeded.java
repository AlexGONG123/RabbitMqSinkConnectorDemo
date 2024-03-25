//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.confluent.connect.utils.retry;

public class RetryCountExceeded extends RetryPolicyException {
    private final int maxAttempts;

    public RetryCountExceeded(String msg, int maxAttempts, Exception lastException) {
        super(msg, lastException);
        this.maxAttempts = maxAttempts;
    }

    public int attempts() {
        return this.maxAttempts;
    }
}
