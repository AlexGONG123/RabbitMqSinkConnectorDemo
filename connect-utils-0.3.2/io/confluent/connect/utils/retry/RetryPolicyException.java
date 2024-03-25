//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.confluent.connect.utils.retry;

import org.apache.kafka.connect.errors.ConnectException;

public class RetryPolicyException extends ConnectException {
    public RetryPolicyException(String msg, Exception lastException) {
        super(msg, lastException);
    }
}
