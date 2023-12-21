package com.springbackend.training.Controladores.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrackDTO {
    private Integer trackNro;
    private String trackName;
    private String previewURL;
}
