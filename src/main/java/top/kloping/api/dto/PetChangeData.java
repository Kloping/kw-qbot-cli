package top.kloping.api.dto;

import lombok.Data;

/**
 * @author github kloping
 * @date 2025/4/18-20:57
 */
@Data
public class PetChangeData {
    private Long id;
    private Integer petId;
    private String name;

    private Boolean isChange;

    private Integer level;
    private Integer exp;
    private Integer hp;
    private Integer attack;
    private Integer defense;
    private Integer speed;
}
