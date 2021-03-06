package ru.tinkoff.qa.neptune.core.api.steps.conditions;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;
import static java.util.stream.StreamSupport.stream;
import static ru.tinkoff.qa.neptune.core.api.steps.conditions.ToGetConditionalHelper.fluentWaitFunction;

@SuppressWarnings("unchecked")
public final class ToGetObjectFromIterable {

    private ToGetObjectFromIterable() {
        super();
    }

    private static <T, R, V extends Iterable<R>> Function<T, R> singleFromIterable(Function<T, V> function,
                                                                                   Predicate<? super R> condition,
                                                                                   @Nullable Duration waitingTime,
                                                                                   @Nullable Duration sleepingTime,
                                                                                   @Nullable Supplier<? extends RuntimeException> exceptionSupplier) {
        return fluentWaitFunction(t ->
                        ofNullable(function.apply(t))
                                .map(v -> stream(v.spliterator(), false).filter(r -> {
                                    try {
                                        return ToGetConditionalHelper.notNullAnd(condition).test(r);
                                    } catch (Throwable t1) {
                                        return ToGetConditionalHelper.printErrorAndFalse(t1);
                                    }
                                }).findFirst().orElse(null))
                                .orElse(null),
                waitingTime, sleepingTime, Objects::nonNull, exceptionSupplier);
    }

    /**
     * This method returns a function. The result function returns a single first found value which
     * suits criteria from {@link Iterable}.
     *
     * @param function          function which should return {@link Iterable}
     * @param condition         predicate which is used to find some target value
     * @param waitingTime       is a duration of the waiting for valuable result
     * @param sleepingTime      is a duration of the sleeping between attempts to get
     *                          expected valuable result
     * @param exceptionSupplier is a supplier which returns the exception to be thrown on the waiting time
     *                          expiration
     * @param <T>               is a type of input value
     * @param <R>               is a type of the target value
     * @param <V>               is a type of {@link Iterable} of {@code R}
     * @return a function. The result function returns a single first found value from {@link Iterable}.
     * It returns a value if something that suits criteria is found. Some exception is thrown if
     * result iterable to get value from is null or has zero-size or it has no item which suits criteria.
     */
    public static <T, R, V extends Iterable<R>> Function<T, R> getFromIterable(Function<T, V> function,
                                                                               Predicate<? super R> condition,
                                                                               Duration waitingTime,
                                                                               Duration sleepingTime,
                                                                               Supplier<? extends RuntimeException> exceptionSupplier) {
        return singleFromIterable(ToGetConditionalHelper.checkFunction(function), ToGetConditionalHelper.checkCondition(condition), ToGetConditionalHelper.checkWaitingTime(waitingTime),
                ToGetConditionalHelper.checkSleepingTime(sleepingTime), ToGetConditionalHelper.checkExceptionSupplier(exceptionSupplier));
    }

    /**
     * This method returns a function. The result function returns a single first found value from {@link Iterable}.
     *
     * @param function          function which should return {@link Iterable}
     * @param waitingTime       is a duration of the waiting for valuable result
     * @param sleepingTime      is a duration of the sleeping between attempts to get
     *                          expected valuable result
     * @param exceptionSupplier is a supplier which returns the exception to be thrown on the waiting time
     *                          expiration
     * @param <T>               is a type of input value
     * @param <R>               is a type of the target value
     * @param <V>               is a type of {@link Iterable} of {@code R}
     * @return a function. The result function returns a single first found non-null value from {@link Iterable}.
     * Some exception is thrown if result iterable to get value from is null or has zero-size.
     */
    public static <T, R, V extends Iterable<R>> Function<T, R> getFromIterable(Function<T, V> function,
                                                                               Duration waitingTime,
                                                                               Duration sleepingTime,
                                                                               Supplier<? extends RuntimeException> exceptionSupplier) {

        return singleFromIterable(ToGetConditionalHelper.checkFunction(function), (Predicate<? super R>) ToGetConditionalHelper.AS_IS, ToGetConditionalHelper.checkWaitingTime(waitingTime),
                ToGetConditionalHelper.checkSleepingTime(sleepingTime), ToGetConditionalHelper.checkExceptionSupplier(exceptionSupplier));
    }

    /**
     * This method returns a function. The result function returns a single first found value which
     * suits criteria from {@link Iterable}.
     *
     * @param function          function which should return {@link Iterable}
     * @param condition         predicate which is used to find some target value
     * @param waitingTime       is a duration of the waiting for valuable result
     * @param exceptionSupplier is a supplier which returns the exception to be thrown on the waiting time
     *                          expiration
     * @param <T>               is a type of input value
     * @param <R>               is a type of the target value
     * @param <V>               is a type of {@link Iterable} of {@code R}
     * @return a function. The result function returns a single first found value from {@link Iterable}.
     * It returns a value if something that suits criteria is found. Some exception is thrown if
     * result iterable to get value from is null or has zero-size or it has no item which suits criteria.
     */
    public static <T, R, V extends Iterable<R>> Function<T, R> getFromIterable(Function<T, V> function,
                                                                               Predicate<? super R> condition,
                                                                               Duration waitingTime,
                                                                               Supplier<? extends RuntimeException> exceptionSupplier) {
        return singleFromIterable(ToGetConditionalHelper.checkFunction(function), ToGetConditionalHelper.checkCondition(condition), ToGetConditionalHelper.checkWaitingTime(waitingTime),
                null, ToGetConditionalHelper.checkExceptionSupplier(exceptionSupplier));
    }

