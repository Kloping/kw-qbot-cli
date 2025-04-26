package top.kloping.api.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author github kloping
 * @date 2025/4/25-13:35
 */
@Data
public class EquipPet {
    private Long pid;
    private Integer petId;
    private Integer level;
    private List<EquipData> equipData = new ArrayList<>();


    @Data
    public static class EquipData {
        private Integer id;
        private String type = "skill";
        private String name;
        private String desc;
    }
}
