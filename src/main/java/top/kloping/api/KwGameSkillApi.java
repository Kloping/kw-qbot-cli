package top.kloping.api;

import io.github.kloping.spt.annotations.Entity;
import org.springframework.http.ResponseEntity;

@Entity
public class KwGameSkillApi extends KwGameApi {
    @Override
    protected String getBasePath() {
        return "/skill";
    }

    public ResponseEntity<String> use(Long id, Integer st, Integer tar) {
        return doGet("/use?pid={id}&st={st}&target={tar}", id, st, tar);
    }

    public ResponseEntity<String> giveUp(Long id) {
        return doGet("/giveup?pid={id}", id);
    }

    public ResponseEntity<String> equips(Long pid) {
        return doGet("/equips?pid={pid}", pid);
    }

    public ResponseEntity<String> currentInfo(Long pid) {
        return doGet("/current_info?pid={pid}", pid);
    }
}
