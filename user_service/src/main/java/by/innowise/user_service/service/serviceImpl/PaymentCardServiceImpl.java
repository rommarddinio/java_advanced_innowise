package by.innowise.user_service.service.serviceImpl;

import by.innowise.user_service.dto.PaymentCardDto;
import by.innowise.user_service.dto.mapping.PaymentCardMapper;
import by.innowise.user_service.entity.PaymentCard;
import by.innowise.user_service.entity.User;
import by.innowise.user_service.exception.CardLimitException;
import by.innowise.user_service.exception.CardNotFoundException;
import by.innowise.user_service.exception.UserNotFoundException;
import by.innowise.user_service.repository.PaymentCardRepository;
import by.innowise.user_service.repository.UserRepository;
import by.innowise.user_service.service.PaymentCardService;
import by.innowise.user_service.specification.PaymentCardSpecifications;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class PaymentCardServiceImpl implements PaymentCardService{

    private final UserRepository userRepository;
    private final PaymentCardRepository paymentCardRepository;
    private final PaymentCardMapper paymentCardMapper;

    @Override
    public PaymentCardDto createPaymentCard(PaymentCardDto paymentCardDto) {
        User user = userRepository.findById(paymentCardDto.getUserId())
                .orElseThrow(UserNotFoundException::new);
        if (user.getPaymentCards().size() >=5) {
            throw new CardLimitException();
        }
        return paymentCardMapper.toPaymentCardDto(paymentCardRepository.save(paymentCardMapper
                .toPaymentCard(paymentCardDto)));
    }

    @CacheEvict(
            value = "payment_card",
            key = "#id"
    )
    @Transactional
    @Override
    public PaymentCardDto updatePaymentCard(Long id, PaymentCardDto paymentCardDto) {
        PaymentCard paymentCard = paymentCardRepository.findById(id)
                .orElseThrow(CardNotFoundException::new);

        paymentCard.setNumber(paymentCardDto.getNumber());
        paymentCard.setHolder(paymentCard.getHolder());
        paymentCard.setExpirationDate(paymentCardDto.getExpirationDate());
        paymentCard.setUser(userRepository.findById(paymentCardDto.getUserId()).
                orElseThrow(UserNotFoundException::new));

        return paymentCardMapper.toPaymentCardDto(paymentCardRepository.save(paymentCard));
    }

    @Cacheable(
            value = "payment_card",
            key = "#id"
    )
    @Override
    public PaymentCardDto findById(Long id) {
        return paymentCardRepository.findById(id).map(paymentCardMapper::toPaymentCardDto)
                .orElseThrow(CardNotFoundException::new);
    }

    @CacheEvict(
            value = "payment_card",
            key = "#id"
    )
    @Transactional
    @Override
    public void activatePaymentCard(Long id) {
        int rows = paymentCardRepository.setActivePaymentCard(id, true);
        if (rows == 0) throw new CardNotFoundException();
    }

    @CacheEvict(
            value = "payment_card",
            key = "#id"
    )
    @Transactional
    @Override
    public void deactivatePaymentCard(Long id) {
        int rows = paymentCardRepository.setActivePaymentCard(id, false);
        if (rows == 0) throw new CardNotFoundException();
    }

    @Override
    public Page<PaymentCardDto> getPaymentCards(int page, int size, String name, String surname) {
        Specification<PaymentCard> specification = Specification
                .where(PaymentCardSpecifications.hasUserName(name))
                .and(PaymentCardSpecifications.hasUserSurname(surname));
        return paymentCardRepository
                .findAll(specification, PageRequest.of(page, size))
                .map(paymentCardMapper::toPaymentCardDto);
    }

    @Override
    public List<PaymentCardDto> findByUserId(Long userId) {
        userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        return paymentCardRepository.findByUserId(userId)
                .stream()
                .map(paymentCardMapper::toPaymentCardDto)
                .toList();
    }

    @CacheEvict(
            value = "payment_card",
            key = "#id"
    )
    @Transactional
    @Override
    public void deleteById(Long id) {
        paymentCardRepository.deleteById(id);
    }

}
