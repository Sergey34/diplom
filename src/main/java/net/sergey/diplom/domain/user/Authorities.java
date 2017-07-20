package net.sergey.diplom.domain.user;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@EqualsAndHashCode(of = "authority")
public class Authorities implements GrantedAuthority {
    private String authority;
}
