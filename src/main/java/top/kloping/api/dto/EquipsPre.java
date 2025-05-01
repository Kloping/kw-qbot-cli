package top.kloping.api.dto;

import lombok.Data;

/**
 * @author github kloping
 * @date 2025/4/30-20:03
 */
@Data
public class EquipsPre {
    private Integer id;
    private Integer level;
    private String name;
    private Integer rarity;
    private String main;
    private String r1;
    private String r2;
    private String r3;

    public String toString(String tab) {
        return id + "." + name + " +" + level + "\n" + "主:" + main + "\n" + "⭐\uFE0F".repeat(rarity) + "\n" + tab + r1 + "\n" + tab + r2 + "\n" + tab + r3;
    }

}
