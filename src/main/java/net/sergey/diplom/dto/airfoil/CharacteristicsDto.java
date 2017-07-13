package net.sergey.diplom.dto.airfoil;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacteristicsDto {
    private String coordinatesStl;
    private String fileName;
    private String filePath;
    private double renolgs;
    private double nCrit;
    private String maxClCd;
}
