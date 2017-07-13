package net.sergey.diplom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Condition {
    private String action;
    private String attrName;
    private double value;
}
