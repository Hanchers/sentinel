package com.hancher.sentinel.web.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
/**
 * 通用key对象
 * @date 2025-07-03 18:15:08
 * @author hancher
 * @since 1.0
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Data
public class SentinelKey {
    private String id;
    private String value;
    private String text;
}
