package uz.nt.ecommerce.domain.dto.request;


import uz.nt.ecommerce.domain.entity.enums.UserState;

public record UserStateDto(String chatId, UserState state) {
}
