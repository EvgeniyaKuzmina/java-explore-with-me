package ru.yandex.practicum.mainservice.event.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.mainservice.status.Status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class EventParam {

    private String text;

    private List<Long> categoriesId = new ArrayList<>();

    private List<Long> usersId = new ArrayList<>();
    private List<Status> states = new ArrayList<>();

    private Boolean paid;

    private LocalDateTime rangeStart;

    private LocalDateTime rangeEnd;

    private Boolean onlyAvailable;

    private String sort;

    public void setRangeStart(String rangeStart) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.rangeStart = LocalDateTime.parse(rangeStart, formatter);
    }

    public void setRangeStart(LocalDateTime rangeStart) {
        this.rangeStart = rangeStart;
    }


    public void setRangeEnd(String rangeEnd) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.rangeEnd = LocalDateTime.parse(rangeEnd, formatter);
    }

    public void setStates(List<String> states) {
        for (String s : states) {
            this.states.add(Status.from(s).orElseThrow(() -> {
                throw new IllegalArgumentException("Unknown state: " + s);
            }));
        }
    }

}
