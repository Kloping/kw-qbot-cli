package top.kloping.controller;

import io.github.kloping.spt.annotations.Controller;

import java.util.HashMap;
import java.util.Map;

/**
 * @author github kloping
 */
@Controller
public class SelectController {

    private final Map<Long, SelectAction> id2r = new HashMap<>();

    public synchronized void register(Long id, SelectAction runnable) {
        id2r.put(id, runnable);
    }

    public Object select(Long id, Integer i) {
        SelectAction selectAction = id2r.remove(id);
        if (selectAction == null) return null;
        return selectAction.action(i);
    }

    public interface SelectAction {
        Object action(Integer i);
    }
}
