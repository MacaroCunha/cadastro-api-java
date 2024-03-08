package com.example.work.service;

import com.example.work.converter.AuthorConverter;
import com.example.work.dto.AuthorDto;
import com.example.work.dto.error.ResponseMessage;
import com.example.work.dto.request.AuthorRequest;
import com.example.work.exception.AuthorException;
import com.example.work.exception.BusinessException;
import com.example.work.message.AuthorMessage;
import com.example.work.model.AuthorModel;
import com.example.work.repository.AuthorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorConverter converter;

    public List<AuthorDto> getAllAuthors() {
        List<AuthorDto> authorsDTO = authorRepository.findAll()
                .stream()
                .map(converter::convertToDTO)
                .collect(Collectors.toList());

        if (authorsDTO.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, AuthorMessage.AUTHORS_NOT_FOUND);
        }

        return authorsDTO;
    }

    public AuthorDto getAuthorById(Long id) {
        return authorRepository.findById(id)
                .map(converter::convertToDTO)
                .orElseThrow(() -> new AuthorException(String.format(AuthorMessage.AUTHOR_NOT_FOUND, id)));
    }

    public AuthorDto updateAuthor(Long id, AuthorDto authorDto) {
        AuthorModel existingAuthor = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorException(String.format(AuthorMessage.AUTHOR_NOT_FOUND, id)));

        updateExistingAuthor(existingAuthor, authorDto);

        AuthorModel updatedAuthor = authorRepository.save(existingAuthor);
        return converter.convertToDTO(updatedAuthor);
    }

    @Transactional
    public ResponseMessage createAuthor(AuthorRequest newAuthorDto) {
        Optional<AuthorModel> findCpf = this.authorRepository.existsByCpf(newAuthorDto.getCpf());
        Optional<AuthorModel> findEmail = this.authorRepository.existsByEmail(newAuthorDto.getEmail());

        if (findEmail.isPresent()) {
            throw new BusinessException(AuthorMessage.DUPLICATE_EMAIL, AuthorMessage.EMAIL_ALREADY_REGISTERED);
        }
        if (findCpf.isPresent()) {
            throw new BusinessException(AuthorMessage.DUPLICATE_CPF, AuthorMessage.CPF_ALREADY_REGISTERED);
        }
        AuthorModel newAuthorModel = converter.convert(newAuthorDto);
        assert newAuthorModel != null;
        authorRepository.save(newAuthorModel);
        return ResponseMessage.builder().message(AuthorMessage.CREATED_AUTOR).build();
    }
    private void updateExistingAuthor(AuthorModel existingAuthor, AuthorDto authorDto) {
        existingAuthor.setName(authorDto.getName());
        existingAuthor.setCpf(authorDto.getCpf());
        existingAuthor.setBirthDate(authorDto.getBirthDate());
        existingAuthor.setEmail(authorDto.getEmail());
        existingAuthor.setCountryOfOrigin(authorDto.getCountryOfOrigin());
        existingAuthor.setGender(authorDto.getGender());
    }
}