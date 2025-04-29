package top.kloping.api;

import io.github.kloping.spt.annotations.Entity;
import org.springframework.http.ResponseEntity;

@Entity
public class KwGameItemApi extends KwGameApi {
    @Override
    protected String getBasePath() {
        return "/items";
    }

    public ResponseEntity<String> list(Long id) {
        return doGet("/list?pid={id}", id);
    }

    public ResponseEntity<String> shop(Long id) {
        return doGet("/shop?pid={id}", id);
    }

    public ResponseEntity<String> buy(Long id, Integer itemId, Integer count) {
        return doGet("/buy?pid={id}&itemId={itemId}&count={count}", id, itemId, count);
    }

    public ResponseEntity<String> sell(Long id, Integer itemId, Integer count) {
        return doGet("/sell?pid={id}&itemId={itemId}&count={count}", id, itemId, count);
    }

    public ResponseEntity<String> use(Long id, Integer itemId, Integer count) {
        return doGet("/use?pid={id}&itemId={itemId}&count={count}", id, itemId, count);
    }

    //给予
    public ResponseEntity<String> give(Long id, Integer itemId, Integer count) {
        return doGet("/give?pid={id}&itemId={itemId}&count={count}", id, itemId, count);
    }
}
