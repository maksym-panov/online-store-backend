package com.panov.store.utils;

import java.util.List;

public class ListUtils {
    private ListUtils() {}

    public static <T> List<T> makeCut(List<T> list, Integer quantity, Integer offset) {
        if (quantity == null)
            return list;
        if (offset == null)
            offset = 0;
        if (offset > list.size())
            offset = list.size();
        if (offset + quantity > list.size())
            quantity = list.size() - offset;
        return list.subList(offset, offset + quantity);
    }
}
