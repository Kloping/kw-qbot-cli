package top.kloping.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.github.kloping.judge.Judge;
import io.github.kloping.spt.annotations.Action;
import io.github.kloping.spt.annotations.AutoStand;
import io.github.kloping.spt.annotations.Controller;
import io.github.kloping.spt.annotations.Param;
import io.github.kloping.spt.interfaces.Logger;
import net.mamoe.mirai.message.data.Message;
import org.springframework.http.ResponseEntity;
import top.kloping.StringUtils;
import top.kloping.api.KwGameConvertApi;
import top.kloping.api.KwGameItemApi;
import top.kloping.api.SrcRegistry;
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
    KwGameItemApi api;

    @AutoStand
    SelectController selectController;

    @Action("背包")
    public Object list(Long id) {
        ResponseEntity<String> data = api.list(id);
        if (data.getStatusCode().value() != 200) return data.getBody();
        JSONArray array = JSON.parseArray(data.getBody());
        StringBuilder sb = new StringBuilder("🎒 背包 🎒\n🆔.物品名x数量\n");
        Map<Integer, String> map = new HashMap<>();
        int i = 1;
        for (Object o : Objects.requireNonNull(array)) {
            JSONObject jo = (JSONObject) o;
            ItemWithName iw = jo.toJavaObject(ItemWithName.class);
            int n = iw.getQuantity();
            sb.append(iw.getSpeciesId()).append(".").append(StringUtils.padChineseString(iw.getName()));
            if (n > 1) sb.append("✖️").append(n);
            sb.append("\n");
            if (i <= 2) map.put(i++, "使用" + iw.getName());
            else if (i <= 4) map.put(i++, "出售" + iw.getName());
        }
        return List.of(sb.toString().trim(), map);
    }

    @Action("商城")
    public String shop(Long id) {
        ResponseEntity<String> data = api.shop(id);
        if (data.getStatusCode().value() != 200) return data.getBody();
        List<ItemForShop> shops = api.convertTs(data, ItemForShop.class);
        StringBuilder sb = new StringBuilder("🛒 商城 🛒\n");
        sb.append("🆔 .物品名\t\t\t💰单价\t\t\t限购\n");
        for (ItemForShop shop : shops) {
            sb.append(shop.getSpeciesId()).append(".").append(StringUtils.padChineseString(shop.getName()));
            sb.append("\t\t").append(StringUtils.padNumberString(shop.getPrice()))
                    .append("/个").append("\t\t📦剩").append(shop.getCount()).append("\n");
        }
        return sb.toString();
    }

    @Action("出售<.*?=>x>")
    public String sell(Long id, @Param("x") String s) {
        if (!Judge.isEmpty(s)) {
            String[] split = s.split("[xX]");
            Integer itemId = api.getIdOrDefault(split[0], null);
            Integer count = 1;
            if (split.length > 1) count = api.getIntegerOrDefault(split[1], count);
            if (itemId != null) {
                ResponseEntity<String> data = api.sell(id, itemId, count);
                return data.getBody();
            }
        }
        return "❌ 格式错误\n🛒 出售示例'出售1001x2'或'出售经验书x2'\n表示为出售2个经验本";
    }

    @Action("使用<.*?=>x>")
    public String use(Long id, @Param("x") String s) {
        if (!Judge.isEmpty(s)) {
            String[] split = s.split("[xX]");
            Integer itemId = api.getIdOrDefault(split[0], null);
            Integer count = 1;
            if (split.length > 1) count = api.getIntegerOrDefault(split[1], count);
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
            if (split.length > 1) count = api.getIntegerOrDefault(split[1], count);
            if (itemId != null) {
                ResponseEntity<String> data = api.buy(id, itemId, count);
                return data.getBody();
            }
        }
        return "❌ 格式错误\n🛒 购买示例'购买1001x2'或'购买经验书x2'\n表示为购买2个经验本";
    }

    @AutoStand
    SrcRegistry registry;

    @AutoStand
    KwGameConvertApi convertApi;

    @Action("说明<.*?=>x>")
    public Object explain(Long id, @Param("x") String s) {
        if (!Judge.isEmpty(s)) {
            Integer itemId = api.getIdOrDefault(s, null);
            if (itemId == null) {
                return convertApi.desc(s);
            } else {
                ResponseEntity<String> data = convertApi.desc(itemId);
                if (data.getStatusCode().value() == 200) {
                    Message image = registry.getImage(itemId);
                    if (image != null) return List.of(image, data.getBody());
                    return List.of(data.getBody());
                } else {
                    return "❌ 物品不存在";
                }
            }
        }
        return "❌ 格式错误\n🛒 购买示例'说明1001'或'说明经验书'";
    }

    //给予
    @Action("给予<.*?=>x>")
    public String give(Long id, @Param("x") String s) {
        if (!Judge.isEmpty(s)) {
            String[] split = s.split("[xX]");
            Integer itemId = api.getIdOrDefault(split[0], null);
            Integer count = 1;
            if (split.length > 1) count = api.getIntegerOrDefault(split[1], count);
            if (itemId != null) {
                ResponseEntity<String> data = api.give(id, itemId, count);
                if (data.getStatusCode().value() == 200) {
                    return data.getBody();
                } else return "❌ " + data.getBody();
            }
        }
        return "❌ 组队后确保队伍中有且仅有2人时可给予\n给予用法:‘给予1001x2’或‘给予经验本x2’";
    }

    @AutoStand
    Logger logger;

    //提交对接
    @Action("提交<.*?=>x>")
    public Object submit(Long id, @Param("x") String s) {
        if (!Judge.isEmpty(s)) {
            String[] split = s.split("[xX]");
            Integer itemId = api.getIdOrDefault(split[0], null);
            Integer count = 1;
            if (split.length > 1) count = api.getIntegerOrDefault(split[1], count);
            if (itemId != null) {
                ResponseEntity<String> data = api.submit(id, itemId, count);
                logger.log("req submit out: " + JSON.toJSONString(data));
                if (data.getBody() != null) return data.getBody();
            } else return "❌ 格式错误(未找到相关物品)\n提交示例'提交1001'或'提交苹果x3'";
        } else return "❌ 格式错误\n提交示例'提交1001'或'提交苹果x3'";
        return null;
    }
}