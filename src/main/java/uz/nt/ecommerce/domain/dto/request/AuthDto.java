package uz.nt.ecommerce.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuthDto {

    @NotBlank(message = "username cannot be empty or null")
    private String username;

    @NotBlank(message = "password cannot be empty or null")
    @Pattern(regexp = "[\\d+]{8,}")
    private String password;

}
