package uz.yeysam.Yeysam.service.base;

import uz.yeysam.Yeysam.model.entity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> getUserByUsername(String username);

    void registerOrUpdate(org.telegram.telegrambots.meta.api.objects.User tgUser);
}