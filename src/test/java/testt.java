import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author github kloping
 * @date 2025/4/24-14:06
 */
public class testt {
    public static void main(String[] args) {
        CountDownLatch cdl = new CountDownLatch(1);
        try {
            boolean k = cdl.await(1, TimeUnit.SECONDS);
            System.out.println(k);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
