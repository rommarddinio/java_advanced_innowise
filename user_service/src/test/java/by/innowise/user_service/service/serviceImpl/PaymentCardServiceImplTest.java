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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentCardServiceImplTest {

    @InjectMocks
    private PaymentCardServiceImpl paymentCardService;

    @Mock
    private PaymentCardRepository paymentCardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentCardMapper paymentCardMapper;

    private PaymentCardDto paymentCardDto;
    private PaymentCard paymentCard;
    private User user;

    @BeforeEach
    void setUp() {

        paymentCard = new PaymentCard();
        paymentCardDto = new PaymentCardDto();
        user = new User();

    }

    @Test
    void createPaymentCard_ShouldReturnPaymentCardDto_WhenSuccessful() {

        paymentCardDto.setUserId(1L);
        user.setId(1L);
        paymentCard.setUser(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(paymentCardMapper.toPaymentCard(paymentCardDto)).thenReturn(paymentCard);
        when(paymentCardRepository.save(paymentCard)).thenReturn(paymentCard);
        when(paymentCardMapper.toPaymentCardDto(paymentCard)).thenReturn(paymentCardDto);

        PaymentCardDto result = paymentCardService.createPaymentCard(paymentCardDto);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());

        verify(userRepository).findById(1L);
        verify(paymentCardMapper).toPaymentCard(paymentCardDto);
        verify(paymentCardRepository).save(paymentCard);
        verify(paymentCardMapper).toPaymentCardDto(paymentCard);

    }

    @Test
    void createPaymentCard_ShouldThrowException_WhenUserNotFound() {

        paymentCardDto.setUserId(99L);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                paymentCardService.createPaymentCard(paymentCardDto));

        verify(userRepository).findById(99L);
        verifyNoInteractions(paymentCardMapper);
        verifyNoInteractions(paymentCardRepository);

    }

    @Test
    void createPaymentCard_ShouldThrowException_WhenUserReachedLimitOfCards() {

        paymentCardDto.setUserId(1L);
        user.setId(1L);

        Set<PaymentCard> paymentCardSet = Set.of(new PaymentCard(), new PaymentCard(),
                new PaymentCard(), new PaymentCard(), new PaymentCard());
        user.setPaymentCards(paymentCardSet);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(CardLimitException.class, () ->
                paymentCardService.createPaymentCard(paymentCardDto));

        verify(userRepository).findById(1L);
        verifyNoInteractions(paymentCardMapper);
        verifyNoInteractions(paymentCardRepository);

    }

    @Test
    void createPaymentCard_ShouldThrowException_WhenNumberNotUnique() {

        paymentCardDto.setUserId(1L);
        user.setId(1L);
        paymentCard.setUser(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(paymentCardMapper.toPaymentCard(paymentCardDto)).thenReturn(paymentCard);
        when(paymentCardRepository.save(paymentCard)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(DataIntegrityViolationException.class, () ->
                paymentCardService.createPaymentCard(paymentCardDto));

        verify(userRepository).findById(1L);
        verify(paymentCardMapper).toPaymentCard(paymentCardDto);
        verify(paymentCardRepository).save(paymentCard);

    }

    @Test
    void updatePaymentCard_ShouldReturnUpdatedPaymentCardDto_WhenSuccessful() {

        user.setId(1L);
        paymentCardDto.setNumber("1111 1111 1111 1111");
        paymentCardDto.setUserId(10L);
        paymentCard.setNumber("0000 0000 0000 0000");
        paymentCard.setUser(user);

        PaymentCard updatedPaymentCard = new PaymentCard();
        updatedPaymentCard.setId(10L);
        updatedPaymentCard.setNumber("1111 1111 1111 1111");
        updatedPaymentCard.setUser(user);
        PaymentCardDto updatedPaymentCardDto = new PaymentCardDto();
        updatedPaymentCardDto.setNumber("1111 1111 1111 1111");
        updatedPaymentCardDto.setUserId(10L);

        when(paymentCardRepository.findById(1L)).thenReturn(Optional.of(paymentCard));
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(paymentCardRepository.save(paymentCard)).thenReturn(updatedPaymentCard);
        when(paymentCardMapper.toPaymentCardDto(updatedPaymentCard)).thenReturn(updatedPaymentCardDto);

        PaymentCardDto result = paymentCardService.updatePaymentCard(1L, paymentCardDto);

        assertNotNull(result);
        assertEquals(updatedPaymentCard.getNumber(), result.getNumber());
        assertEquals(10L, result.getUserId());

        verify(paymentCardRepository).findById(1L);
        verify(userRepository).findById(10L);
        verify(paymentCardRepository).save(paymentCard);
        verify(paymentCardMapper).toPaymentCardDto(updatedPaymentCard);
    }

    @Test
    void updatePaymentCard_ShouldThrowException_WhenNotFound() {

        when(paymentCardRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () ->
                paymentCardService.updatePaymentCard(99L, paymentCardDto));

        verify(paymentCardRepository).findById(99L);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(paymentCardMapper);
    }

    @Test
    void updatePaymentCard_ShouldThrowException_WhenUserNotFound() {

        paymentCardDto.setUserId(99L);

        when(paymentCardRepository.findById(1L)).thenReturn(Optional.of(paymentCard));
        when(userRepository.findById(paymentCardDto.getUserId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                paymentCardService.updatePaymentCard(1L, paymentCardDto));

        verify(paymentCardRepository).findById(1L);
        verify(userRepository).findById(99L);
        verifyNoInteractions(paymentCardMapper);

    }

    @Test
    void updatePaymentCard_ShouldThrowException_WhenNumberNotUnique() {

        paymentCardDto.setUserId(10L);

        when(paymentCardRepository.findById(1L)).thenReturn(Optional.of(paymentCard));
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(paymentCardRepository.save(paymentCard)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(DataIntegrityViolationException.class, () ->
                paymentCardService.updatePaymentCard(1L, paymentCardDto));

        verify(paymentCardRepository).findById(1L);
        verify(userRepository).findById(10L);
        verify(paymentCardRepository).save(paymentCard);
        verifyNoInteractions(paymentCardMapper);

    }


    @Test
    void getPaymentCards_ShouldReturnPageWithoutFilters() {

        int page = 0;
        int size = 10;

        Page<PaymentCard> paymentCardPage = new PageImpl<>(List.of(paymentCard),
                PageRequest.of(page, size), 1);

        when(paymentCardRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(paymentCardPage);
        when(paymentCardMapper.toPaymentCardDto(paymentCard)).thenReturn(paymentCardDto);

        Page<PaymentCardDto> result = paymentCardService
                .getPaymentCards(page, size, null, null);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(paymentCardRepository).findAll(any(Specification.class), any(PageRequest.class));
        verify(paymentCardMapper).toPaymentCardDto(paymentCard);

    }

    @Test
    void getPaymentCards_ShouldReturnEmptyPage() {

        int page = 0;
        int size = 10;

        Page<PaymentCard> paymentCardPage = new PageImpl<>(List.of(),
                PageRequest.of(page, size), 0);

        when(paymentCardRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(paymentCardPage);

        Page<PaymentCardDto> result =
                paymentCardService.getPaymentCards(page, size, null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(paymentCardRepository).findAll(any(Specification.class), any(PageRequest.class));
        verifyNoInteractions(paymentCardMapper);

    }

    @Test
    void getPaymentCards_ShouldReturnPageWithFilters() {

        int page = 0;
        int size = 10;

        Page<PaymentCard> paymentCardPage = new PageImpl<>(List.of(paymentCard),
                PageRequest.of(page, size), 1);

        when(paymentCardRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(paymentCardPage);
        when(paymentCardMapper.toPaymentCardDto(paymentCard))
                .thenReturn(paymentCardDto);

        Page<PaymentCardDto> result = paymentCardService
                .getPaymentCards(page, size, "Roman", "Sidorchuk");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(paymentCardRepository).findAll(any(Specification.class), any(PageRequest.class));
        verify(paymentCardMapper).toPaymentCardDto(paymentCard);

    }

    @Test
    void findById_ShouldThrowException_WhenNotFound() {

        when(paymentCardRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () ->
                paymentCardService.findById(99L));

        verify(paymentCardRepository).findById(99L);
        verifyNoInteractions(paymentCardMapper);

    }

    @Test
    void findById_ShouldReturnPaymentCard_WhenSuccessful() {

        paymentCardDto.setHolder("RAMAN SIDARCHUK");
        paymentCard.setHolder("RAMAN SIDARCHUK");

        when(paymentCardRepository.findById(1L)).thenReturn(Optional.of(paymentCard));
        when(paymentCardMapper.toPaymentCardDto(paymentCard)).thenReturn(paymentCardDto);
        PaymentCardDto result = paymentCardService.findById(1L);

        assertNotNull(result);
        assertEquals("RAMAN SIDARCHUK", result.getHolder());

        verify(paymentCardRepository).findById(1L);
        verify(paymentCardMapper).toPaymentCardDto(paymentCard);

    }

    @Test
    void findByUserId_ShouldReturnListOfUserPaymentCards_WhenSuccessful() {

        paymentCardDto.setUserId(1L);
        user.setId(1L);
        paymentCard.setUser(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(paymentCardRepository.findByUserId(1L)).thenReturn(List.of(paymentCard));
        when(paymentCardMapper.toPaymentCardDto(paymentCard)).thenReturn(paymentCardDto);

        List<PaymentCardDto> result = paymentCardService.findByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getUserId());

        verify(userRepository).findById(1L);
        verify(paymentCardRepository).findByUserId(1L);
        verify(paymentCardMapper).toPaymentCardDto(paymentCard);

    }

    @Test
    void findByUserId_ShouldThrowException_WhenUserNotFound() {

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                paymentCardService.findByUserId(99L));

        verify(userRepository).findById(99L);
        verifyNoInteractions(paymentCardRepository);
        verifyNoInteractions(paymentCardMapper);

    }

    @Test
    void findByUserId_ShouldReturnEmptyList() {

        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(paymentCardRepository.findByUserId(1L)).thenReturn(List.of());

        List<PaymentCardDto> result = paymentCardService.findByUserId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository).findById(1L);
        verify(paymentCardRepository).findByUserId(1L);
        verifyNoInteractions(paymentCardMapper);

    }

    @Test
    void activatePaymentCard_ShouldReturnUpdatedRow_WhenSuccessful() {

        when(paymentCardRepository.setActivePaymentCard(1L, true)).thenReturn(1);

        paymentCardService.activatePaymentCard(1L);

        verify(paymentCardRepository).setActivePaymentCard(1L, true);

    }

    @Test
    void activatePaymentCard_ShouldThrowException_WhenNotFound() {

        when(paymentCardRepository.setActivePaymentCard(99L, true)).thenReturn(0);

        assertThrows(CardNotFoundException.class,
                () -> paymentCardService.activatePaymentCard(99L));

        verify(paymentCardRepository).setActivePaymentCard(99L, true);

    }

    @Test
    void deactivatePaymentCard_ShouldReturnUpdatedRow_WhenSuccessful() {

        when(paymentCardRepository.setActivePaymentCard(1L, false)).thenReturn(1);

        paymentCardService.deactivatePaymentCard(1L);

        verify(paymentCardRepository).setActivePaymentCard(1L, false);

    }

    @Test
    void deactivatePaymentCard_ShouldThrowException_WhenNotFound() {

        when(paymentCardRepository.setActivePaymentCard(99L, false)).thenReturn(0);

        assertThrows(CardNotFoundException.class,
                () -> paymentCardService.deactivatePaymentCard(99L));

        verify(paymentCardRepository).setActivePaymentCard(99L, false);

    }

    @Test
    void deleteById_ShouldDeletePaymentCard() {

        paymentCardService.deleteById(1L);

        verify(paymentCardRepository).deleteById(1L);

    }

}