package com.estudo.aula.services;


import com.estudo.aula.dto.ProductDTO;
import com.estudo.aula.repositories.ProductRepository;
import com.estudo.aula.services.Exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional // grante o rollback após cada teste, para não poluir o banco de dados.
public class ProductServiceIT {

    @Autowired
    private ProductService service;

    @Autowired
    private ProductRepository repository;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalProducts;

    @BeforeEach
    void setUp() throws Exception {

        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;


    }


    //verifica se retorna a página paginada.
    @Test
    public void findAllPagedshouldReturningPageWhenPage0Size10() {

        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<ProductDTO> result = service.findAllPaged(pageRequest);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(0, result.getNumber());
        Assertions.assertEquals(10, result.getSize());
        Assertions.assertEquals(countTotalProducts, result.getTotalElements());

    }

    //verifica se retorna pagina vazia quando a página solicitada não existe.
    @Test
    public void findAllPagedshouldReturningEmptyPageWhenPageDoesNotExist() {

        PageRequest pageRequest = PageRequest.of(1000, 10);

        Page<ProductDTO> result = service.findAllPaged(pageRequest);

        Assertions.assertTrue(result.isEmpty());

    }

    //verifica se a pagina esta ordenada.
    @Test
    public void findAllPagedshouldReturningSortedPage() {

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));

        Page<ProductDTO> result = service.findAllPaged(pageRequest);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName());
        Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
        Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
    }


    //verifica se o deleteById deleta o produto quando o id existe no banco de dados.
    @Test
    public void deleteShouldDeleteResourceWhenIdExists() {

        service.delete(existingId);

        Assertions.assertEquals(countTotalProducts - 1, repository.count());

    }

    //verifica se o deleteById lança uma exceção quando o id não existe no banco de dados.
    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });
    }
}
