package com.example.work.converter;

import com.example.work.dto.request.WorkRequest;
import com.example.work.dto.response.WorkDto;
import com.example.work.model.WorkModel;

public class WorkConverter {
    public static WorkDto toDto(WorkModel workModel) {
        return WorkDto.builder()
                .id(workModel.getId())
                .workName(workModel.getWorkName())
                .workDescription(workModel.getWorkDescription())
                .publicationDate(workModel.getPublicationDate())
                .exhibitionDate(workModel.getExhibitionDate())
                .build();
    }
}