package ru.tinkoff.qa.neptune.core.api.steps;

import java.util.function.Predicate;

import static ru.tinkoff.qa.neptune.core.api.steps.AsIsCondition.AS_IS;
import static ru.tinkoff.qa.neptune.core.api.utils.IsLoggableUtil.isLoggable;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

interface Condition<T> extends Predicate<T> {

    default Predicate<T> and(Predicate<? super T> other) {
        checkNotNull(other);

        var thisCondition = this;
        return new Condition<>() {

            @Override
            public boolean test(T t) {
                return thisCondition.test(t) && other.test(t);
            }

            @Override
            public String toString() {
                if (!AS_IS.equals(other) && isLoggable(other)) {
                    return format("%s, %s", thisCondition.toString(), other.toString());
                }
                return thisCondition.toString();
            }
        };
    }

    default Predicate<T> negate() {
        var thisCondition = this;

        return new Condition<>() {
            @Override
            public boolean test(T t) {
                return !thisCondition.test(t);
            }

            @Override
            public String toString() {
                return format("not (%s)", thisCondition.toString());
            }
        };
    }

    default Predicate<T> or(Predicate<? super T> other) {
        checkNotNull(other);

        var thisCondition = this;
        return new Condition<>() {

            @Override
            public boolean test(T t) {
                if (!AS_IS.equals(other)) {
                    return thisCondition.test(t) || other.test(t);
                }
                return thisCondition.test(t);
            }

            @Override
            public String toString() {
                if (!AS_IS.equals(other) && isLoggable(other)) {
                    return format("(%s) or (%s)", thisCondition.toString(), other.toString());
                }
                return thisCondition.toString();
            }
        };
    }
}