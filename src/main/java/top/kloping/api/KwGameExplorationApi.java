package top.kloping.api;

import io.github.kloping.spt.annotations.Entity;
import org.springframework.http.ResponseEntity;

import static top.kloping.config.MultiValueMapUtils.of;

@Entity
public class KwGameExplorationApi extends KwGameApi {
    @Override
    protected String getBasePath() {
        return "/exploration";
    }

    public ResponseEntity<String> explore(Long id) {
        return doPost("/explore", of("id", id));
    }
}
