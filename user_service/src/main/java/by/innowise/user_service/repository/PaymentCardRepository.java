package by.innowise.user_service.repository;

import by.innowise.user_service.entity.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long>, JpaSpecificationExecutor<PaymentCard> {

    @Modifying
    @Query(value = "UPDATE payment_cards SET active = :active WHERE id = :id", nativeQuery = true)
    int setActivePaymentCard(Long id, boolean active);

    List<PaymentCard> findByUserId(Long id);

    @Modifying
    @Query ("UPDATE PaymentCard p SET p.active = false WHERE p.user.id = :id")
    void deactivateByUserId(Long id);

}
