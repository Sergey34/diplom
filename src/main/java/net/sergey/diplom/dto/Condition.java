package net.sergey.diplom.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Condition {
    private String action;
    private String attrName;
    private double value;
}
