package com.estudo.aula.services;

import com.estudo.aula.dto.CategoryDTO;
import com.estudo.aula.entities.Category;
import com.estudo.aula.repositories.CategoryesRepository;
import com.estudo.aula.services.Exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryesRepository repository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll() {
        List<Category> result = repository.findAll( );
        return result.stream().map(x -> new CategoryDTO(x))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id){
        Category category = repository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Recurso não encontrado")
        );
        return new CategoryDTO(category);
    }
}
