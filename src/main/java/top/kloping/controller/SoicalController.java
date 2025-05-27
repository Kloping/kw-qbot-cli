package top.kloping.controller;

import io.github.kloping.judge.Judge;
import io.github.kloping.spt.annotations.Action;
import io.github.kloping.spt.annotations.AutoStand;
import io.github.kloping.spt.annotations.Controller;
import io.github.kloping.spt.annotations.Param;
import org.springframework.http.ResponseEntity;
import top.kloping.api.KwGameSocialApi;
import top.kloping.api.dto.DataWithTips;

/**
 * @author github kloping
 * @date 2025/4/20-13:56
 */
@Controller
public class SoicalController {
    @AutoStand
    KwGameSocialApi api;

    @Action("助力<.*?=>x>")
    public String helpTo(Long id, @Param("x") String text) {
        if (!Judge.isEmpty(text)) {
            int i = text.indexOf("@");
            if (i >= 0) {
                String stid = text.substring(i + 1).trim();
                Long tid = Long.valueOf(stid);
                ResponseEntity<String> data = api.staminaHelp(id, tid);
                if (data.getStatusCode().is2xxSuccessful()) {
                    DataWithTips dwt = api.convertT(data, DataWithTips.class);
                    return dwt.toString();
                } else return data.getBody();
            }
        }
        return "通过`助力`@xx ;给同伴助力恢复体力!";
    }
}
