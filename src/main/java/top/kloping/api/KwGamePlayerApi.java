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
        return doGet("/register?pid={id}&name={name}", id, name);
    }

    public ResponseEntity<String> show(Long id) {
        return doGet("/show?pid={id}", id);
    }

    public ResponseEntity<String> rename(Long id, String name) {
        return doPost("/rename", of("pid", id, "newName", name));
    }

    public ResponseEntity<String> work(Long id) {
        return doPost("/work", of("pid", id));
    }

    //组队信息
    public ResponseEntity<String> team(Long id, Long tid) {
        return doGet("/team?pid={id}&tid={tid}", id, tid);
    }

    public ResponseEntity<String> team(Long id) {
        return doGet("/team?pid={id}", id);
    }

    //退出组队
    public ResponseEntity<String> quitTeam(Long id) {
        return doGet("/quit_team?pid={pid}", id);
    }
}
