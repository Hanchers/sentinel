package com.hancher.sentinel.web.vo;

import com.mybatisflex.core.paginate.Page;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 统一分页返回参数
 * @date 2025-10-11 16:44:14
 * @author hancher
 * @since 1.0
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Data
public class PageInfo<E> {

    /**
     * 总页数
     */
    private Long pages;
    /**
     * 总行数
     */
    private Long rows;
    /**
     * 当前页
     */
    private Long currentPage;
    /**
     * 每页行数
     */
    private Long pageSize;
    /**
     * 数据列表
     */
    private List<E> list;



    public static <E> PageInfo<E> of(Page<E> page) {
        return PageInfo.<E>builder()
                .list(page.getRecords())
                .rows(page.getTotalRow())
                .currentPage(page.getPageNumber())
                .pageSize(page.getPageSize())
                .pages(page.getTotalPage())
                .build();
    }
}
