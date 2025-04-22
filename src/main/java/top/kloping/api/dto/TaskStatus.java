package top.kloping.api.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author github kloping
 * @date 2025/4/22-16:43
 */
@Data
@Accessors(chain = true)
public class TaskStatus {
    private Long playerId;
    private String name;
    private String desc;
    private String type;
    private Integer num;
    private Integer cnum;
    private String reward;
}
