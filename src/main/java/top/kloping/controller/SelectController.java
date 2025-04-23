package top.kloping.controller;

import io.github.kloping.spt.annotations.Controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author github kloping
 */
@Controller
public class SelectController {

    private final Map<Long, SelectAction> id2r = new HashMap<>();
    public final Set<Long> canovers = new HashSet<>();


    public void register(Long id, SelectAction runnable) {
        register(true, id, runnable);
    }

    public void register(boolean canover, Long id, SelectAction sa) {
        if (canovers.contains(id)) return;
        if (canover) {
            id2r.put(id, sa);
        } else {
            canovers.add(id);
            id2r.put(id, sa);
        }
    }

    public Object select(Long id, Integer i) {
        SelectAction selectAction = id2r.remove(id);
        canovers.remove(id);
        if (selectAction == null) return null;
        return selectAction.action(i);
    }

    public interface SelectAction {
        Object action(Integer i);
    }
}
