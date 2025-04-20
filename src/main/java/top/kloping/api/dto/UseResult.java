package top.kloping.api.dto;

import lombok.Data;

/**
 * 物品使用结果
 *
 * @author github kloping
 * @date 2025/4/18-20:11
 */
@Data
public class UseResult {
    private boolean success;
    private String name;
    private String field;
    private Object data;

    public UseResult() {
    }

    public UseResult(boolean b, String s) {
        this.success = b;
        this.data = s;
    }

}
