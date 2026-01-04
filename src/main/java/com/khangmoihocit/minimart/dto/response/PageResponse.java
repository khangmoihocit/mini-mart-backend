package com.khangmoihocit.minimart.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageResponse<T> {
    List<T> content;
    int pageNo;
    int pageSize;
    long totalElements;
    int totalPages;
    boolean last;
    boolean first;

    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .pageNo(page.getNumber() + 1) // Convert 0-based to 1-based
                .pageSize(page.getSize() == Integer.MAX_VALUE ? (int) page.getTotalElements() : page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .first(page.isFirst())
                .build();
    }
}

