package uz.yeysam.Yeysam.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.yeysam.Yeysam.model.entity.Role;
import uz.yeysam.Yeysam.model.entity.User;
import uz.yeysam.Yeysam.repository.RoleRepository;
import uz.yeysam.Yeysam.repository.UserRepository;
import uz.yeysam.Yeysam.service.base.UserService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class IUserService implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public Optional<User> getUserByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public void registerOrUpdate(org.telegram.telegrambots.meta.api.objects.User tgUser) {
        Optional<User> opt = userRepository.findByTelegramId(tgUser.getId());
        User user = opt.orElseGet(User::new);

        user.setTelegramId(tgUser.getId());
        user.setFirstName(tgUser.getFirstName());
        user.setLastName(tgUser.getLastName());
        user.setUsername(tgUser.getUserName());
        user.setLanguageCode(tgUser.getLanguageCode());

        if (user.getId() == null) {
            user.setCreatedAt(LocalDateTime.now());
            Role defaultRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Default role not found"));
            user.setRoles(Set.of(defaultRole));
        }
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}