package com.pedrozc90.core.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pedrozc90.core.querydsl.JPAQuery;
import io.micronaut.core.annotation.Introspected;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Introspected
public class Page<E> implements Serializable {

    @JsonInclude(ALWAYS)
    @JsonProperty("page")
    private int page = 1;

    @JsonInclude(ALWAYS)
    @JsonProperty("rpp")
    private int rpp = 15;

    @JsonInclude(ALWAYS)
    @JsonProperty("list")
    private List<E> list = new ArrayList<>();

    @JsonInclude(ALWAYS)
    @JsonProperty("next")
    private boolean next = false;

    @JsonInclude(ALWAYS)
    @JsonProperty("prev")
    private boolean prev = false;

    @JsonInclude(ALWAYS)
    @JsonProperty("total")
    private long total = 0;

    public static <E> Page<E> create(final JPAQuery<E> query, final int page, final int rpp) {
        final long total = query.fetchCount();
        final List<E> list = query.limit(rpp + 1)
            .offset((page - 1L) * rpp)
            .fetch();
        boolean next = false;
        if (list.size() > rpp) {
            list.remove(list.size() - 1);
            next = true;
        }
        return new Page<E>(page, rpp, list, page > 1, next, total);
    }

}
