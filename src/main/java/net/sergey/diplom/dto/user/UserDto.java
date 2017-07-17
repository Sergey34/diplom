package net.sergey.diplom.dto.user;

import lombok.*;
import net.sergey.diplom.domain.user.Authorities;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDto {
    private String userName;
    private List<Authorities> userRoles;
}
