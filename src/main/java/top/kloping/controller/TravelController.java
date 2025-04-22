package top.kloping.controller;

import com.alibaba.fastjson.JSONObject;
import io.github.kloping.judge.Judge;
import io.github.kloping.spt.annotations.Action;
import io.github.kloping.spt.annotations.AutoStand;
import io.github.kloping.spt.annotations.Controller;
import io.github.kloping.spt.annotations.Param;
import org.springframework.http.ResponseEntity;
import top.kloping.api.KwGameItemApi;
import top.kloping.api.KwGameTravelApi;
import top.kloping.api.dto.DataWithTips;
import top.kloping.api.dto.TravelDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author github kloping
 * @date 2025/4/20-13:56
 */
@Controller
public class TravelController {
    @AutoStand
    KwGameTravelApi api;

    @AutoStand
    KwGameItemApi itemApi;

    @Action("游历<.*?=>x>")
    public Object explore(Long pid, @Param("x") String s) {
        if (!Judge.isEmpty(s)) {
            Integer id = api.getIdOrDefault(s, null);
            ResponseEntity<String> data = api.travel(pid, id);
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
        } else {
            Map<Integer, String> opt = new HashMap<>();
            StringBuilder sb = new StringBuilder();
            int n = 1;
            for (TravelDto location : api.locations()) {

                sb.append("\n").append(location.getId()).append("🏞️【").append(location.getName()).append("】")
                        .append("\n\t🔸地形特征：").append(location.getDesc())
                        .append("\n\t🔸体力消耗：").append(location.getCost()).append("点/次")
                        .append("\n\t🔸等级要求：Lv.").append(location.getReqLevel());

                if (n <= 4) opt.put(n++, "游历" + location.getId());
            }
            return List.of(sb, opt);
        }
    }
}
