package top.kloping.controller;

import com.alibaba.fastjson.JSONObject;
import io.github.kloping.judge.Judge;
import io.github.kloping.spt.annotations.Action;
import io.github.kloping.spt.annotations.AutoStand;
import io.github.kloping.spt.annotations.Controller;
import io.github.kloping.spt.annotations.Param;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import top.kloping.api.KwGameItemApi;
import top.kloping.api.KwGameTravelApi;
import top.kloping.api.SrcRegistry;
import top.kloping.api.dto.DataWithTips;
import top.kloping.api.dto.TravelDto;

import java.util.ArrayList;
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

    @AutoStand
    SrcRegistry registry;

    @Action("游历<.*?=>x>")
    public Object travel(Long pid, @Param("x") String s) {
        if (!Judge.isEmpty(s)) {
            Integer id = api.getIdOrDefault(s, null);
            ResponseEntity<String> data = api.travel(pid, id);
            if (data.getStatusCode().value() == 200) {
                DataWithTips dataWithTips = api.convertT(data, DataWithTips.class);
                JSONObject jo = (JSONObject) dataWithTips.getData();
                Integer itemId = jo.getInteger("id");
                if (itemId != null) {
                    return List.of(registry.getImage(itemId),
                            dataWithTips.getTips(), Map.of(1, "背包", 2, "信息", 3, "游历", 4, "宠物信息"));
                } else {
                    return dataWithTips.getTips();
                }
            } else {
                return data.getBody();
            }
        }
        return showLocations(1);
    }

    private @NotNull List<Object> showLocations(int i) {
        Map<Integer, String> opt = new HashMap<>();
        int n = 1;
        List<Object> list = new ArrayList<>();
        for (TravelDto location : i == 1 ? api.locations() : api.locations2()) {
            String sb = "\n" + location.getId() + "🏞️【" + location.getName() + "】" +
                    "\n\t🔸地形特征：" + location.getDesc() +
                    "\n\t🔸体力消耗：" + location.getCost() + "点/次" +
                    "\n\t🔸玩家等级：Lv." + location.getReqLevel() +
                    "\n\t🔸宠物等级：Lv." + location.getReqPetLevel() + "\n";
            list.add(registry.getImage(location.getId()));
            list.add(sb);
            if (n <= 4) opt.put(n++, (i == 1 ? "游历" : "探索") + location.getId());
        }
        list.add(opt);
        return list;
    }

    @Action("探索<.*?=>x>")
    public Object explore(Long pid, @Param("x") String s) {
        if (!Judge.isEmpty(s)) {
            Integer id = api.getIdOrDefault(s, null);
            ResponseEntity<String> data = api.explore(pid, id);
            if (data.getStatusCode().value() == 200) {
                DataWithTips dataWithTips = api.convertT(data, DataWithTips.class);
                JSONObject jo = (JSONObject) dataWithTips.getData();
                Integer itemId = jo.getInteger("id");
                if (itemId != null) {
                    return List.of(registry.getImage(itemId), dataWithTips.getTips(), Map.of(1, "信息",
                                    2, "装备背包", 3, "探索", 4, "宠物信息"));
                } else {
                    return dataWithTips.getTips();
                }
            } else {
                return data.getBody();
            }
        }
        return showLocations(2);
    }
}
