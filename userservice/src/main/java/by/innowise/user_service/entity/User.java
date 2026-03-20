package by.innowise.user_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String surname;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    private String email;

    private boolean active;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @BatchSize(size = 5)
    private Set<PaymentCard> paymentCards = new HashSet<>();
}
