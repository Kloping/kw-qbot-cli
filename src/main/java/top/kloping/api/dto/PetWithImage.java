package top.kloping.api.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import top.kloping.api.entity.Pet;

/**
 * @author github kloping
 * @date 2025/4/16-00:03
 */
@Data
@Accessors(chain = true)
public class PetWithImage {
    private Pet pet;
    private String data;
}
