package shareit.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDto {

    private Long id;
    private String name;
    @Email(message = "Incorrect email")
    private String email;
}
