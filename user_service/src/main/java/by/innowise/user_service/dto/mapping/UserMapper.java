package by.innowise.user_service.dto.mapping;

import by.innowise.user_service.dto.UserDto;
import by.innowise.user_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = PaymentCardMapper.class)
public interface UserMapper {

    @Mapping(target = "paymentCards", source = "paymentCards")
    UserDto toUserDto(User user);

    User toUser (UserDto userDto);

}
