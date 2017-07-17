package net.sergey.diplom.dto.airfoil;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@lombok.Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CharacteristicsDto {
    private String coordinatesStl;
    private String fileName;
    private String filePath;
    private String alpha;
    private double renolgs;
    private double nCrit;
    private double maxClCd;
}
