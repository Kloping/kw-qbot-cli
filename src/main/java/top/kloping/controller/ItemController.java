package top.kloping.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.github.kloping.judge.Judge;
import io.github.kloping.spt.annotations.Action;
import io.github.kloping.spt.annotations.AutoStand;
import io.github.kloping.spt.annotations.Controller;
import io.github.kloping.spt.annotations.Param;
import org.springframework.http.ResponseEntity;
import top.kloping.api.KwGameItemtApi;
import top.kloping.api.dto.ItemForShop;
import top.kloping.api.dto.ItemWithName;
import top.kloping.api.dto.UseResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author github kloping
 * @date 2025/4/20-17:32
 */
@Controller
public class ItemController {
    @AutoStand
    KwGameItemtApi api;

    @AutoStand
    SelectController selectController;

    @Action("背包")
    public Object list(Long id) {
        ResponseEntity<String> data = api.list(id);
        JSONArray array = JSON.parseArray(data.getBody());
        StringBuilder sb = new StringBuilder("🎒 背包 🎒\n🆔.物品名x数量\n");
        Map<Integer, String> map = new HashMap<>();
        int i = 1;
        for (Object o : Objects.requireNonNull(array)) {
            JSONObject jo = (JSONObject) o;
            ItemWithName iw = jo.toJavaObject(ItemWithName.class);
            int n = iw.getQuantity();
            sb.append(iw.getSpeciesId()).append(".").append(iw.getName());
            if (n > 1) sb.append("✖️").append(n);
            sb.append("\n");
            if (i <= 4) map.put(i++, "使用" + iw.getName());
        }
        return List.of(sb, map);
    }

    @Action("商城")
    public String shop(Long id) {
        ResponseEntity<String> data = api.shop(id);
        List<ItemForShop> shops = api.convertTs(data, ItemForShop.class);
        StringBuilder sb = new StringBuilder("🛒 商城 🛒\n");
        sb.append("🆔 .物品名\t\t💰单价\t\t限购\n");
        for (ItemForShop shop : shops) {
            sb.append(shop.getSpeciesId()).append(".").append(shop.getName());
            sb.append("\t\t").append(shop.getPrice()).append("/个").append("\t\t📦剩")
                    .append(shop.getCount()).append("\n");
        }
        return sb.toString();
    }

    @Action("使用<.*?=>x>")
    public String use(Long id, @Param("x") String s) {
        if (!Judge.isEmpty(s)) {
            String[] split = s.split("[xX]");
            Integer itemId = api.getIdOrDefault(split[0], null);
            Integer count = 1;
            if (split.length > 1) count = api.getIdOrDefault(split[1], count);
            if (itemId != null) {
                ResponseEntity<String> data = api.use(id, itemId, count);
                UseResult result = api.convertT(data, UseResult.class);
                if (result.isSuccess()) {
                    return result.getName() + " " + result.getData();
                } else {
                    return result.getData().toString();
                }
            }
        }
        return "🎮 使用示例'使用1001x2'或'使用经验本x2'\n表示为使用2个经验本(对置顶宠物使用)";
    }

    @Action("购买<.*?=>x>")
    public String buy(Long id, @Param("x") String s) {
        if (!Judge.isEmpty(s)) {
            String[] split = s.split("[xX]");
            Integer itemId = api.getIdOrDefault(split[0], null);
            Integer count = 1;
            if (split.length > 1) count = api.getIdOrDefault(split[1], count);
            if (itemId != null) {
                ResponseEntity<String> data = api.buy(id, itemId, count);
                if (data.getStatusCode().value() == 200) {
                    return "✅ 购买成功";
                }
                return data.getBody();
            }
        }
        return "❌ 格式错误\n🛒 购买示例'购买1001x2'或'购买经验书x2'\n表示为购买2个经验本";
    }

    @Action("说明<.*?=>x>")
    public Object explain(Long id, @Param("x") String s) {
        if (!Judge.isEmpty(s)) {
            Integer itemId = api.getIdOrDefault(s, null);
            ResponseEntity<String> data = api.desc(itemId);
            if (data.getStatusCode().value() == 200) {
                ResponseEntity<byte[]> dab = api.src(itemId);
                return List.of(dab.getBody(), data.getBody());
            } else {
                return "❌ 物品不存在";
            }
        }
        return "❌ 格式错误\n🛒 购买示例'说明1001'或'说明经验书'";
    }
}