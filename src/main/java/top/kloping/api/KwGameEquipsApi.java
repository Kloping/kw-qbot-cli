package top.kloping.api;

import io.github.kloping.spt.annotations.Entity;
import org.springframework.http.ResponseEntity;

@Entity
public class KwGameEquipsApi extends KwGameApi {
    @Override
    protected String getBasePath() {
        return "/equip";
    }

    public ResponseEntity<String> bag(Long pid, int n) {
        return doGet("/bag?pid={pid}&n={n}", pid, n);
    }

    //equip 装备上
    public ResponseEntity<String> equip(Long pid, Integer id) {
        return doGet("/equip?pid={pid}&id={id}", pid, id);
    }

    //卸装备
    public ResponseEntity<String> unequip(Long pid, Integer id) {
        return doGet("/unequip?pid={pid}&id={id}", pid, id);
    }

    public ResponseEntity<String> upgrade(Long pid, Integer id) {
        return doGet("/upgrade?pid={pid}&id={id}", pid, id);
    }

    public ResponseEntity<String> disassemble(Long pid, Integer id) {
        return doGet("/disassemble?pid={pid}&id={id}", pid, id);
    }
}
