package top.kloping.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author github kloping
 * @date 2025/4/22-13:03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TravelDto {
    private Integer id;
    private String name;
    private String desc;
    private Integer cost;
    private Integer reqLevel;
    private Integer reqPetLevel;
}
