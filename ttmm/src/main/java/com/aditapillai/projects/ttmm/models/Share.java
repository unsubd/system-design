package com.aditapillai.projects.ttmm.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Share {
    private final String userId;
    private final double paid;
    private final double toPay;
}
