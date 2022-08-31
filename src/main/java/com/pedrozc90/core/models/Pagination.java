package com.pedrozc90.core.models;

import com.pedrozc90.core.querydsl.JPAQuery;

import java.util.List;

public class Pagination {

    public static <T> Page<T> fetch(final JPAQuery<T> query, final int page, final int rpp) {
        final long total = query.fetchCount();
        final List<T> list = query.limit(rpp + 1)
            .offset((page - 1L) * rpp)
            .fetch();
        boolean next = false;
        if (list.size() > rpp) {
            list.remove(list.size() - 1);
            next = true;
        }
        return new Page<T>(page, rpp, list, page > 1, next, total);
    }

}
