package top.kloping.api;

import io.github.kloping.spt.annotations.Entity;
import org.springframework.http.ResponseEntity;

import static top.kloping.config.MultiValueMapUtils.of;

@Entity
public class KwGamePlayerApi extends KwGameApi {
    @Override
    protected String getBasePath() {
        return "/players";
    }

    public ResponseEntity<String> register(Long id, String name) {
        return doGet("/register?id={id}&name={name}", id, name);
    }

    public ResponseEntity<String> show(Long id) {
        return doGet("/show?id={id}", id);
    }

    public ResponseEntity<String> rename(Long id, String name) {
        return doPost("/rename", of("id", id, "newName", name));
    }

    public ResponseEntity<String> work(Long id) {
        return doPost("/work", of("id", id));
    }
}
