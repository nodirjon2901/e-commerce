package uz.nt.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.nt.ecommerce.domain.dto.BaseResponse;
import uz.nt.ecommerce.domain.dto.request.AuthDto;
import uz.nt.ecommerce.domain.dto.request.UserRequest;
import uz.nt.ecommerce.domain.dto.request.UserStateDto;
import uz.nt.ecommerce.domain.entity.UserEntity;
import uz.nt.ecommerce.domain.entity.enums.UserRole;
import uz.nt.ecommerce.domain.entity.enums.UserState;
import uz.nt.ecommerce.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements BaseService<UserEntity, UserRequest> {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    @Override
    public BaseResponse<UserEntity> save(UserRequest userRequest) {
        System.out.println(userRequest);
        UserEntity userEntity = modelMapper.map(userRequest, UserEntity.class);
        try {
            userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
            userEntity.setRoles(userEntity.getRoles());
            UserEntity entity = userRepository.save(userEntity);
            return new BaseResponse<>("Successfully created", 200, entity, true);
        } catch (Exception e) {
            return new BaseResponse<>("Failed creating new user!", 400, null, false);
        }
    }

    public BaseResponse<UserEntity> saveBot(UserRequest userRequest) {
        System.out.println(userRequest);
        UserEntity userEntity = modelMapper.map(userRequest, UserEntity.class);
        try {
            userEntity.setState(UserState.START);
            UserEntity entity = userRepository.save(userEntity);
            System.out.println(entity);
            return new BaseResponse<>("Successfully created", 200, entity, true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new BaseResponse<>("Failed creating new user!", 400, null, false);
        }
    }


    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserEntity update(UserRequest request, Long id) {
        Optional<UserEntity> userEntity = userRepository.findById(id);
        userEntity.get().setName(request.getName());
        userEntity.get().setUsername(request.getUsername());
        userEntity.get().setBalance(request.getBalance());
        return userRepository.save(userEntity.get());
    }

    @Override
    public BaseResponse<UserEntity> getById(Long id) {
        if (userRepository.findById(id).isPresent()) {
            UserEntity userEntity = userRepository.findById(id).get();
            return new BaseResponse<>("User found", 200, userEntity, true);
        }
        return new BaseResponse<>("User not found", 404, null, false);

    }

    public UserEntity findByChatId(String chatId) {
        if (userRepository.findByChatId(chatId).isPresent()) {
            return userRepository.findByChatId(chatId).get();
        }
        return null;
    }

    @Override
    public List<UserEntity> getAll() {
        List<UserEntity> users = new ArrayList<>();
        for (UserEntity user : userRepository.findAll()) {
            if (!user.getRoles().contains(UserRole.SUPER_ADMIN)) {
                users.add(user);
            }
        }
        return users;
    }

    public BaseResponse<UserEntity> login(AuthDto authDto) {
        UserEntity userEntity = userRepository.findByUsername(authDto.getUsername()).get();

        if (!passwordEncoder.matches(authDto.getPassword(), userEntity.getPassword())) {
            return new BaseResponse<>("Wrong username and/or password", 404, null, false);
        }
        Collection<? extends GrantedAuthority> authorities = userEntity.getAuthorities();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userEntity.getUsername(),
                null,
                authorities
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new BaseResponse<>("Success", 200, userEntity, true);
    }


    public BaseResponse<UserEntity> getByUsername(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            UserEntity userEntity = userRepository.findByUsername(username).get();
            return new BaseResponse<>("User found", 200, userEntity, true);
        }
        return new BaseResponse<>("User not found", 404, null, true);

    }

    public void updateState(UserStateDto stateDTO) {
        if (userRepository.findByChatId(stateDTO.chatId()).isPresent()) {
            UserEntity userEntity = userRepository.findByChatId(stateDTO.chatId()).get();
            userEntity.setState(stateDTO.state());
            if (!userEntity.getState().equals(UserState.START)) {
                UserEntity userEntity1 = userRepository.findByChatId(stateDTO.chatId()).get();
                userEntity1.setState(stateDTO.state());
                userRepository.save(userEntity1);
            }
        }
    }

    public BaseResponse<UserStateDto> getUserState(String chatId) {
        Optional<UserEntity> byChatId = userRepository.findByChatId(chatId);
        if (byChatId.isPresent()) {
            UserEntity userEntity = byChatId.get();
            UserStateDto userStateDto = new UserStateDto(userEntity.getChatId(), userEntity.getState());
            return new BaseResponse<>("Success", 200, userStateDto, true);
        }
        return new BaseResponse<>("Fail", 404, null, false);
    }
}