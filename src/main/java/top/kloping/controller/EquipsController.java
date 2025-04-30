package top.kloping.controller;

import io.github.kloping.judge.Judge;
import io.github.kloping.spt.annotations.Action;
import io.github.kloping.spt.annotations.AutoStand;
import io.github.kloping.spt.annotations.Controller;
import io.github.kloping.spt.annotations.Param;
import org.springframework.http.ResponseEntity;
import top.kloping.api.KwGameEquipsApi;
import top.kloping.api.dto.EquipsPre;
import top.kloping.api.dto.EquipsPrePage;

/**
 * @author github kloping
 * @date 2025/4/20-17:32
 */
@Controller
public class EquipsController {
    @AutoStand
    KwGameEquipsApi api;

    @Action("装备背包<.*?=>x>")
    public String give(Long id, @Param("x") String s) {
        Integer cn = api.getIntegerOrDefault(s, 1);
        ResponseEntity<String> data = api.bag(id, cn);
        if (data != null && data.getStatusCode().value() == 200) {
            EquipsPrePage prePage = api.convertT(data, EquipsPrePage.class);
            StringBuilder sb = new StringBuilder(String.format("页面/总页---%s/%s", prePage.getCn(), prePage.getCount()));
            for (EquipsPre equip : prePage.getEquips()) {
                sb.append("\n----------------\n").append(equip.toString("  "));
            }
            return sb.toString();
        } else return "背包异常";
    }

    public static final String TIPS0 = "装备用法'装备[装备背包中ID]'";
    @Action("装备<.*?=>x>")
    public String equip(Long id, @Param("x") String s) {
        if (!Judge.isEmpty(s)) {
            Integer eid = api.getIntegerOrDefault(s, null);
            if (eid == null) {
                return TIPS0;
            } else {
                ResponseEntity<String> data = api.equip(id, eid);
                if (data.getStatusCode().value() == 200) {
                    return "✅ " + data.getBody();
                } else {
                    return "❌ " + data.getBody();
                }
            }
        }
        return TIPS0;
    }

    public static final String TIPS1 = "装备用法'卸下装备[装备背包中ID]'";

    @Action("卸下装备<.*?=>x>")
    public String unequip(Long id, @Param("x") String s) {
        if (!Judge.isEmpty(s)) {
            Integer eid = api.getIntegerOrDefault(s, null);
            if (eid == null) {
                return TIPS1;
            } else {
                ResponseEntity<String> data = api.unequip(id, eid);
                if (data.getStatusCode().value() == 200) {
                    return "✅ " + data.getBody();
                } else {
                    return "❌ " + data.getBody();
                }
            }
        }
        return TIPS1;
    }
}