package by.innowise.user_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "payment_cards")
public class PaymentCard extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String number;
    private String holder;
    private Instant expiration_date;
    private boolean active;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
