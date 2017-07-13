package net.sergey.diplom.dto.airfoil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Data {
    private String fileName;
    private String data;
    private double reynolds;
    private double nCrit;
    private double maxClCd;
}