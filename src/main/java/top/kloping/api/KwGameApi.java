package top.kloping.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public abstract class KwGameApi {

    public static String URL = "http://127.0.0.1:920";
    public static RestTemplate TEMPLATE;

    protected abstract String getBasePath();

    // 通用GET请求
    protected ResponseEntity<String> doGet(String sub, Object... params) {
        return TEMPLATE.getForEntity(buildUrl(sub), String.class, params);
    }

    // 通用POST请求
    protected ResponseEntity<String> doPost(String sub, MultiValueMap<String, Object> params) {
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
        int filledLength = (int) Math.round((double) current / total * length);
        filledLength = Math.min(filledLength, length);
        return String.valueOf(filledChar).repeat(Math.max(0, filledLength)) + String.valueOf(emptyChar).repeat(Math.max(0, length - filledLength));
    }

    public Integer getIntOrDefault(String s, Integer defaultValue) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
