package net.sergey.diplom.dto.airfoil;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
public class AirfoilDetail extends AirfoilDTO {
    private List<String> imgCsvName;
    private List<String> stlFilePath;
    private List<CharacteristicsDto> characteristics;
    private String coordView;
    private String coordViewPath;

    public void addStlFilePath(String filePath) {
        if (stlFilePath == null) {
            stlFilePath = new ArrayList<>();
        }
        stlFilePath.add(filePath);
    }
}
