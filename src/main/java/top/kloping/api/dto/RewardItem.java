package top.kloping.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author github kloping
 * @date 2025/4/24-20:41
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RewardItem {
    private Long pid;
    private boolean win;
    private String tips;
    private List<int[]> idcount = new ArrayList<>();
}
