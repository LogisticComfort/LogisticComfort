package com.logisticcomfort.repos;

import com.logisticcomfort.model.TelegramUser;
import com.logisticcomfort.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelegramUserRepo extends JpaRepository<TelegramUser, Long> {
    TelegramUser findByChatId(long chatId);
}
