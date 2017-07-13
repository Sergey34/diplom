package net.sergey.diplom.dto.airfoil;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@lombok.Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AirfoilDTO {
    private String name;
    private String description;
    private String image;
    private String shortName;
}
