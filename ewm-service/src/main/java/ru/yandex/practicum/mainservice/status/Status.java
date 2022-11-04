package ru.yandex.practicum.mainservice.status;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum Status {
    REJECTED("REJECTED"),
    PUBLISHED("PUBLISHED"),
    PENDING("PENDING"),
    CONFIRMED("CONFIRMED"),
    CANCELED("CANCELED");

    Status(String status) {
    }

    public static Status from(String stringState) {
        for (Status state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return state;
            }
        }
        log.warn("Status: from — указан неверный статус");
        throw new IllegalArgumentException("Unknown state: " + stringState);
    }
}
