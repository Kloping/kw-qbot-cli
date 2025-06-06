package top.kloping.controller;

import com.alibaba.fastjson.JSONObject;
import io.github.kloping.judge.Judge;
import io.github.kloping.spt.annotations.Action;
import io.github.kloping.spt.annotations.AutoStand;
import io.github.kloping.spt.annotations.Controller;
import io.github.kloping.spt.annotations.Param;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import top.kloping.api.KwGameApi;
import top.kloping.api.KwGameConvertApi;
import top.kloping.api.KwGameSkillApi;
import top.kloping.api.dto.DataWithTips;
import top.kloping.api.dto.EquipPet;
import top.kloping.api.dto.PetWithImage;
import top.kloping.api.entity.Pet;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static top.kloping.api.KwGameApi.getProgressBar;

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
            if (st != null) {
                Integer target = 1;
                if (split.length > 1) target = api.getIntegerOrDefault(split[1], 1);
                ResponseEntity<String> data = api.use(pid, st, target);
                if (data.getStatusCode().value() == 200) {
                    DataWithTips tips = api.convertT(data, DataWithTips.class);
                    JSONObject jo = (JSONObject) tips.getData();
                    return tips.getTips() + "\n 当前技能点剩余: "
                            + KwGameApi.getProgressBar(jo.getInteger("skp"), 5, 5, "○", "●");
                } else return data.getBody();
            }
        }
        return "❌ 格式错误\n示例: 技能1x2\n表示为向的对局2号为释放技能1";
    }

    @Action("放弃")
    public Object giveUp(Long pid) {
        ResponseEntity<String> entity = api.giveUp(pid);
        return entity.getBody();
    }

    public static String[] PARTS = {"头", "脚", "胸", "腿"};

    @Action("宠物装备")
    public Object equip(Long pid) {
        ResponseEntity<String> entity = api.equips(pid);
        if (entity.getStatusCode().value() != 200) return entity.getBody();
        EquipPet equipPet = api.convertT(entity, EquipPet.class);
        List<Object> list = new ArrayList<>();
        byte[] bytes = api.src(equipPet.getPetId(), equipPet.getLevel()).getBody();
        list.add(bytes);
        StringBuilder sb = new StringBuilder("宠物装备↓↓↓");
        int n = 0;
        if (equipPet.getEffects() != null && !equipPet.getEffects().isEmpty()) {
            sb.append("\n已激活套装效果:");
            for (String effect : equipPet.getEffects()) {
                sb.append("\n").append(effect);
            }
        }
        for (EquipPet.EquipData equipData : equipPet.getEquipData()) {
            if (equipData.getType() == null) {
                if (n == 0) sb.append("\n---------------");
                sb.append("\n[").append(PARTS[n++]).append("]部分: ");
                if (equipData.getId() == null) sb.append("无");
                else sb.append(equipData.getId()).append(".").append(equipData.getName())
                        .append(equipData.getDesc());
            }
        }
        list.add(sb);
        list.add(Map.of(1, "宠物信息", 2, "背包", 3, "装备背包", 4, "宠物技能"));
        return list;
    }

    @Action("宠物技能")
    public Object equipSkills(Long pid) {
        ResponseEntity<String> entity = api.equips(pid);
        if (entity.getStatusCode().value() != 200) return entity.getBody();
        EquipPet equipPet = api.convertT(entity, EquipPet.class);
        List<Object> list = new ArrayList<>();
        byte[] bytes = api.src(equipPet.getPetId(), equipPet.getLevel()).getBody();
        list.add(bytes);
        StringBuilder sb = new StringBuilder("宠物技能↓↓↓");
        int i = 1;
        for (EquipPet.EquipData equipData : equipPet.getEquipData()) {
            if (equipData.getType() != null) {
                sb.append("\n技能").append(i++).append(": ").append(equipData.getName())
                        .append(" [").append(equipData.getType()).append("]技能")
                        .append("\n\t ").append(equipData.getDesc());
            }
        }
        list.add(sb);
        list.add(Map.of(1, "宠物信息", 2, "背包", 3, "装备背包", 4, "宠物装备"));
        return list;
    }

    @Action("当前信息<.*?=>x>")
    public Object info(Long id) {
        ResponseEntity<String> response = api.currentInfo(id);
        if (response.getStatusCode().value() == 200) {
            PetWithImage petw = api.convertT(response, PetWithImage.class);
            String base64 = petw.getData();
            byte[] bytes = Base64.getDecoder().decode(base64);
            Pet pet = petw.getPet();
            StringBuilder sb = new StringBuilder("对局信息\n");
            return getPetInfoPre(bytes, pet, sb);
        } else return response.getBody();
    }

    @NotNull
    public static Object getPetInfoPre(byte[] bytes, Pet pet, StringBuilder sb) {
        sb.append("🐾名字: ").append(pet.getName()).append("\n");
        sb.append("🔮类型: ").append(pet.getType()).append("\n");
        sb.append("⭐等级: ").append(pet.getLevel()).append("\n");
        sb.append("📈经验: ").append(pet.getExperience()).append("/").append(pet.getRequiredExp()).append("\n");
        sb.append(getProgressBar(pet.getExperience().intValue(), pet.getRequiredExp().intValue(), 10, "⬜", "🟦")).append("\n");
        sb.append("❤️血量: ").append(pet.getCurrentHp()).append("/").append(pet.getHp()).append("\n");
        sb.append(getProgressBar(pet.getCurrentHp(), pet.getHp(), 10, "⬜", "🟩")).append("\n");
        sb.append("🏃速度: ").append(pet.getSpeed()).append("\n");
        sb.append("⚔️攻击: ").append(pet.getAttack()).append("\n");
        sb.append("🛡️防御: ").append(pet.getDefense()).append("\n");
        sb.append("🎯暴率: ").append(pet.getCritRate()).append("%\n");
        sb.append("💥暴伤: ").append(pet.getCritDamage()).append("%");
        return List.of(bytes, sb, Map.of(1, "宠物装备", 2, "背包", 3, "等级突破", 4, "装备背包"));
    }
}
