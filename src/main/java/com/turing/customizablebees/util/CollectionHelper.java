package com.turing.customizablebees.util;

import java.util.Comparator;

public class CollectionHelper {
    public static <T> Comparator<T> checkEqualThen(Comparator<T> comparator) {
        return (a, b) -> {
            if (a == b) return 0;
            return comparator.compare(a, b);
        };
    }
}
