package top.kloping.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import top.kloping.api.entity.Item;

/**
 * @author github kloping
 * @date 2025/4/16-10:12
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode
public class ItemWithName extends Item {
    private String name;
}
