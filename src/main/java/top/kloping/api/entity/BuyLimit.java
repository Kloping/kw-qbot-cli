package top.kloping.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author kloping
 * @since 2025-04-18 17:42
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class BuyLimit {

    private Integer id;

    private Long playerId;

    private Integer speciesId;

    private String mday;

    private Integer count;
}
