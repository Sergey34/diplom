package base.dto;

import lombok.Data;

import java.util.List;

@Data
public class MenuDto {
    private String header;
    private List<MenuItemDto> menuItems;
}
