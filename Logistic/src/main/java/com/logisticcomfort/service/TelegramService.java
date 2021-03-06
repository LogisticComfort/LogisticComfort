package com.logisticcomfort.service;

import com.logisticcomfort.model.HistoryMessage;
import com.logisticcomfort.model.TelegramUser;
import com.logisticcomfort.model.User;
import com.logisticcomfort.repos.HistoryMessageRepo;
import com.logisticcomfort.repos.TelegramUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class TelegramService {

    private TelegramUserRepo telegramUserRepo;
    private HistoryMessageRepo historyMessageRepo;

    @Autowired
    public TelegramService(TelegramUserRepo telegramUserRepo, HistoryMessageRepo historyMessageRepo) {
        this.telegramUserRepo = telegramUserRepo;
        this.historyMessageRepo = historyMessageRepo;
    }

    public TelegramUser findUserByChatId(Long chat_id){
        return telegramUserRepo.findByChatId(chat_id);
    }

    public HistoryMessage findMessageByTelegramUser(TelegramUser user){
        return historyMessageRepo.findByTelegramUser(user);
    }

    public HistoryMessage findMessageByChatId(Long chatId){
        return historyMessageRepo.findByTelegramUser(findUserByChatId(chatId));
    }

    public void saveTelegramUser(TelegramUser user){
        telegramUserRepo.saveAndFlush(user);
    }

    public void saveHistoryMessage(HistoryMessage message){
        historyMessageRepo.saveAndFlush(message);
    }

    public void deleteHistoryMessageById(Long id){
        historyMessageRepo.deleteById(id);
    }

    @Transactional
    public void deleteTelegramUserByChatId(Long chatId){
        var user = findUserByChatId(chatId);
        user.setUser(null);
        telegramUserRepo.save(user);
        telegramUserRepo.deleteByChatId(chatId);
    }
}
