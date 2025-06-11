package top.kloping.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author github kloping
 * @date 2025/6/11-18:03
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Emails {
    private Integer id;

    private Long pid;
    private String content;
    private Integer oid;
    private String type;
    private Integer count;
    // 1 领取 0 未领
    private Integer get;
}
