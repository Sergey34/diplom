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
public class Data {
    private String fileName;
    private String data;
    private String alpha;
    private double reynolds;
    private double nCrit;
    private double maxClCd;
}