    /**
     * This method returns a function. The result function returns a single first found value from {@link Iterable}.
     *
     * @param function          function which should return {@link Iterable}
     * @param waitingTime       is a duration of the waiting for valuable result
     * @param exceptionSupplier is a supplier which returns the exception to be thrown on the waiting time
     *                          expiration
     * @param <T>               is a type of input value
     * @param <R>               is a type of the target value
     * @param <V>               is a type of {@link Iterable} of {@code R}
     * @return a function. The result function returns a single first found non-null value from {@link Iterable}.
     * Some exception is thrown if result iterable to get value from is null or has zero-size.
     */
    public static <T, R, V extends Iterable<R>> Function<T, R> getFromIterable(Function<T, V> function,
                                                                               Duration waitingTime,
                                                                               Supplier<? extends RuntimeException> exceptionSupplier) {
        return singleFromIterable(ToGetConditionalHelper.checkFunction(function), (Predicate<? super R>) ToGetConditionalHelper.AS_IS, ToGetConditionalHelper.checkWaitingTime(waitingTime), null,
                ToGetConditionalHelper.checkExceptionSupplier(exceptionSupplier));
    }

    /**
     * This method returns a function. The result function returns a single first found value which
     * suits criteria from {@link Iterable}.
     *
     * @param function          function which should return {@link Iterable}
     * @param condition         predicate which is used to find some target value
     * @param exceptionSupplier is a supplier which returns the exception to be thrown on the waiting time
     *                          expiration
     * @param <T>               is a type of input value
     * @param <R>               is a type of the target value
     * @param <V>               is a type of {@link Iterable} of {@code R}
     * @return a function. The result function returns a single first found value from {@link Iterable}.
     * It returns a value if something that suits criteria is found. Some exception is thrown if
     * result iterable to get value from is null or has zero-size or it has no item which suits criteria.
     */
    public static <T, R, V extends Iterable<R>> Function<T, R> getFromIterable(Function<T, V> function,
                                                                               Predicate<? super R> condition,
                                                                               Supplier<? extends RuntimeException> exceptionSupplier) {
        return singleFromIterable(ToGetConditionalHelper.checkFunction(function), ToGetConditionalHelper.checkCondition(condition), null, null,
                ToGetConditionalHelper.checkExceptionSupplier(exceptionSupplier));
    }

    /**
     * This method returns a function. The result function returns a single first found value from {@link Iterable}.
     *
     * @param function          function which should return {@link Iterable}
     * @param exceptionSupplier is a supplier which returns the exception to be thrown on the waiting time
     *                          expiration
     * @param <T>               is a type of input value
     * @param <R>               is a type of the target value
     * @param <V>               is a type of {@link Iterable} of {@code R}
     * @return a function. The result function returns a single first found non-null value from {@link Iterable}.
     * Some exception is thrown if result iterable to get value from is null or has zero-size.
     */
    public static <T, R, V extends Iterable<R>> Function<T, R> getFromIterable(Function<T, V> function,
                                                                               Supplier<? extends RuntimeException> exceptionSupplier) {
        return singleFromIterable(ToGetConditionalHelper.checkFunction(function), (Predicate<? super R>) ToGetConditionalHelper.AS_IS, null, null,
                ToGetConditionalHelper.checkExceptionSupplier(exceptionSupplier));
    }

    /**
     * This method returns a function. The result function returns a single first found value which
     * suits criteria from {@link Iterable}.
     *
     * @param function     function which should return {@link Iterable}
     * @param condition    predicate which is used to find some target value
     * @param waitingTime  is a duration of the waiting for valuable result
     * @param sleepingTime is a duration of the sleeping between attempts to get
     *                     expected valuable result
     * @param <T>          is a type of input value
     * @param <R>          is a type of the target value
     * @param <V>          is a type of {@link Iterable} of {@code R}
     * @return a function. The result function returns a single first found value from {@link Iterable}.
     * It returns a value if something that suits criteria is found. {@code null} is returned if
     * result iterable to get value from is null or has zero-size or it has no item which suits criteria.
     */
    public static <T, R, V extends Iterable<R>> Function<T, R> getFromIterable(Function<T, V> function,
                                                                               Predicate<? super R> condition,
                                                                               Duration waitingTime,
                                                                               Duration sleepingTime) {
        return singleFromIterable(ToGetConditionalHelper.checkFunction(function), ToGetConditionalHelper.checkCondition(condition), ToGetConditionalHelper.checkWaitingTime(waitingTime),
                ToGetConditionalHelper.checkSleepingTime(sleepingTime), null);
    }

