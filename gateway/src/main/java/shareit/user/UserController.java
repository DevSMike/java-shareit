package shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @GetMapping()
    public ResponseEntity<Object> getAllUsers() {
        log.debug("Gateway: Getting all users");
        return userClient.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable @Positive long userId) {
        log.debug("Gateway: Getting user by id: {}", userId);
        return userClient.getUserById(userId);
    }

    @PostMapping()
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserDto userDto) {
        log.debug("Gateway: Creating user: {}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable @Positive long userId, @Valid @RequestBody UserDto userDto) {
        log.debug("Gateway: Updating user by id: {}", userId);
        userDto.setId(userId);
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable @Positive long userId) {
        log.debug("Gateway: Deleting user by id : {}", userId);
        return userClient.deleteUser(userId);
    }
}
