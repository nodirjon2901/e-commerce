package uz.nt.ecommerce;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;
import uz.nt.ecommerce.domain.entity.UserEntity;
import uz.nt.ecommerce.domain.entity.enums.UserRole;
import uz.nt.ecommerce.repository.UserRepository;

import java.util.List;

@SpringBootApplication
@EnableJpaAuditing
public class ECommerceApplication implements CommandLineRunner {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    public ECommerceApplication(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public static void main(String[] args) {

        SpringApplication.run(ECommerceApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("qwer@gmail.com").isEmpty()) {
            UserEntity user = UserEntity.builder()
                    .roles(List.of(UserRole.SUPER_ADMIN))
                    .name("qwer")
                    .username("qwer@gmail.com")
                    .balance(1.0)
                    .password(passwordEncoder.encode("qwer"))
                    .build();
            userRepository.save(user);
        }
    }
}
