package top.kloping.api.dto;

import lombok.Data;

/**
 * @author github kloping
 * @date 2025/5/17-18:25
 */
@Data
public class TopicData {
    private Long pid;
    private String tips;
    private Integer status = 0;
}
