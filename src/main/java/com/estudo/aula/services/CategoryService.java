package com.estudo.aula.services;

import com.estudo.aula.dto.CategoryDTO;
import com.estudo.aula.entities.Category;
import com.estudo.aula.repositories.CategoryesRepository;
import com.estudo.aula.services.Exceptions.DatabaseException;
import com.estudo.aula.services.Exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

    @Transactional
    public CategoryDTO insert(CategoryDTO dto){
        Category category = new Category();
        category.setName(dto.getName());
        category = repository.save(category);
        return new CategoryDTO(category);

    }

    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto){
       try{
              Category category = repository.getReferenceById(id);
                category.setName(dto.getName());
                category = repository.save(category);
                return new CategoryDTO(category);
       }catch (ResourceNotFoundException e){
              throw new ResourceNotFoundException("Recurso não encontrado");
       }
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
