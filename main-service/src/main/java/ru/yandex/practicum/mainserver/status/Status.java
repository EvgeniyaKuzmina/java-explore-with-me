package ru.yandex.practicum.mainserver.status;

import java.util.Optional;

public enum Status {

    REJECTED("REJECTED"),
    PUBLISHED("PUBLISHED"),
    PENDING("PENDING"),
    CONFIRMED("CONFIRMED"),
    CANCELED("CANCELED");

    Status(String  status) {
    }

    public static Optional<Status> from(String stringState) {
        for (Status state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
