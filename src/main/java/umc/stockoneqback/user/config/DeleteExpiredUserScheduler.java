package umc.stockoneqback.user.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import umc.stockoneqback.user.service.UserService;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class DeleteExpiredUserScheduler {
    private final UserService userService;

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteExpiredUser() {
        userService.deleteExpiredUser();
    }
}
