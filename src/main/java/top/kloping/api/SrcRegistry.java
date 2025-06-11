package top.kloping.api;

import io.github.kloping.spt.annotations.AutoStand;
import io.github.kloping.spt.annotations.Entity;
import io.github.kloping.spt.annotations.Schedule;
import io.github.kloping.spt.interfaces.component.ContextManager;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Image;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author github kloping
 * @date 2025/4/26-13:00
 */
@Entity
public class SrcRegistry {

    private final Map<Integer, Image> id2image = new HashMap<>();
    private final Map<String, Image> url2image = new HashMap<>();
    private final Map<String, Image> path2image = new HashMap<>();

    @AutoStand
    KwGameApi api;

    @AutoStand
    ContextManager contextManager;

    public Image getImage(Integer id) {
        Image image = id2image.get(id);
        if (image == null) {
            ResponseEntity<byte[]> entity = api.src(id);
            if (entity == null || entity.getStatusCode().value() != 200) return null;
            byte[] bytes = entity.getBody();
            Bot bot = contextManager.getContextEntity(Bot.class);
            if (bytes != null) {
                image = Contact.uploadImage(bot.getAsFriend(), new ByteArrayInputStream(bytes));
                id2image.put(id, image);
            }
        }
        return image;
    }

    public Image getImageByPath(String path) {
        Image image = path2image.get(path);
        if (image == null) {
            ResponseEntity<byte[]> entity = KwGameGachaApi.TEMPLATE.getForEntity(KwGameApi.URL + path, byte[].class);
            if (entity.getStatusCode().value() != 200) return null;
            byte[] bytes = entity.getBody();
            Bot bot = contextManager.getContextEntity(Bot.class);
            if (bytes != null) {
                image = Contact.uploadImage(bot.getAsFriend(), new ByteArrayInputStream(bytes));
                path2image.put(path, image);
            }
        }
        return image;
    }

    public Image getImage(String url) {
        Image image = url2image.get(url);
        if (image == null) {
            String[] id2level = url.split("_");
            Integer id = Integer.valueOf(id2level[0]);
            Integer level = Integer.valueOf(id2level[1]);
            byte[] bytes = api.src(id, level).getBody();
            Bot bot = contextManager.getContextEntity(Bot.class);
            if (bytes != null) {
                image = Contact.uploadImage(bot.getAsFriend(), new ByteArrayInputStream(bytes));
                url2image.put(url, image);
            }
        }
        return image;
    }

    @Schedule("00:01:22")
    public void clear() {
        id2image.clear();
        url2image.clear();
        path2image.clear();
    }
}
