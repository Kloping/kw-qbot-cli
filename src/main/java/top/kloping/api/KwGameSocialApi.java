package top.kloping.api;

import io.github.kloping.spt.annotations.Entity;
import org.springframework.http.ResponseEntity;

@Entity
public class KwGameSocialApi extends KwGameApi {
    @Override
    protected String getBasePath() {
        return "/social";
    }

    public ResponseEntity<String> staminaHelp(Long pid, Long tid) {
        return doGet("/staminaHelp?pid={pid}&tid={tid}", pid, tid);
    }
}
