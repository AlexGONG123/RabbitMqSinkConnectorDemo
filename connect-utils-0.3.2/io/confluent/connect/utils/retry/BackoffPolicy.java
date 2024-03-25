//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.confluent.connect.utils.retry;

public interface BackoffPolicy {
    long computeBackoffMillis(int var1);

    String type();
}
