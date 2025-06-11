package top.kloping.api;

import io.github.kloping.spt.annotations.Entity;
import org.springframework.http.ResponseEntity;

@Entity
public class KwGameGachaApi extends KwGameApi {
    @Override
    protected String getBasePath() {
        return "/gacha";
    }

    public ResponseEntity<String> list() {
        return doGet("/list");
    }

    public ResponseEntity<String> one(Long pid, String type) {
        return doGet("/one?pid={pid}&type={type}", pid, type);
    }

    public ResponseEntity<String> ten(Long pid, String type) {
        return doGet("/ten?pid={pid}&type={type}", pid, type);
    }
}
