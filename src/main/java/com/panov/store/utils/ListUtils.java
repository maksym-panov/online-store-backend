package com.panov.store.utils;

import java.util.List;

/**
 * Methods of this class are used to process lists.
 *
 * @author Maksym Panov
 * @version 1.0
 */
public class ListUtils {
    private ListUtils() {}

    /**
     * Use to skip some items and limit the size of the list.
     *
     * @param quantity if specified, the method will return only the first
     *                 {@code quantity} list elements.
     * @param offset if specified, the method will skip first {@code offset}
     *               list elements.
     * @return a shortened list.
     */
    public static <T> List<T> makeCut(List<T> list, Integer quantity, Integer offset) {
        if (quantity == null || quantity < 0)
            return list;
        if (offset == null || offset < 0)
            offset = 0;
        if (offset > list.size())
            offset = list.size();
        if (offset + quantity > list.size())
            quantity = list.size() - offset;
        return list.subList(offset, offset + quantity);
    }
}
