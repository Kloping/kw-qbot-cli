package top.kloping.config;

import com.alibaba.fastjson.JSON;
import io.github.kloping.file.FileUtils;
import io.github.kloping.initialize.FileInitializeValue;
import io.github.kloping.judge.Judge;

import java.util.ArrayList;
import java.util.List;

/**
 * @author github kloping
 * @date 2025/6/2-11:58
 */
public class OpenConf {
    public static final String path = "./conf/opens.json";

    public OpenConf() {
        String data = FileUtils.getStringFromFile(path);
        if (Judge.isNotEmpty(data)) {
            List<Long> ids = JSON.parseArray(data, Long.class);
            for (Long id : ids) {
                if (!opens.contains(id))
                    opens.add(id);
            }
        }
    }

    public List<Long> opens = new ArrayList<>();

    public boolean opened(long id) {
        return opens.contains(id);
    }

    public void toOpen(long id) {
        if (!opens.contains(id)) {
            opens.add(id);
            FileInitializeValue.putValues(path, opens);
        }
    }

    public void toClose(long id) {
        if (opens.contains(id)) {
            opens.remove(id);
            FileInitializeValue.putValues(path, opens);
        }
    }
}
