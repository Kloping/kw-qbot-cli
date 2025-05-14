package top.kloping.controller;

import io.github.kloping.number.NumberUtils;
import io.github.kloping.spt.annotations.Action;
import io.github.kloping.spt.annotations.AutoStand;
import io.github.kloping.spt.annotations.Controller;
import io.github.kloping.spt.annotations.Param;
import org.springframework.http.ResponseEntity;
import top.kloping.api.KwGamePetApi;
import top.kloping.api.dto.PetWithImage;
import top.kloping.api.entity.Pet;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import static top.kloping.api.KwGameApi.getProgressBar;
import static top.kloping.controller.SkillController.getPetInfoPre;

/**
 * @author github kloping
 * @date 2025/4/20-17:32
 */
@Controller
public class PetController {
    @AutoStand
    KwGamePetApi api;

    @AutoStand
    SelectController selectController;

    @Action("领取宠物")
    public String claim(Long id) {
        ResponseEntity<String> response = api.available(id);
        if (response.getStatusCode().value() == 200) {
            selectController.register(id, (i) -> {
                ResponseEntity<String> data = api.claim(id, i);
                if (data.getStatusCode().value() == 200) {
                    Pet pet = api.convertT(data, Pet.class);
                    StringBuilder sb = new StringBuilder();
                    int hp = pet.getHp();
                    int chp = pet.getCurrentHp();
                    sb.append(pet.getName()).append(" '").append(pet.getType()).append("'系 ");
                    sb.append(pet.getTop() == 1 ? "已置顶\n" : "\n");
                    sb.append(pet.getLevel()).append("级 还需").append(pet.getRequiredExp() - pet.getExperience()).append("点经验升级\n剩余血量:");
                    sb.append(chp).append("/").append(hp).append("(").append(NumberUtils.toPercent(chp, hp)).append("%)").append("\n\n");
                    return sb.toString().trim();
                } else {
                    return data.getBody();
                }
            });
        }
        return response.getBody();
    }

    @Action("我的宠物")
    public String list(Long id) {
        ResponseEntity<String> response = api.list(id);
        if (response.getStatusCode().value() == 200) {
            List<Pet> pets = api.convertTs(response, Pet.class);
            StringBuilder sb = new StringBuilder();
            int n = 1;
            for (Pet pet : pets) {
                sb.append(n++).append(".").append(pet.getName()).append(" '").append(pet.getType()).append("'系 ");
                sb.append(pet.getTop() == 1 ? "已置顶\n " : "\n ");
                sb.append(pet.getLevel()).append("级 还需").append(pet.getRequiredExp() - pet.getExperience()).append("点经验升级\n 剩余血量:");
                sb.append(pet.getCurrentHp()).append("/").append(pet.getHp()).append("(").append(NumberUtils.toPercent(pet.getCurrentHp(), pet.getHp())).append("%)").append("\n\n");
            }
            return sb.toString().trim();
        }
        return response.getBody();
    }

    @Action("宠物信息<.*?=>x>")
    public Object info(Long id, @Param("x") String tx) {
        Integer n = api.getIdOrDefault(tx, 1);
        ResponseEntity<String> response = api.info(id, n);
        if (response.getStatusCode().value() == 200) {
            PetWithImage petw = api.convertT(response, PetWithImage.class);
            String base64 = petw.getData();
            byte[] bytes = Base64.getDecoder().decode(base64);
            Pet pet = petw.getPet();
            StringBuilder sb = new StringBuilder();
            return getPetInfoPre(bytes, pet, sb);
        } else return response.getBody();
    }

    @Action("宠物置顶<.*?=>x>")
    public String topto(Long id, @Param("x") String tx) {
        Integer n = api.getIdOrDefault(tx, 1);
        ResponseEntity<String> response = api.topto(id, n);
        if (response.getStatusCode().value() == 200) return "置顶成功";
        return response.getBody();
    }

    @Action("等级突破")
    public String breakthrough(Long id) {
        ResponseEntity<String> response = api.breakthrough(id, 1);
        if (response.getStatusCode().value() == 200) {
            return response.getBody();
        } else return response.getBody();
    }
}