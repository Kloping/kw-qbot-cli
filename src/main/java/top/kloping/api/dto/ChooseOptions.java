package top.kloping.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author github kloping
 * @date 2025/4/23-18:03
 */
@Data
public class ChooseOptions {
    private Long pid;
    private String tips;
    private List<Option> list = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Option {
        private String name;
        private String url;
    }
}
