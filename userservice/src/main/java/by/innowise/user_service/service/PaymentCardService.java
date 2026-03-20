package by.innowise.user_service.service;

import by.innowise.user_service.dto.PaymentCardDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PaymentCardService {

    PaymentCardDto createPaymentCard(PaymentCardDto  paymentCardDto);

    PaymentCardDto  updatePaymentCard(Long id, PaymentCardDto  paymentCardDto);

    PaymentCardDto findById(Long id);

    void activatePaymentCard(Long id);

    void deactivatePaymentCard(Long id);

    Page<PaymentCardDto> getPaymentCards(int page, int size, String name, String surname);

    List<PaymentCardDto> findByUserId(Long userId);

    void deleteById(Long id);

}
