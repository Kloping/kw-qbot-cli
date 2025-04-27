package top.kloping.api;

import io.github.kloping.spt.annotations.Entity;
import org.springframework.http.ResponseEntity;

@Entity
public class KwGameConvertApi extends KwGameApi {
    @Override
    protected String getBasePath() {
        return "/convert";
    }

    /**
     * 根据id转名字
     *
     * @param id
     * @return
     */
    public String toName(Integer id) {
        ResponseEntity<String> e = doGet("/toname?id={id}", id);
        if (e.getStatusCode().value() == 200) {
            return e.getBody();
        }
        return "转化失败";
    }

    public ResponseEntity<String> desc(Integer id) {
       return doGet("/desc?id={id}", id);
    }

    public String desc(String name) {
        ResponseEntity<String> e = doGet("/desc?name={name}", name);
        if (e.getStatusCode().value() == 200) {
            return e.getBody();
        }
        return "获取失败";
    }
}
