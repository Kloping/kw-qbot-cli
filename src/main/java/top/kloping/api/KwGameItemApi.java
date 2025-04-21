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
        return doGet("/list?id={id}", id);
    }

    public ResponseEntity<String> shop(Long id) {
        return doGet("/shop?id={id}", id);
    }

    public ResponseEntity<String> buy(Long id, Integer itemId, Integer count) {
        return doGet("/buy?id={id}&itemId={itemId}&count={count}", id, itemId, count);
    }
    public ResponseEntity<String> sell(Long id, Integer itemId, Integer count) {
        return doGet("/sell?id={id}&itemId={itemId}&count={count}", id, itemId, count);
    }

    public ResponseEntity<String> use(Long id, Integer itemId, Integer count) {
        return doGet("/use?id={id}&itemId={itemId}&count={count}", id, itemId, count);
    }

    public ResponseEntity<String> desc(Integer id) {
        return doGet("/desc?id={id}", id);
    }

    public ResponseEntity<byte[]> src(Integer id) {
        return doGet(byte[].class, "/src?id={id}", id);
    }
}
