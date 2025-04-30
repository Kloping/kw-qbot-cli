package top.kloping.api.dto;

import lombok.Data;

/**
 * @author github kloping
 * @date 2025/4/30-20:34
 */
@Data
public class EquipsPrePage {
    private EquipsPre[] equips;
    private Integer cn;
    private Integer count;
}
