package com.hancher.sentinel.core.processor.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Builder
@Accessors(chain = true)
@Data
public class Result {
    private boolean success;
    private Object output;


    /**
     * 创建成功结果
     *
     * @param output 返回内容
     * @return result
     */
    public static Result success(Object output) {
        return Result.builder()
                .success(true)
                .output(output)
                .build();
    }

    /**
     * 创建失败结果
     *
     * @param output 返回内容
     * @return result
     */
    public static Result fail(String output) {
        return Result.builder()
                .success(false)
                .output(output)
                .build();
    }
}
