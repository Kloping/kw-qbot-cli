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
public class DataWithTips {
    private String tips;
    private Object data;

    @Override
    public String toString() {
        return getData() + "\ntips:" + getTips();
    }
}
