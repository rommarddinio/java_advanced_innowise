package by.innowise.user_service.controller;

import by.innowise.user_service.dto.PaymentCardDto;
import by.innowise.user_service.service.serviceImpl.PaymentCardServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
@AllArgsConstructor
public class PaymentCardController {

    private final PaymentCardServiceImpl paymentCardService;

    @PutMapping("/{id}")
    public ResponseEntity<PaymentCardDto> updatePaymentCard(@PathVariable Long id,
                                            @RequestBody @Valid PaymentCardDto paymentCardDto){
        return new ResponseEntity<>(paymentCardService.updatePaymentCard(id, paymentCardDto), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PaymentCardDto> createPaymentCard(@RequestBody @Valid PaymentCardDto paymentCardDto) {
        return new ResponseEntity<>(paymentCardService.createPaymentCard(paymentCardDto), HttpStatus.CREATED) ;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentCardDto> findById(@PathVariable Long id) {
        return new ResponseEntity<>(paymentCardService.findById(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<PaymentCardDto>> getPaymentCards(@RequestParam int page, @RequestParam int size,
                                                @RequestParam(required = false) String name,
                                                @RequestParam(required = false) String surname) {
        return new ResponseEntity<>(paymentCardService.getPaymentCards(page, size, name, surname), HttpStatus.OK);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activatePaymentCard(@PathVariable Long id) {
        paymentCardService.activatePaymentCard(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivatePaymentCard(@PathVariable Long id) {
        paymentCardService.deactivatePaymentCard(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/byUser/{userId}")
    public ResponseEntity<List<PaymentCardDto>> findByUserId(@PathVariable Long userId) {
        return new ResponseEntity<>(paymentCardService.findByUserId(userId), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        paymentCardService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
