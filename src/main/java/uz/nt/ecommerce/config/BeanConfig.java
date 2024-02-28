package uz.nt.ecommerce.config;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import uz.nt.ecommerce.common.DataNotFoundException;
import uz.nt.ecommerce.domain.dto.request.UserRequest;
import uz.nt.ecommerce.domain.entity.UserEntity;
import uz.nt.ecommerce.domain.entity.enums.UserRole;
import uz.nt.ecommerce.repository.UserRepository;
import uz.nt.ecommerce.service.UserService;

import java.util.Collections;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class BeanConfig {

    private final UserRepository userRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository
                .findByUsername(username)
                .orElseThrow(() -> new DataNotFoundException("Username is not found"));
    }

}
