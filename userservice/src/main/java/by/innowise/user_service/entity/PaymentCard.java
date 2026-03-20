package by.innowise.user_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

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

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    private boolean active;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
