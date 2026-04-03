package by.innowise.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.util.List;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "items")
public class Item extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal price;

    private Boolean deleted;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    @BatchSize(size = 10)
    private List<OrderItem> orders;

}
