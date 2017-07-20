package net.sergey.diplom.dto.airfoil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;


@lombok.Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AirfoilEdit {
    private String airfoilName;
    private String shortName;
    private String details;
    private String viewCsv;
    private List<Data> data;
}
