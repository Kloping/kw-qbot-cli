package top.kloping.controller;

import com.alibaba.fastjson.JSONObject;
import io.github.kloping.judge.Judge;
import io.github.kloping.spt.annotations.Action;
import io.github.kloping.spt.annotations.AutoStand;
import io.github.kloping.spt.annotations.Controller;
import io.github.kloping.spt.annotations.Param;
import org.springframework.http.ResponseEntity;
import top.kloping.api.KwGameApi;
import top.kloping.api.KwGameConvertApi;
import top.kloping.api.KwGameSkillApi;
import top.kloping.api.dto.DataWithTips;
import top.kloping.api.dto.EquipPet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author github kloping
 * @date 2025/4/24-22:08
 */
@Controller
public class SkillController {
    @AutoStand
    private KwGameSkillApi api;

    @AutoStand
    private KwGameConvertApi convertApi;

    @Action("技能<.*?=>x>")
    public Object skill(Long pid, @Param("x") String s) {
        if (!Judge.isEmpty(s)) {
            String[] split = s.split("[xX]");
            Integer st = api.getIntegerOrDefault(split[0], null);
            Integer target = 1;
            if (split.length > 1) {
                target = api.getIntegerOrDefault(split[1], 1);
            }
            ResponseEntity<String> data = api.use(pid, st, target);
            if (data.getStatusCode().value() == 200) {
                DataWithTips tips = api.convertT(data, DataWithTips.class);
                JSONObject jo = (JSONObject) tips.getData();
                return tips.getTips() + "\n 当前技能点剩余: "
                        + KwGameApi.getProgressBar(jo.getInteger("skp"), 5, 5, "○", "●");
            } else return data.getBody();
        }
        return "❌ 格式错误\n示例: 技能1x2\n表示为向的对局2号为释放技能1";
    }

    @Action("放弃")
    public Object giveUp(Long pid) {
        ResponseEntity<String> entity = api.giveUp(pid);
        return entity.getBody();
    }

    @Action("宠物装备")
    public Object equip(Long pid) {
        ResponseEntity<String> entity = api.equips(pid);
        if (entity.getStatusCode().value() != 200) return entity.getBody();
        EquipPet equipPet = api.convertT(entity, EquipPet.class);
        List<Object> list = new ArrayList<>();
        byte[] bytes = api.src(equipPet.getPetId(), equipPet.getLevel()).getBody();
        list.add(bytes);
        String name = convertApi.toName(equipPet.getPetId());
        StringBuilder sb = new StringBuilder(name);
        int i = 1;
        for (EquipPet.EquipData equipData : equipPet.getEquipData()) {
            sb.append("\n技能").append(i++).append(": ").append(equipData.getName())
                    .append(" [").append(equipData.getType()).append("]技能")
                    .append("\n\t ").append(equipData.getDesc());
        }
        list.add(sb);
        list.add(Map.of(1, "宠物信息", 2, "背包"));
        return list;
    }
}
