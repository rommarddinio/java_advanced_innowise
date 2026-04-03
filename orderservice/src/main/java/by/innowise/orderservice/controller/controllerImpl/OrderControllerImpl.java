package by.innowise.orderservice.controller.controllerImpl;

import by.innowise.orderservice.controller.OrderController;
import by.innowise.orderservice.dto.OrderDto;
import by.innowise.orderservice.dto.UpdateStatusDto;
import by.innowise.orderservice.dto.create.OrderCreateDto;
import by.innowise.orderservice.dto.user.UserInfo;
import by.innowise.orderservice.enums.Status;
import by.innowise.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderControllerImpl implements OrderController {

    private final OrderService orderService;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody @Valid OrderCreateDto dto) {
        return new ResponseEntity<>(orderService.createOrder(dto), HttpStatus.CREATED);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping
    public ResponseEntity<OrderDto> updateById(@RequestBody UpdateStatusDto dto) {
        return new ResponseEntity<>(orderService.updateById(dto.getId(), dto.getStatus()), HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> findById(@PathVariable Long id) {
        return new ResponseEntity<>(orderService.findById(id), HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<OrderDto>> findAll(int page, int size,
                                                  @RequestParam(required = false) Instant startDate,
                                                  @RequestParam(required = false) Instant endDate,
                                                  @RequestParam(required = false) Status status) {
        return new ResponseEntity<>(orderService.findAll(page, size, startDate, endDate, status),
                HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDto>> findByUserId(@PathVariable Long userId) {
        return new ResponseEntity<>(orderService.findByUserId(userId), HttpStatus.OK);
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<List<OrderDto>> findBySelfId() {
        return new ResponseEntity<>(orderService.findBySelfId(), HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        orderService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
