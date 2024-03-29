package com.example.work.service;

import com.example.work.converter.WorkConverter;
import com.example.work.converter.WorkPostConverter;
import com.example.work.dto.request.WorkRequest;
import com.example.work.dto.response.WorkDto;
import com.example.work.exception.WorkException;
import com.example.work.message.WorkMessage;
import com.example.work.model.WorkModel;
import com.example.work.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkService {
    private final Logger logger = LoggerFactory.getLogger(WorkService.class);

    private final WorkRepository workRepository;
    private final WorkPostConverter workPostConverter;

    public List<WorkDto> getAllWorks() {
        List<WorkModel> works = workRepository.findAll();
        if (works.isEmpty()) {
            throw new WorkException(WorkMessage.WORKS_NOT_FOUND);
        }
        return works.stream()
                .map(WorkConverter::toDto)
                .collect(Collectors.toList());
    }

    public Optional<WorkDto> getWorkById(Long id) {
        return workRepository.findById(id).map(WorkConverter::toDto);
    }

    @Transactional
    public WorkDto createWork(@Validated WorkRequest workRequest) {
        try {
            WorkModel newWork = workPostConverter.convert(workRequest);
            assert newWork != null;
            WorkModel savedWork = workRepository.save(newWork);
            logger.info("Work created successfully. ID: {}", savedWork.getId());
            return WorkConverter.toDto(savedWork);
        } catch (Exception e) {
            logger.error("Error creating work", e);
            throw new WorkException(WorkMessage.CREATED_WORK);
        }
    }

    public WorkDto updateWork(Long id, @Validated WorkRequest workRequest) {
        try {
            return workRepository.findById(id)
                    .map(existingWork -> {
                        updateEntityFromRequest(existingWork, workRequest);
                        WorkModel updatedWork = workRepository.save(existingWork);
                        logger.info("Work updated successfully. ID: {}", updatedWork.getId());
                        return WorkConverter.toDto(updatedWork);
                    })
                    .orElseThrow(() -> new IllegalArgumentException(String.format(WorkMessage.WORK_NOT_FOUND, id)));
        } catch (Exception e) {
            logger.error("Error updating work", e);
            throw new WorkException(WorkMessage.UPDATED_WORK);
        }
    }

    public void deleteWork(Long id) {
        workRepository.findById(id).ifPresentOrElse(
                work -> {
                    workRepository.delete(work);
                    logger.info(WorkMessage.DELETED_WORK);
                },
                () -> {
                    logger.error("Work not found for deletion. ID: {}", id);
                    throw new IllegalArgumentException(String.format(WorkMessage.WORK_NOT_FOUND, id));
                }
        );
    }

    private void updateEntityFromRequest(WorkModel existingWork, WorkRequest workRequest) {
        existingWork.setWorkName(workRequest.getWorkName());
        existingWork.setWorkDescription(workRequest.getWorkDescription());
        existingWork.setPublicationDate(workRequest.getPublicationDate());
        existingWork.setExhibitionDate(workRequest.getExhibitionDate());
    }
}

