package net.sergey.diplom.dto.airfoil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;


@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AirfoilEdit {
    private String airfoilName;
    private String shortName;
    private String details;
    private String viewCsv;
    private List<Data> data;
}
