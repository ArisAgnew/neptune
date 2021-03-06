package ru.tinkoff.qa.neptune.core.api.utils;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Map.entry;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public final class ConstructorUtil {

    private static final Map<Class<?>, Class<?>> FOR_USED_SIMPLE_TYPES =
            Map.ofEntries(entry(Integer.class, int.class),
                    entry(Long.class, long.class),
                    entry(Boolean.class, boolean.class),
                    entry(Byte.class, byte.class),
                    entry(Short.class, short.class),
                    entry(Float.class, float.class),
                    entry(Double.class, double.class),
                    entry(Character.class, char.class));

    private ConstructorUtil() {
        super();
    }

    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> findSuitableConstructor(Class<T> clazz, Object...params) throws Exception {
        var constructorList = asList(clazz.getDeclaredConstructors());
        final List<Class<?>> paramTypes = Arrays.stream(params).map(o -> ofNullable(o)
                .map(Object::getClass)
                .orElse(null))
                .collect(toList());

        var foundConstructor = constructorList.stream().filter(constructor -> {
            var constructorTypes = asList(constructor.getParameterTypes());
            return constructorTypes.size() == paramTypes.size() && matches(constructorTypes, paramTypes);
        })
                .findFirst().orElseThrow(() -> new NoSuchMethodException(
                        format("There is no constructor that convenient to parameter list %s", paramTypes)));
        foundConstructor.setAccessible(true);
        return (Constructor<T>) foundConstructor;
    }

    private static boolean matches(List<Class<?>> constructorTypes,
                                   List<Class<?>> paramTypes) {
        var i = -1;
        for (Class<?> parameter : constructorTypes) {
            i++;
            var currentType = paramTypes.get(i);
            if (isNull(currentType) && nonNull(FOR_USED_SIMPLE_TYPES.get(parameter))) {
                return false;
            }
            else if (currentType == null){
                continue;
            }

            if (parameter.isAssignableFrom(currentType)) {
                continue;
            }

            Class<?> simple;
            if (nonNull(simple = FOR_USED_SIMPLE_TYPES.get(currentType)) &&
                    parameter.isAssignableFrom(simple)) {
                continue;
            }

            var declaredArrayType = parameter.getComponentType();
            var currentArrayType = currentType.getComponentType();
            if (nonNull(declaredArrayType) && nonNull(currentArrayType) &&
                    declaredArrayType.isAssignableFrom(currentArrayType)) {
                continue;
            }
            return false;
        }
        return true;
    }
}
