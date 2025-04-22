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
        return doPost("/travel", of("player_id", pid, "id", id));
    }

    public List<TravelDto> locations() {
        ResponseEntity<String> data = doGet("/locations");
        return convertTs(data, TravelDto.class);
    }
}
