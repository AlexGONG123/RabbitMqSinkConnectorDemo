
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import org.apache.kafka.connect.errors.RetriableException;

@FunctionalInterface
public interface RetryCondition {
    boolean isRetriable(Exception var1);

    default RetryCondition or(RetryCondition other) {
        Objects.requireNonNull(other);
        return (e) -> {
            return this.isRetriable(e) || other.isRetriable(e);
        };
    }

    default RetryCondition and(RetryCondition other) {
        Objects.requireNonNull(other);
        return (e) -> {
            return this.isRetriable(e) && other.isRetriable(e);
        };
    }

    default RetryCondition orInstanceOf(Class<? extends Exception>... errorClasses) {
        Objects.requireNonNull(errorClasses);
        if (errorClasses.length == 0) {
            throw new IllegalArgumentException("At least one exception class must be specified");
        } else {
            return this.or(retryOn(errorClasses));
        }
    }

    default RetryCondition orConnectRetriable() {
        return this.or(retryOnConnectRetriable());
    }

    default RetryCondition orKafkaRetriable() {
        return this.or(retryOnKafkaRetriable());
    }

    default RetryCondition orSqlException() {
        return this.or(retryOnAnySqlException());
    }

    default RetryCondition orSqlExceptionMatching(Function<SQLException, Boolean> condition) {
        Objects.requireNonNull(condition);
        return this.or(retryOnSqlExceptionMatching(condition));
    }

    default RetryCondition orSqlExceptionCodeIs(int... errorCodes) {
        if (errorCodes.length == 0) {
            throw new IllegalArgumentException("At least one error code must be specified");
        } else {
            return this.or(retryOnSqlExceptionCode(errorCodes));
        }
    }

    static RetryCondition always() {
        return (e) -> {
            return true;
        };
    }

    static RetryCondition never() {
        return (e) -> {
            return false;
        };
    }

    static RetryCondition retryOn(Class<? extends Exception> errorClass) {
        Objects.requireNonNull(errorClass);
        return (e) -> {
            return errorClass.isInstance(e);
        };
    }

    static RetryCondition retryOn(Class<? extends Exception>... errorClasses) {
        if (errorClasses.length == 0) {
            throw new IllegalArgumentException("At least one exception class must be specified");
        } else {
            return (e) -> {
                return Arrays.stream(errorClasses).filter((clazz) -> {
                    return clazz.isInstance(e);
                }).findFirst().isPresent();
            };
        }
    }

    static <T extends Exception> RetryCondition retryOn(Function<T, Boolean> criteria) {
        Objects.requireNonNull(criteria);
        return (e) -> {
            try {
                return (Boolean)criteria.apply(e);
            } catch (ClassCastException var3) {
                return false;
            }
        };
    }

    static RetryCondition retryOnConnectRetriable() {
        return retryOn(RetriableException.class);
    }

    static RetryCondition retryOnKafkaRetriable() {
        return retryOn(org.apache.kafka.common.errors.RetriableException.class);
    }

    static RetryCondition retryOnAnySqlException() {
        return retryOn(SQLException.class);
    }

    static RetryCondition retryOnSqlExceptionMatching(Function<SQLException, Boolean> criteria) {
        Objects.requireNonNull(criteria);
        return retryOnAnySqlException().and((e) -> {
            SQLException sqlException = (SQLException)e;

            while(!(Boolean)criteria.apply(sqlException)) {
                sqlException = sqlException.getNextException();
                if (sqlException == null) {
                    return false;
                }
            }

            return true;
        });
    }

    static RetryCondition retryOnSqlExceptionCode(int... errorCodes) {
        if (errorCodes.length == 0) {
            throw new IllegalArgumentException("At least one error code must be specified");
        } else {
            return retryOnSqlExceptionMatching((sqlException) -> {
                int errorCode = sqlException.getErrorCode();
                int[] var3 = errorCodes;
                int var4 = errorCodes.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    int expectedErrorCode = var3[var5];
                    if (errorCode == expectedErrorCode) {
                        return true;
                    }
                }

                return false;
            });
        }
    }
}