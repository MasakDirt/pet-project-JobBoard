package com.board.job.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Document(collection = "feedbacks")
public class Feedback {
    @Id
    private String id;

    @Column(columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "You should write text about why you the best candidate for this vacation.")
    private String text;

    @Column(name = "send_at")
    @CreationTimestamp
    private LocalDateTime sendAt;

    @Column(name = "messenger_id")
    private long messengerId;

    @Column(name = "owner_id")
    private long ownerId;

    public Feedback() {
        this.sendAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feedback feedback = (Feedback) o;
        return messengerId == feedback.messengerId && Objects.equals(id, feedback.id) && Objects.equals(text, feedback.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, messengerId);
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", messengerId=" + messengerId +
                '}';
    }
}
