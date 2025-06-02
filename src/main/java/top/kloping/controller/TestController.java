package top.kloping.controller;

import io.github.kloping.spt.annotations.Action;
import io.github.kloping.spt.annotations.AutoStand;
import io.github.kloping.spt.annotations.Controller;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import top.kloping.config.OpenConf;

/**
 * @author github kloping
 * @date 2025/5/15-18:07
 */
@Controller
public class TestController {

    @AutoStand
    OpenConf openConf;

    @Action("开")
    public String open(MessageEvent event) {
        if (event instanceof GroupMessageEvent) {
            GroupMessageEvent gme = (GroupMessageEvent) event;
            if (gme.getSender().getPermission().getLevel() >= 1) {
                openConf.toOpen(gme.getGroup().getId());
                return "OK";
            }
        }
        return null;
    }

    @Action("关")
    public String close(MessageEvent event) {
        if (event instanceof GroupMessageEvent) {
            GroupMessageEvent gme = (GroupMessageEvent) event;
            if (gme.getSender().getPermission().getLevel() >= 1) {
                openConf.toClose(gme.getGroup().getId());
                return "OK";
            }
        }
        return null;
    }
}
