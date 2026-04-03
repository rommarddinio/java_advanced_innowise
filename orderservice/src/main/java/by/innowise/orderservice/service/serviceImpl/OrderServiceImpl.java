package by.innowise.orderservice.service.serviceImpl;

import by.innowise.orderservice.dto.OrderDto;
import by.innowise.orderservice.dto.user.UserInfo;
import by.innowise.orderservice.dto.create.OrderCreateDto;
import by.innowise.orderservice.dto.create.OrderItemCreateDto;
import by.innowise.orderservice.dto.mapping.OrderMapper;
import by.innowise.orderservice.entity.Item;
import by.innowise.orderservice.entity.Order;
import by.innowise.orderservice.entity.OrderItem;
import by.innowise.orderservice.enums.Status;
import by.innowise.orderservice.exception.*;
import by.innowise.orderservice.repository.ItemRepository;
import by.innowise.orderservice.repository.OrderRepository;
import by.innowise.orderservice.service.OrderService;
import by.innowise.orderservice.service.UserClientService;
import by.innowise.orderservice.specification.OrderSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final OrderMapper orderMapper;
    private final UserClientService userClientService;

    private UserInfo userInfo;

    @Override
    public OrderDto createOrder(OrderCreateDto dto) {
        Order order = orderMapper.toEntity(dto);

        userInfo = userClientService.findUserByEmail(dto.getEmail());
        order.setUserId(userInfo.getId());

        List<Item> items = itemRepository.findAllById(dto.getItems().stream()
                .map(OrderItemCreateDto::getItemId).toList());
        List<OrderItem> orderItems = mapToOrderItems(order, dto.getItems(), items);

        order.setOrderItems(orderItems);
        order.setTotalPrice(calculateTotalPrice(orderItems));
        order.setStatus(Status.NEW);
        order.setDeleted(false);

        OrderDto orderDto = orderMapper.toDto(orderRepository.save(order));
        orderDto.setUserInfo(userInfo);
        return orderDto;
    }

    @Transactional
    @Override
    public OrderDto updateById(Long id, Status status) {
        Order order = orderRepository.findById(id).orElseThrow(OrderNotFoundException::new);

        userInfo = userClientService.findUserById(order.getUserId());
        if (!Status.isValid(status)) throw new InvalidOrderStatusException();
        order.setStatus(status);

        OrderDto dto = orderMapper.toDto(orderRepository.save(order));
        dto.setUserInfo(userInfo);
        return dto;
    }

    @Override
    public OrderDto findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(OrderNotFoundException::new);
        OrderDto dto = orderMapper.toDto(order);

        userInfo = userClientService.findUserById(order.getUserId());
        dto.setUserInfo(userInfo);

        return dto;
    }

    @Override
    public List<OrderDto> findByUserId(Long id) {
        List<Order> orders = orderRepository.findByUserId(id);

        userInfo = userClientService.findUserById(id);

        return orders.stream().map(order -> {
            OrderDto orderDto = orderMapper.toDto(order);
            orderDto.setUserInfo(userInfo);
            return orderDto;
        }).toList();
    }

    @Override
    public List<OrderDto> findBySelfId() {
        userInfo = userClientService.findUserBySelfId();

        List<OrderDto> dto = orderRepository.findByUserId(userInfo.getId()).stream()
                .map(orderMapper::toDto).toList();

        return dto.stream().peek(orderDto -> orderDto.setUserInfo(userInfo)).toList();
    }

    @Override
    public Page<OrderDto> findAll(int page, int size, Instant startDate, Instant endDate, Status status) {
        Specification<Order> specification = Specification.where(OrderSpecifications
                .hasDate(startDate, endDate)).and(OrderSpecifications.hasStatus(status));

        Page<Order> orders = orderRepository.findAll(specification, PageRequest.of(page, size));

        List<Long> ids = orders.stream()
                .map(Order::getUserId)
                .distinct()
                .toList();

        List<UserInfo> users = userClientService.findAllUsersById(ids);

        Map<Long, UserInfo> userInfoMap = users.stream()
                .collect(Collectors.toMap(UserInfo::getId, u -> u));

        return orders.map(order -> {
            OrderDto dto = orderMapper.toDto(order);
            dto.setUserInfo(userInfoMap.get(order.getUserId()));
            return dto;
        });
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        int rows = orderRepository.softDeleteById(id);
        if (rows==0) throw new OrderNotFoundException();
    }

    private BigDecimal calculateTotalPrice(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderItem ->
                        orderItem.getItem().getPrice()
                                .multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<OrderItem> mapToOrderItems(Order order, List<OrderItemCreateDto> dtoItems,
                                           List<Item> items) {
        Map<Long, Item> itemMap = items.stream()
                .collect(Collectors.toMap(Item::getId, i -> i));

        return dtoItems.stream()
                .map(dtoItem -> {
                    Item item = itemMap.get(dtoItem.getItemId());
                    if (item == null) throw new ItemNotFoundException();
                    if(item.getDeleted()) throw new DeletedItemException(item.getId());

                    OrderItem orderItem = new OrderItem();
                    orderItem.setItem(item);
                    orderItem.setQuantity(dtoItem.getQuantity());
                    orderItem.setOrder(order);
                    return orderItem;
                }).toList();
    }

}
