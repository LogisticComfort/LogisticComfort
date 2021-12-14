package com.logisticcomfort.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "TelegramUser")
public class TelegramUser {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @OneToOne(optional=true, cascade= CascadeType.ALL, fetch = FetchType.EAGER)
    private User user;

    private Long chatId;

    @OneToOne(mappedBy="telegramUser", fetch = FetchType.LAZY)
    private HistoryMessage message;

    public TelegramUser(){
    }

    public TelegramUser(Long chatId) {
        this.chatId = chatId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public HistoryMessage getMessage() {
        return message;
    }

    public void setMessage(HistoryMessage message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "TelegramUser{" +
                "id=" + id +
                ", user=" + user +
                ", chatId=" + chatId +
                ", message=" + message +
                '}';
    }
}
