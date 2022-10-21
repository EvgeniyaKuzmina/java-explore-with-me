package ru.yandex.practicum.mainserver.event.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.mainserver.status.Status;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
public class EventParam {

    private String text;

    private List<Long> categoriesId;

    private List<Long> usersId;
    private List<Status> states;

    private Boolean paid;

    private LocalDateTime rangeStart;

    private LocalDateTime rangeEnd;

    private Boolean onlyAvailable;

    private String sort;
    /*public void setRangeStart (LocalDateTime rangeStart) {
        this.rangeStart = Objects.requireNonNullElseGet(rangeStart, LocalDateTime::now);

    }*/

    public void setRangeStart(String rangeStart) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.rangeStart = LocalDateTime.parse(rangeStart, formatter);
    }

    public void setRangeStart(LocalDateTime rangeStart) {
        this.rangeStart = rangeStart;
    }

    public void setRangeEnd(LocalDateTime rangeEnd) {
        this.rangeEnd = rangeEnd;
    }

    public void setRangeEnd(String rangeEnd) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.rangeEnd = LocalDateTime.parse(rangeEnd, formatter);
    }

    public void setStates (List<String> states) {
        for (String s : states) {
            this.states.add(Status.from(s).orElseThrow(() -> {
                throw new IllegalArgumentException("Unknown state: " + s);
            }));
        }


    }

}