    /**
     * This method returns a function. The result function returns a single first found value from {@link Iterable}.
     *
     * @param function     function which should return {@link Iterable}
     * @param waitingTime  is a duration of the waiting for valuable result
     * @param sleepingTime is a duration of the sleeping between attempts to get
     *                     expected valuable result
     * @param <T>          is a type of input value
     * @param <R>          is a type of the target value
     * @param <V>          is a type of {@link Iterable} of {@code R}
     * @return a function. The result function returns a single first found non-null value from {@link Iterable}.
     * {@code null} is returned if result iterable to get value from is null or has zero-size.
     */
    public static <T, R, V extends Iterable<R>> Function<T, R> getFromIterable(Function<T, V> function,
                                                                               Duration waitingTime,
                                                                               Duration sleepingTime) {
        return singleFromIterable(ToGetConditionalHelper.checkFunction(function), (Predicate<? super R>) ToGetConditionalHelper.AS_IS, ToGetConditionalHelper.checkWaitingTime(waitingTime),
                ToGetConditionalHelper.checkSleepingTime(sleepingTime), null);
    }

    /**
     * This method returns a function. The result function returns a single first found value which
     * suits criteria from {@link Iterable}.
     *
     * @param function    function which should return {@link Iterable}
     * @param condition   predicate which is used to find some target value
     * @param waitingTime is a duration of the waiting for valuable result
     * @param <T>         is a type of input value
     * @param <R>         is a type of the target value
     * @param <V>         is a type of {@link Iterable} of {@code R}
     * @return a function. The result function returns a single first found value from {@link Iterable}.
     * It returns a value if something that suits criteria is found. {@code null} is returned if
     * result iterable to get value from is null or has zero-size or it has no item which suits criteria.
     */
    public static <T, R, V extends Iterable<R>> Function<T, R> getFromIterable(Function<T, V> function,
                                                                               Predicate<? super R> condition,
                                                                               Duration waitingTime) {
        return singleFromIterable(ToGetConditionalHelper.checkFunction(function), ToGetConditionalHelper.checkCondition(condition), ToGetConditionalHelper.checkWaitingTime(waitingTime),
                null, null);
    }

    /**
     * This method returns a function. The result function returns a single first found value from {@link Iterable}.
     *
     * @param function    function which should return {@link Iterable}
     * @param waitingTime is a duration of the waiting for valuable result
     * @param <T>         is a type of input value
     * @param <R>         is a type of the target value
     * @param <V>         is a type of {@link Iterable} of {@code R}
     * @return a function. The result function returns a single first found non-null value from {@link Iterable}.
     * {@code null} is returned if result iterable to get value from is null or has zero-size.
     */
    public static <T, R, V extends Iterable<R>> Function<T, R> getFromIterable(Function<T, V> function,
                                                                               Duration waitingTime) {
        return singleFromIterable(ToGetConditionalHelper.checkFunction(function), (Predicate<? super R>) ToGetConditionalHelper.AS_IS, ToGetConditionalHelper.checkWaitingTime(waitingTime),
                null, null);
    }

    /**
     * This method returns a function. The result function returns a single first found value which
     * suits criteria from {@link Iterable}.
     *
     * @param function  function which should return {@link Iterable}
     * @param condition predicate which is used to find some target value
     * @param <T>       is a type of input value
     * @param <R>       is a type of the target value
     * @param <V>       is a type of {@link Iterable} of {@code R}
     * @return a function. The result function returns a single first found value from {@link Iterable}.
     * It returns a value if something that suits criteria is found. {@code null} is returned if
     * result iterable to get value from is null or has zero-size or it has no item which suits criteria.
     */
    public static <T, R, V extends Iterable<R>> Function<T, R> getFromIterable(Function<T, V> function,
                                                                               Predicate<? super R> condition) {
        return singleFromIterable(ToGetConditionalHelper.checkFunction(function), ToGetConditionalHelper.checkCondition(condition), null,
                null, null);
    }

    /**
     * This method returns a function. The result function returns a single first found value from {@link Iterable}.
     *
     * @param function function which should return {@link Iterable}
     * @param <T>      is a type of input value
     * @param <R>      is a type of the target value
     * @param <V>      is a type of {@link Iterable} of {@code R}
     * @return a function. The result function returns a single first found non-null value from {@link Iterable}.
     * {@code null} is returned if result iterable to get value from is null or has zero-size.
     */
    public static <T, R, V extends Iterable<R>> Function<T, R> getFromIterable(Function<T, V> function) {
        return singleFromIterable(ToGetConditionalHelper.checkFunction(function), (Predicate<? super R>) ToGetConditionalHelper.AS_IS, null, null, null);
    }
}
