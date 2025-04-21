package top.kloping.controller;

import com.alibaba.fastjson.JSONObject;
import io.github.kloping.spt.annotations.Action;
import io.github.kloping.spt.annotations.AutoStand;
import io.github.kloping.spt.annotations.Controller;
import org.springframework.http.ResponseEntity;
import top.kloping.api.KwGameExplorationApi;
import top.kloping.api.KwGameItemApi;
import top.kloping.api.dto.DataWithTips;

import java.util.List;
import java.util.Map;

/**
 * @author github kloping
 * @date 2025/4/20-13:56
 */
@Controller
public class ExplorationController {
    @AutoStand
    KwGameExplorationApi api;

    @AutoStand
    KwGameItemApi itemApi;

    @Action("游历")
    public Object explore(Long id) {
        ResponseEntity<String> data = api.explore(id);
        if (data.getStatusCode().value() == 200) {
            DataWithTips dataWithTips = api.convertT(data, DataWithTips.class);
            JSONObject jo = (JSONObject) dataWithTips.getData();
            Integer itemId = jo.getInteger("id");
            if (itemId != null) {
                byte[] bytes = itemApi.src(itemId).getBody();
                return List.of(bytes, dataWithTips.getTips(), Map.of(1, "背包", 2, "信息", 3, "游历", 4, "宠物信息"));
            } else {
                return dataWithTips.getTips();
            }
        } else {
            return data.getBody();
        }
    }
}
