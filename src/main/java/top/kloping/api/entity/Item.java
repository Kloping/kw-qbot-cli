package top.kloping.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 玩家背包物品
 *
 * @author kloping
 * @since 2025-04-18 14:59
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    private Integer id;

    private Long playerId;

    private Integer speciesId;

    private Integer quantity;
}
