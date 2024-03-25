//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.confluent.connect.utils.retry;

class NoBackoffPolicy implements BackoffPolicy {
    NoBackoffPolicy() {
    }

    public long computeBackoffMillis(int attemptNumber) {
        return 0L;
    }

    public String type() {
        return "no";
    }
}
