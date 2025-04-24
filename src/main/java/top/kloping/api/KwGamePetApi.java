package top.kloping.api;

import io.github.kloping.spt.annotations.Entity;
import org.springframework.http.ResponseEntity;
import top.kloping.config.MultiValueMapUtils;

@Entity
public class KwGamePetApi extends KwGameApi {
    @Override
    protected String getBasePath() {
        return "/pets";
    }

    public ResponseEntity<String> claim(Long id, Integer n) {
        return doPost("/claim", MultiValueMapUtils.of("pid", id, "n", n));
    }

    public ResponseEntity<String> info(Long id, Integer n) {
        return doGet("/info?pid={id}&n={n}", id, n);
    }

    public ResponseEntity<String> list(Long id) {
        return doGet("/list?pid={id}", id);
    }

    public ResponseEntity<String> topto(Long id, Integer n) {
        return doGet("/topto?pid={id}&n={n}", id, n);
    }

    public ResponseEntity<String> available(Long id) {
        return doGet("/available?pid={id}", id);
    }

}
