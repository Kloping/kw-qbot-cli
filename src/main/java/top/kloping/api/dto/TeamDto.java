package top.kloping.api.dto;

import lombok.Data;

import java.util.List;

/**
 * @author github kloping
 * @date 2025/4/29-09:58
 */
@Data
public class TeamDto {
    private Long mid;
    private Long tid = -1L, tid2=-1L;

    public TeamDto() {
    }

    public TeamDto(Long mid) {
        this.mid = mid;
    }

    public int getCount() {
        int i = 0;
        if (mid > 0) i++;
        if (tid > 0) i++;
        if (tid2 > 0) i++;
        return i;
    }

    public List<Long> getAllDefMain() {
        return List.of(tid, tid2);
    }

    public void addOne(Long tid) {
        if (this.tid < 0) {
            this.tid = tid;
        } else if (this.tid2 < 0) {
            this.tid2 = tid;
        }
    }

    public void remove(Long pid) {
        if (mid > 0 && mid.equals(pid)) {
            mid = -1L;
        } else {
            if (tid > 0 && tid.equals(pid)) {
                tid = -1L;
            } else {
                if (tid2 > 0 && tid2.equals(pid)) {
                    tid = tid2;
                    tid2 = -1L;
                }
            }
        }
    }

    public boolean isEmpty() {
        return mid < 0;
    }

    public long getOneDefMain() {
        return tid < 0 ? tid2 : tid;
    }

    @Override
    public String toString() {
        return String.format("组队信息:\n队长: %s\n队员1: %s\n队员2: %s"
                , mid, tid < 0 ? "无" : tid, tid2 < 0 ? "无" : tid2);
    }
}
