package by.innowise.user_service.dto.mapping;

import by.innowise.user_service.dto.PaymentCardDto;
import by.innowise.user_service.entity.PaymentCard;
import by.innowise.user_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper (componentModel = "spring")
public interface PaymentCardMapper {

    @Mapping(source = "user.id", target = "userId")
    PaymentCardDto toPaymentCardDto(PaymentCard paymentCard);

    @Mapping(source = "userId", target = "user")
    PaymentCard toPaymentCard(PaymentCardDto paymentCardDto);

    default User map(Long userId) {
        if (userId == null) return null;
        User user = new User();
        user.setId(userId);
        return user;
    }
}
