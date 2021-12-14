package com.logisticcomfort.model;

import javax.persistence.*;

@Entity
@Table(name = "HistoryMessage")
public class HistoryMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @OneToOne(optional=true, fetch = FetchType.EAGER)
    private TelegramUser telegramUser;

    private String message;

    public HistoryMessage(TelegramUser telegramUser, String message) {
        this.telegramUser = telegramUser;
        this.message = message;
    }

    public HistoryMessage() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TelegramUser getTelegramUser() {
        return telegramUser;
    }

    public void setTelegramUser(TelegramUser telegramUser) {
        this.telegramUser = telegramUser;
    }

    @Override
    public String toString() {
        return "HistoryMessage{" +
                "id=" + id +
                ", message='" + message + '\'' +
                '}';
    }
}
