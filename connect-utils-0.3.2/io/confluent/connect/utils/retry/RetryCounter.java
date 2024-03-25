import io.confluent.connect.utils.Strings;
import io.confluent.connect.utils.TimeUtil;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.kafka.common.utils.LogContext;
import org.apache.kafka.common.utils.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetryCounter implements AutoCloseable {
    protected static final long MAX_RETRY_TIME_MS;
    protected static final long SLEEP_INCREMENT;
    private static final long NOT_YET_SET = -1L;
    private final AtomicBoolean running;
    private final Time clock;
    private final int minRetries;
    private final int maxRetries;
    private final long maxTotalRetryTimeoutMillis;
    private final BackoffPolicy policy;
    private long stopTimeMillis;
    private int retryAttempts;
    private Logger log;
    private String name;

    public static RetryCounter usingConstantBackoff(int minRetries, int maxRetries, long constantBackoff, TimeUnit unit) {
        if (constantBackoff < 0L) {
            throw new IllegalArgumentException("The backoff time must be 0 or more");
        } else {
            return using(minRetries, maxRetries, BackoffPolicies.constantBackoff(Duration.ofMillis(unit.toMillis(constantBackoff))));
        }
    }

    public static RetryCounter usingExponentialJitter(int minRetries, int maxRetries, long initialBackoff, TimeUnit unit) {
        if (initialBackoff < 0L) {
            throw new IllegalArgumentException("The initial backoff time must be 0 or more");
        } else {
            return using(minRetries, maxRetries, BackoffPolicies.exponentialJitter(Duration.ofMillis(unit.toMillis(initialBackoff))));
        }
    }

    public static RetryCounter usingExponentialJitter(long maxTotalRetryTimeout, TimeUnit unit) {
        return using(0, Integer.MAX_VALUE, maxTotalRetryTimeout, unit, BackoffPolicies.exponentialJitter(Duration.ofMillis(0L), Duration.ofMillis(MAX_RETRY_TIME_MS)));
    }

    public static RetryCounter using(int minRetries, int maxRetries, BackoffPolicy policy) {
        return using(minRetries, maxRetries, Long.MAX_VALUE, TimeUnit.MILLISECONDS, policy);
    }

    public static RetryCounter using(int minRetries, int maxRetries, long maxTotalRetryTimeout, TimeUnit unit, BackoffPolicy policy) {
        return new RetryCounter(minRetries, maxRetries, unit.toMillis(maxTotalRetryTimeout), policy);
    }

    public static RetryCounter using(int minRetries, int maxRetries, long maxTotalRetryTimeout, TimeUnit unit, BackoffPolicy policy, Time clock) {
        return new RetryCounter(minRetries, maxRetries, unit.toMillis(maxTotalRetryTimeout), policy, clock);
    }

    protected RetryCounter(int minRetries, int maxRetries, long maxTotalRetryTimeoutMillis, BackoffPolicy policy) {
        this(minRetries, maxRetries, maxTotalRetryTimeoutMillis, policy, Time.SYSTEM);
    }

    protected RetryCounter(int minRetries, int maxRetries, long maxTotalRetryTimeoutMillis, BackoffPolicy policy, Time clock) {
        this.running = new AtomicBoolean(true);
        this.log = LoggerFactory.getLogger(RetryCounter.class);
        this.name = "";
        if (minRetries < 0) {
            throw new IllegalArgumentException("The minimum number of retries must be 0 or more");
        } else if (maxRetries < minRetries) {
            throw new IllegalArgumentException("The maximum number of retries (" + maxRetries + ") must be larger than the minimum number (" + minRetries + ")");
        } else if (maxTotalRetryTimeoutMillis <= 0L) {
            throw new IllegalArgumentException("The maximum retry timeout must be positive");
        } else if (policy == null) {
            throw new IllegalArgumentException("The backoff policy may not be null");
        } else if (clock == null) {
            throw new IllegalArgumentException("The clock may not be null");
        } else {
            this.minRetries = minRetries;
            this.maxRetries = maxRetries;
            this.policy = policy;
            this.clock = clock;
            this.maxTotalRetryTimeoutMillis = maxTotalRetryTimeoutMillis;
            this.stopTimeMillis = -1L;
        }
    }

    public RetryCounter name(String name) {
        if (name == null) {
            name = "";
        }

        if (!Objects.equals(this.name, name)) {
            this.log = (new LogContext(name)).logger(RetryCounter.class);
            this.name = name;
        }

        return this;
    }

    public String name() {
        return this.name;
    }

    public void reset() {
        this.retryAttempts = 0;
        this.stopTimeMillis = -1L;
    }

    public boolean backoffAfterFailedAttempt() throws RetryCountExceeded, BackoffInterruptedException, RetryTimeoutExceeded {
        return this.backoffAfterFailedAttempt((String)null);
    }

    public boolean backoffAfterFailedAttempt(String reason) throws RetryCountExceeded, BackoffInterruptedException, RetryTimeoutExceeded {
        if (this.retryAttempts < this.minRetries) {
            ++this.retryAttempts;
            this.log.debug("Retry {} of {} before beginning {} backoff", new Object[]{this.retryAttempts, this.minRetries, this.type()});
            return false;
        } else if (this.retryAttempts >= this.maxRetries) {
            throw new RetryCountExceeded(this.name(), String.format("Exceeded the maximum number of retries (%s)", this.maxRetries));
        } else {
            long timeRemaining = this.timeRemaining();
            if (timeRemaining <= 0L) {
                throw new RetryTimeoutExceeded(this.name(), String.format("Exceeded the maximum retry time (%s)", this.maxTotalRetryTimeoutMillis));
            } else {
                long sleepTimeMs = this.policy.computeBackoffMillis(this.retryAttempts - this.minRetries);
                sleepTimeMs = Math.min(sleepTimeMs, timeRemaining);
                String sleepTimeStr = TimeUtil.durationAsString(sleepTimeMs);
                if (Strings.isNullOrEmpty(reason)) {
                    this.log.debug("Start {} backoff of {} before another attempt", this.type(), sleepTimeStr);
                } else {
                    this.log.debug("Start {} backoff of {} before another attempt: ", new Object[]{this.type(), sleepTimeStr, reason});
                }

                long startMillis = this.clock.milliseconds();
                if (this.sleep(sleepTimeMs)) {
                    this.log.debug("Completed {} backoff of {}", this.type(), sleepTimeStr);
                    ++this.retryAttempts;
                    return true;
                } else {
                    long elapsedMillis = this.clock.milliseconds() - startMillis;
                    throw new BackoffInterruptedException(this.name(), elapsedMillis);
                }
            }
        }
    }

    public void close() {
        this.running.set(false);
    }

    protected String type() {
        return this.policy.type();
    }

    protected boolean sleep(long timeInMillis) {
        long remainingMillis = timeInMillis;

        for(long stopMillis = this.clock.milliseconds() + timeInMillis; this.running.get() && remainingMillis > 0L; remainingMillis = stopMillis - this.clock.milliseconds()) {
            long sleepTimeMs = Math.min(remainingMillis, SLEEP_INCREMENT);
            if (this.log.isTraceEnabled()) {
                this.log.trace("Sleeping for {}ms, {}ms remaining", TimeUtil.durationAsString(sleepTimeMs), remainingMillis);
            }

            this.clock.sleep(Math.min(remainingMillis, SLEEP_INCREMENT));
        }

        return this.running.get();
    }

    protected long timeRemaining() {
        if (this.maxTotalRetryTimeoutMillis == Long.MAX_VALUE) {
            return Long.MAX_VALUE;
        } else {
            long now = this.clock.milliseconds();
            if (this.stopTimeMillis == -1L) {
                this.stopTimeMillis = now + this.maxTotalRetryTimeoutMillis;
                if (this.stopTimeMillis < 0L) {
                    this.stopTimeMillis = Long.MAX_VALUE;
                    return this.stopTimeMillis;
                }
            }

            return this.stopTimeMillis - now;
        }
    }

    static {
        MAX_RETRY_TIME_MS = TimeUnit.HOURS.toMillis(24L);
        SLEEP_INCREMENT = TimeUnit.SECONDS.toMillis(2L);
    }

    public static class BackoffInterruptedException extends RetryCounterException {
        private final long elapsedTimeInMillis;

        public BackoffInterruptedException(String counterName, long elapsedTimeInMillis) {
            super(counterName, "Backoff has been halted after " + TimeUtil.durationAsString(elapsedTimeInMillis));
            this.elapsedTimeInMillis = elapsedTimeInMillis;
        }

        public long getElapsedTimeInMillis() {
            return this.elapsedTimeInMillis;
        }
    }

    public static class RetryCountExceeded extends RetryCounterException {
        public RetryCountExceeded(String counterName, String msg) {
            super(counterName, msg);
        }
    }

    public static class RetryTimeoutExceeded extends RetryCounterException {
        public RetryTimeoutExceeded(String counterName, String msg) {
            super(counterName, msg);
        }
    }

    public static class RetryCounterException extends Exception {
        private final String counterName;

        public RetryCounterException(String counterName, String msg) {
            super(counterName + msg);
            this.counterName = counterName;
        }

        public String getCounterName() {
            return this.counterName;
        }
    }
}