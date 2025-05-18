package top.kloping.api;

import io.github.kloping.spt.annotations.Entity;
import org.springframework.http.ResponseEntity;
import top.kloping.api.dto.TravelDto;

import java.util.List;

import static top.kloping.config.MultiValueMapUtils.of;

@Entity
public class KwGameTravelApi extends KwGameApi {
    @Override
    protected String getBasePath() {
        return "/travel";
    }

    public ResponseEntity<String> travel(Long pid, Integer id) {
        return doPost("/travel", of("pid", pid, "id", id));
    }

    public ResponseEntity<String> explore(Long pid, Integer id) {
        return doPost("/explore", of("pid", pid, "id", id));
    }

    public ResponseEntity<String> challenge(Long pid, Integer id) {
        return doGet("/challenge?pid={pid}&id={id}", pid, id);
    }

    public ResponseEntity<String> challenges() {
        return doGet("/challenges");
    }


    public List<TravelDto> locations() {
        ResponseEntity<String> data = doGet("/locations");
        return convertTs(data, TravelDto.class);
    }

    public List<TravelDto> locations2() {
        ResponseEntity<String> data = doGet("/locations2");
        return convertTs(data, TravelDto.class);
    }
}
