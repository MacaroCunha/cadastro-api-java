package com.example.obra.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ObraDto {

    private Long id;
    private String nomeObra;
    private String descObra;
    private Date dataPub;
    private Date dataExpo;
}

