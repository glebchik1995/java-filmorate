package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.PositiveOrZero;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Event {

    @PositiveOrZero
    private Long eventId;
    private Long timestamp;
    private Long userId;
    private String eventType;
    private String operation;
    private Long entityId;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("event_id", eventId);
        values.put("ts", timestamp);
        values.put("user_id", userId);
        values.put("event_type", eventType);
        values.put("operation", operation);
        values.put("entity_id", entityId);
        return values;
    }

    public Event(Long userId, EventType eventType, Operation operation, Long entityId) {
        this.timestamp = Instant.now().toEpochMilli();
        this.userId = userId;
        this.eventType = eventType.toString();
        this.operation = operation.toString();
//        this.eventId = null;
        this.entityId = entityId;
    }

    public enum EventType {
        LIKE,
        REVIEW,
        FRIEND
    }

    public enum Operation {
        REMOVE,
        ADD,
        UPDATE
    }
}
