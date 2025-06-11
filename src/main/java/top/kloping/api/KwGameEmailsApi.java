package top.kloping.api;

import io.github.kloping.spt.annotations.Entity;
import org.springframework.http.ResponseEntity;

@Entity
public class KwGameEmailsApi extends KwGameApi {
    @Override
    protected String getBasePath() {
        return "/emails";
    }

    public ResponseEntity<String> list(Long pid) {
        return doGet("/list?pid={pid}", pid);
    }

    public void gets(Long pid) {
        doGet("/gets?pid={pid}", pid);
    }
}
