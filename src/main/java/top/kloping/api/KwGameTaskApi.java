package top.kloping.api;

import io.github.kloping.spt.annotations.Entity;
import org.springframework.http.ResponseEntity;

@Entity
public class KwGameTaskApi extends KwGameApi {
    @Override
    protected String getBasePath() {
        return "/tasks";
    }

    public ResponseEntity<String> list(Long pid) {
        return doGet("/list?pid={pid}", pid);
    }
}
