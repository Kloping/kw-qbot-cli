package top.kloping.controller;

import io.github.kloping.spt.annotations.Action;
import io.github.kloping.spt.annotations.Controller;

/**
 * @author github kloping
 * @date 2025/5/15-18:07
 */
@Controller
public class TestController {

    @Action("开")
    public String open(String s) {
        return "OK";
    }
}
