package top.kloping.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.github.kloping.judge.Judge;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import top.kloping.CliMain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class KwGameApi {

    public static String URL = "http://127.0.0.1:920";
    public static RestTemplate TEMPLATE;

    protected abstract String getBasePath();

    public ResponseEntity<String> doGetAbs(String url) {
        return TEMPLATE.getForEntity(URL + url, String.class);
    }

    // 通用GET请求
    protected ResponseEntity<String> doGet(String sub, Object... params) {
        try {
            CliMain.client.tryConnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TEMPLATE.getForEntity(buildUrl(sub), String.class, params);
    }

    protected <T> ResponseEntity<T> doGet(Class<T> cla, String sub, Object... params) {
        try {
            CliMain.client.tryConnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TEMPLATE.getForEntity(buildUrl(sub), cla, params);
    }

    // 通用POST请求
    protected ResponseEntity<String> doPost(String sub, MultiValueMap<String, Object> params) {
        try {
            CliMain.client.tryConnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TEMPLATE.postForEntity(buildUrl(sub), params, String.class);
    }

    private String buildUrl(String subPath) {
        return KwGameApi.URL + getBasePath() + subPath;
    }

    public <T> T convertT(ResponseEntity<String> data, Class<T> t) {
        String body = data.getBody();
        return JSON.parseObject(body, t);
    }

    public <T> List<T> convertTs(ResponseEntity<String> data, Class<T> t) {
        String body = data.getBody();
        return JSONArray.parseArray(body, t);
    }

    public static String getProgressBar(int current, int total, int length, String emptyChar, String filledChar) {
        int filledLength = getFilledLength((double) current, total, length);
        return String.valueOf(filledChar).repeat(Math.max(0, filledLength)) + String.valueOf(emptyChar).repeat(Math.max(0, length - filledLength));
    }

    public static int getFilledLength(double current, int total, int length) {
        int filledLength = (int) Math.round(current / total * length);
        filledLength = Math.min(filledLength, length);
        return filledLength;
    }

    public Integer getIdOrDefault(String s, Integer defaultValue) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            Integer id = toId(s);
            if (id == null) return defaultValue;
            else return id;
        }
    }

    public Integer getIntegerOrDefault(String s, Integer defaultValue) {
        try {
            if (s == null) return defaultValue;
            return Integer.parseInt(s);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Integer toId(String name) {
        if (Judge.isEmpty(name)) return null;
        Integer id = null;
        ResponseEntity<Integer> e = TEMPLATE.getForEntity(URL + "/convert/toid?name={name}", Integer.class, name);
        if (e.getStatusCode().value() == 200) {
            id = e.getBody();
        }
        return id > 0 ? id : null;
    }

    public ResponseEntity<byte[]> src(Integer id) {
        ResponseEntity<byte[]> e = TEMPLATE.getForEntity(URL + "/convert/src?id={id}", byte[].class, id);
        if (e.getStatusCode().value() == 200) {
            return e;
        } else return null;
    }

    public ResponseEntity<byte[]> src(Integer id, Integer level) {
        ResponseEntity<byte[]> e = TEMPLATE.getForEntity(URL + "/convert/src?id={id}&level={level}", byte[].class, id, level);
        if (e.getStatusCode().value() == 200) {
            return e;
        } else return null;
    }

    public Map<Integer, String> logs() {
        ResponseEntity<String> e = TEMPLATE.getForEntity(URL + "/tt/logs", String.class);
        if (e.getStatusCode().value() == 200) {
            JSONObject jo = JSON.parseObject(e.getBody());
            Map<Integer, String> map = new HashMap<>();
            if (jo != null) {
                jo.forEach((k, v) -> map.put(Integer.parseInt(k), v.toString()));
            }
            return map;
        } else return null;
    }
}
