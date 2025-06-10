package com.jcfp.tallererp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CambiarPasswordRequest {
    private String passwordActual;
    private String passwordNueva;

}