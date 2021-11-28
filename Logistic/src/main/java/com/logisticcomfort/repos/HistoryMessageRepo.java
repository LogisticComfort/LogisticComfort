package com.logisticcomfort.repos;

import com.logisticcomfort.model.HistoryMessage;
import com.logisticcomfort.model.TelegramUser;
import com.logisticcomfort.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryMessageRepo extends JpaRepository<HistoryMessage, Long> {
    HistoryMessage findByTelegramUser(TelegramUser telegramUser);
    void deleteById(long id);
}
