//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.confluent.connect.utils;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class Strings {
    public Strings() {
    }

    public static String toLowerCase(String str) {
        return str == null ? null : str.toLowerCase(Locale.getDefault());
    }

    public static String toUpperCase(String str) {
        return str == null ? null : str.toUpperCase(Locale.getDefault());
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNullOrBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isNullOrEmpty(str);
    }

    public static boolean isNotBlank(String str) {
        return !isNullOrBlank(str);
    }

    public static boolean isAllLowerCase(String str) {
        return isTrueForAllCharacters(str, Character::isLowerCase);
    }

    public static boolean isAlphanumeric(String str) {
        return isTrueForAllCharacters(str, Character::isLetterOrDigit);
    }

    public static <E> String readableJoin(CharSequence delim, Collection<E> items) {
        return "either " + (String)items.stream().map((o) -> {
            return o != null ? o.toString() : null;
        }).collect(Collectors.joining(", or "));
    }

    /** @deprecated */
    @Deprecated
    public static <E> String join(CharSequence delim, Collection<E> items) {
        return "either " + (String)items.stream().map((o) -> {
            return o != null ? o.toString() : null;
        }).collect(Collectors.joining(", or "));
    }

    public static boolean isTrueForAllCharacters(String str, CharPredicate predicate) {
        if (isNullOrBlank(str)) {
            return true;
        } else {
            for(int i = 0; i < str.length(); ++i) {
                if (!predicate.test(str.charAt(i))) {
                    return false;
                }
            }

            return true;
        }
    }

    public static int firstIndexOf(String str, char c1, char c2) {
        return firstIndexOf(str, c1, c2, 0);
    }

    public static int firstIndexOf(String str, char c1, char c2, int startIndex) {
        int c1Index = str.indexOf(c1, startIndex);
        int c2Index = str.indexOf(c2, startIndex);
        if (c1Index == -1) {
            return c2Index;
        } else {
            return c2Index == -1 ? c1Index : Math.min(c1Index, c2Index);
        }
    }

    public static String removeLeadingWhitespace(String input) {
        int len = ((String)Objects.requireNonNull(input)).length();
        if (len == 0) {
            return input;
        } else {
            int index = 0;

            for(char[] val = input.toCharArray(); index < len && val[index] <= ' '; ++index) {
            }

            return index > 0 ? input.substring(index, len) : input;
        }
    }

    public static String removeTrailingWhitespace(String input) {
        int len = ((String)Objects.requireNonNull(input)).length();
        if (len == 0) {
            return input;
        } else {
            for(char[] val = input.toCharArray(); len > 0 && val[len - 1] <= ' '; --len) {
            }

            return len < input.length() ? input.substring(0, len) : input;
        }
    }

    @FunctionalInterface
    public interface CharPredicate {
        boolean test(char var1);

        default CharPredicate and(CharPredicate other) {
            Objects.requireNonNull(other);
            return (value) -> {
                return this.test(value) && other.test(value);
            };
        }

        default CharPredicate negate() {
            return (value) -> {
                return !this.test(value);
            };
        }

        default CharPredicate or(CharPredicate other) {
            Objects.requireNonNull(other);
            return (value) -> {
                return this.test(value) || other.test(value);
            };
        }
    }
}
