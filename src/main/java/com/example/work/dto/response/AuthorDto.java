package com.example.work.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class AuthorDto{

    private Long id;
    private String cpf;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;
    private String email;
    private String name;
    private String countryOfOrigin;
    private String gender;
    private List<WorkDto> works;
    public AuthorDto() {
    }
}


