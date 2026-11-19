package com.estudo.aula.services;


import com.estudo.aula.dto.ProductDTO;
import com.estudo.aula.entities.Category;
import com.estudo.aula.entities.Product;
import com.estudo.aula.repositories.CategoryesRepository;
import com.estudo.aula.repositories.ProductRepository;
import com.estudo.aula.services.Exceptions.DatabaseException;
import com.estudo.aula.services.Exceptions.ResourceNotFoundException;
import com.estudo.aula.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    @Mock
    private CategoryesRepository categoryRepository;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;
    private PageImpl<Product> page;
    private Product product;

    @BeforeEach
    void setUp() throws Exception {


        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        product = Factory.createProduct();
        page = new PageImpl<>(List.of(product));

        //simulação Get Paginado
        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
        //simulando Get por Id existente
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
        //simulando Get por Id não existente
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        //simulação Inset
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

        //simula update
        Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);
        Mockito.when(categoryRepository.getOne(2L))
                        .thenReturn(new Category(2L, "Eletrônicos"));

        //delete
        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
        Mockito.when(repository.getReferenceById(nonExistingId))
                .thenThrow(ResourceNotFoundException.class);

        //simulado existsById
        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
        Mockito.when(repository.existsById(dependentId)).thenReturn(true);

    }

    //Verifica se o Get Paginado retorna uma página de produtos.
    @Test
    public void findAllPagedShouldReturnPage(){

        Pageable pageable = PageRequest.of(0, 10);

        Page<ProductDTO> result = service.findAllPaged(pageable);

        Assertions.assertNotNull(result);

        Mockito.verify(repository).findAll(pageable);
    }

    //verifica se o Get por Id existente retorna um ProductDTO.
    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists(){

        ProductDTO result = service.findById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(product.getId(), result.getId());
        Assertions.assertEquals(product.getName(), result.getName());

        Mockito.verify(repository).findById(existingId);
    }

    //verifica se o Get por Id não existente lança a exceção ResourceNotFoundException.
    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist(){
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
                    service.findById(nonExistingId);
                });
        Mockito.verify(repository).findById(nonExistingId);
    }

    //verifica se o insert retorna um ProductDTO.
    @Test
    public void insertShouldReturnProductDTOWhenValidData(){
        ProductDTO dto = Factory.createProductDTO();
        dto.setId(null);

        ProductDTO result = service.insert(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(product.getId(), result.getId());
        Assertions.assertEquals(dto.getName(), result.getName());
        Assertions.assertEquals(dto.getPrice(), result.getPrice());

        Mockito.verify(categoryRepository).getOne(2L);
        Mockito.verify(repository).save(ArgumentMatchers.any(Product.class));
    }


    //verifica se o update por Id existente retorna um ProductDTO.
    @Test
    public void updateShouldReturnProductDTOWhenIdExists(){

        ProductDTO dto = Factory.createProductDTO();
        dto.setName("Updated Product");
        dto.setPrice(1200.0);

        ProductDTO result = service.update(existingId, dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getId());
        Assertions.assertEquals(dto.getName(), result.getName());
        Assertions.assertEquals(dto.getPrice(), result.getPrice());

        Mockito.verify(repository).getReferenceById(existingId);
        Mockito.verify(categoryRepository).getOne(2L);
        Mockito.verify(repository).save(product);
    }

    //verifica se o update por Id não existente lança a exceção ResourceNotFoundException.
    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist(){
        ProductDTO dto = Factory.createProductDTO();

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.update(nonExistingId, dto);
        });

        Mockito.verify(repository).getReferenceById(nonExistingId);
        Mockito.verify(repository, Mockito.never()).save(ArgumentMatchers.any(Product.class));
    }


    //verifica se o delete lança a exceção DatabaseException quando o id é dependente de outra entidade.
    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId(){

        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(dependentId);
        });

    }

    // verifica se o delete lança a exceção ResourceNotFoundException quando o id não existe.
    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });

    }

    // verifica se o delete foi chamado para um id existente, deletando corretamente.
    @Test
    public void deleteShouldDoNothingWhenIdExists() {

        Assertions.assertDoesNotThrow(() -> {;
            service.delete(existingId);
        });

        Mockito.verify(repository,Mockito.times(1)).deleteById(existingId);
    }
}
