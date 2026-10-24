package com.estudo.aula.services;

import com.estudo.aula.dto.CategoryDTO;
import com.estudo.aula.entities.Category;
import com.estudo.aula.repositories.CategoryesRepository;
import com.estudo.aula.services.Exceptions.DatabaseException;
import com.estudo.aula.services.Exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryesRepository repository;

    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAll(Pageable pageable) {
        Page<Category> result = repository.findAll(pageable);
        return result.map(x -> new CategoryDTO(x));
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id){
        Category category = repository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Recurso não encontrado")
        );
        return new CategoryDTO(category);
    }

    @Transactional
    public CategoryDTO insert(CategoryDTO dto){
        Category category = new Category();
        category.setName(dto.getName());
        category = repository.save(category);
        return new CategoryDTO(category);

    }

    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto){
        Category category = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado")
        );
        category.setName(dto.getName());
        category = repository.save(category);
        return new CategoryDTO(category);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if(!repository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }try{
            repository.deleteById(id);
        }catch (DataIntegrityViolationException e){
            throw new DatabaseException("Falha de integridade referencial");
        }

    }
}
