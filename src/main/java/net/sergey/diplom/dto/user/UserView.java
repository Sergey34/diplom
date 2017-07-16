package net.sergey.diplom.dto.user;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserView {

    private String name;

    private String password;

    private List<String> roles;
}

