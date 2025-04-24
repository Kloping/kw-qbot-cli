package top.kloping.controller;

import com.alibaba.fastjson.JSONObject;
import io.github.kloping.judge.Judge;
import io.github.kloping.spt.annotations.Action;
import io.github.kloping.spt.annotations.AutoStand;
import io.github.kloping.spt.annotations.Controller;
import io.github.kloping.spt.annotations.Param;
import org.springframework.http.ResponseEntity;
import top.kloping.api.KwGameApi;
import top.kloping.api.KwGameSkillApi;
import top.kloping.api.dto.DataWithTips;

/**
 * @author github kloping
 * @date 2025/4/24-22:08
 */
@Controller
public class SkillController {
    @AutoStand
    private KwGameSkillApi api;

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
}
