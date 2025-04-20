package top.kloping.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author github kloping
 * @date 2025/4/16-10:12
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode
public class ItemForShop extends ItemWithName {
    //描述
    private String desc;
    //价格
    private Integer price;
    //剩余购买次数
    private Integer count;
}
