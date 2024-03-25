import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.connect.errors.ConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetryPolicy {
    private static final Logger log = LoggerFactory.getLogger(RetryPolicy.class);
    private static final Duration MAX_DURATION = Duration.ofMillis(Long.MAX_VALUE);
    private final int maxAttempts;
    private final RetryCondition whenError;
    private final BackoffPolicy backoffPolicy;
    private final Optional<Duration> maxRetryTimeout;
    private final Time clock;

    public static Builder builder() {
        return new Builder();
    }

    protected RetryPolicy(Builder builder) {
        this.maxAttempts = builder.maxAttempts;
        this.whenError = (RetryCondition)builder.whenError.orElse(RetryCondition.never());
        this.backoffPolicy = (BackoffPolicy)builder.backoffPolicy.orElse(BackoffPolicies.noBackoff());
        this.maxRetryTimeout = builder.maxRetryTimeout;
        this.clock = (Time)builder.clock.orElse(Time.SYSTEM);

        assert this.maxAttempts >= 1;

    }

    public int maxAttempts() {
        return this.maxAttempts;
    }

    public int maxRetries() {
        return this.maxAttempts - 1;
    }

    public Optional<Duration> maxRetryTimeout() {
        return this.maxRetryTimeout;
    }

    public BackoffPolicy backoffPolicy() {
        return this.backoffPolicy;
    }

    public RetryCondition retryCondition() {
        return this.whenError;
    }

    public <T> T call(Supplier<String> description, Callable<T> function) {
        return this.call((String)description.get(), function);
    }

    public <T> T call(String description, Callable<T> function) {
        return this.callWith(description, () -> {
            return null;
        }, (nullResource) -> {
            return function.call();
        });
    }

    public <ResourceT extends AutoCloseable, T> T callWith(Supplier<String> description, ResourceSupplier<ResourceT> resourceSupplier, FunctionWithResource<ResourceT, T> function) {
        return this.callWith((String)description.get(), resourceSupplier, function);
    }

    public <ResourceT extends AutoCloseable, T> T callWith(String description, ResourceSupplier<ResourceT> resourceSupplier, FunctionWithResource<ResourceT, T> function) {
        int attempt = 0;
        Exception lastException = null;
        RetryCounter retryCounter = null;

        while(true) {
            ++attempt;
            log.trace("Create resources for {} (attempt {} of {})", new Object[]{description, attempt, this.maxAttempts});

            try {
                ResourceT resource = (AutoCloseable)resourceSupplier.get();
                Throwable var8 = null;

                Object var29;
                try {
                    log.trace("Try {} (attempt {} of {})", new Object[]{description, attempt, this.maxAttempts});
                    var29 = function.apply(resource);
                } catch (Throwable var21) {
                    var8 = var21;
                    throw var21;
                } finally {
                    if (resource != null) {
                        if (var8 != null) {
                            try {
                                resource.close();
                            } catch (Throwable var25) {
                                var8.addSuppressed(var25);
                            }
                        } else {
                            resource.close();
                        }
                    }

                }

                return var29;
            } catch (Exception var27) {
                Exception e = var27;
                if (!this.whenError.isRetriable(var27)) {
                    throw new ConnectException(this.exceptionMessageFor(var27, description, attempt, this.maxAttempts), var27);
                }

                if (attempt >= this.maxAttempts) {
                    throw new RetryCountExceeded(this.exceptionMessageFor(var27, description, attempt, this.maxAttempts), this.maxAttempts, var27);
                }

                if (retryCounter == null) {
                    retryCounter = this.newRetryCounter();
                }

                try {
                    log.trace("Waiting before retrying to {} (attempt {} of {})", new Object[]{description, attempt, this.maxAttempts, e.getMessage()});
                    retryCounter.backoffAfterFailedAttempt();
                    log.debug("Retrying to {} (attempt {} of {}) after previous retriable error: {}", new Object[]{description, attempt, this.maxAttempts, e.getMessage(), e});
                } catch (RetryCounter.RetryCountExceeded var22) {
                    throw new RetryCountExceeded(this.exceptionMessageFor(var27, description, attempt, this.maxAttempts), this.maxAttempts, var27);
                } catch (RetryCounter.RetryTimeoutExceeded var23) {
                    throw new RetryTimeoutExceeded(this.exceptionMessageFor(var27, description, attempt, this.maxAttempts), (Duration)this.maxRetryTimeout().get(), var27);
                } catch (RetryCounter.BackoffInterruptedException var24) {
                    Duration duration = Duration.ofMillis(var24.getElapsedTimeInMillis());
                    throw new RetryBackoffInterrupted(this.interruptedExceptionMessageFor(var27, description, attempt, duration), duration, var27);
                }
            }
        }
    }

    public String toString() {
        return this.maxRetryTimeout().isPresent() ? String.format("Call functions and retry up to %d times or as many times up to %d milliseconds using %s backoff", this.maxRetries(), ((Duration)this.maxRetryTimeout().get()).toMillis(), this.backoffPolicy().type()) : String.format("Call functions and retry up to %d times using %s backoff", this.maxRetries(), this.backoffPolicy().type());
    }

    protected String interruptedExceptionMessageFor(Exception lastException, String operationDescription, int attempt, Duration interruptedAfter) {
        return String.format("Interrupted after %s on attempt %d of %d to %s. Previous error: %s", interruptedAfter.toString(), attempt, this.maxAttempts, operationDescription, lastException.getMessage());
    }

    protected String exceptionMessageFor(Exception lastException, String operationDescription, int attempt, int maxAttempts) {
        if (maxAttempts == 1) {
            return String.format("Failed on 1st attempt to %s: %s", operationDescription, lastException.getMessage());
        } else {
            return attempt == maxAttempts ? String.format("Failed after %d attempts to %s: %s", maxAttempts, operationDescription, lastException.getMessage()) : String.format("Failed on attempt %d of %d to %s: %s", attempt, maxAttempts, operationDescription, lastException.getMessage());
        }
    }

    protected RetryCounter newRetryCounter() {
        return RetryCounter.using(0, this.maxAttempts, ((Duration)this.maxRetryTimeout.orElse(MAX_DURATION)).toMillis(), TimeUnit.MILLISECONDS, this.backoffPolicy, this.clock);
    }

    public static class Builder {
        private int maxAttempts = 1;
        private Optional<RetryCondition> whenError = Optional.empty();
        private Optional<BackoffPolicy> backoffPolicy = Optional.empty();
        private Optional<Duration> maxRetryTimeout = Optional.empty();
        private Optional<Time> clock = Optional.empty();

        public Builder() {
        }

        public Builder withNoRetries() {
            return this.maxAttempts(1);
        }

        public Builder maxAttempts(int maxAttempts) {
            if (maxAttempts < 1) {
                throw new IllegalArgumentException(String.format("Maximum number of attempts (%d) must be positive.", maxAttempts));
            } else {
                this.maxAttempts = maxAttempts;
                return this;
            }
        }

        public Builder maxRetries(int maxRetries) {
            return this.maxAttempts(1 + maxRetries);
        }

        public Builder maxRetryTimeout(Duration maxRetryTimeout) {
            this.maxRetryTimeout = Optional.ofNullable(maxRetryTimeout);
            return this;
        }

        public Builder backoffPolicy(BackoffPolicy backoffPolicy) {
            this.backoffPolicy = Optional.ofNullable(backoffPolicy);
            return this;
        }

        public Builder constantBackoffPolicy(Duration backoffDuration) {
            Objects.requireNonNull(backoffDuration);
            this.backoffPolicy = Optional.of(new ConstantBackoffPolicy(backoffDuration.toMillis(), TimeUnit.MILLISECONDS));
            return this;
        }

        public Builder exponentialJitterBackoff(Duration initialBackoff) {
            Objects.requireNonNull(initialBackoff);
            this.backoffPolicy = Optional.of(new ExponentialJitterBackoffPolicy(initialBackoff.toMillis(), TimeUnit.MILLISECONDS));
            return this;
        }

        public Builder when(RetryCondition retryWhen) {
            Objects.requireNonNull(retryWhen);
            if (this.maxAttempts <= 1) {
                throw new IllegalArgumentException("The retry condition will never be called since maxAttempts=1");
            } else {
                this.whenError = Optional.of(retryWhen);
                return this;
            }
        }

        public Builder usingClock(Time clock) {
            this.clock = Optional.ofNullable(clock);
            return this;
        }

        public RetryPolicy build() {
            return new RetryPolicy(this);
        }
    }

    @FunctionalInterface
    public interface FunctionWithResource<ResourceT, ReturnT> {
        ReturnT apply(ResourceT var1) throws Exception;
    }

    @FunctionalInterface
    public interface ResourceSupplier<ResourceT> {
        ResourceT get() throws Exception;
    }
}