package com.estudo.aula.repositories;

import com.estudo.aula.entities.Product;
import com.estudo.aula.tests.Factory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.data.domain.Pageable;
import java.util.Optional;

@DataJpaTest
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private EntityManager entityManager;


    private Long existingId;
    private Long countTotalProducts;
    private Long nonExistingId;

    //Fixtures que serve para iniciar as variáveis antes de cada teste.
    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;

        // aqui passamos a quantidade exata cadastrada de produtos no banco.
        countTotalProducts = 25L;
    }

    //verifica se o Get paginado retorna produtos quando o ID existe no banco de dados.
    @Test
    public void findAllShouldReturnPagedProducts(){

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        Page<Product> result = repository.findAll(pageable);

        Assertions.assertEquals(0, result.getNumber());
        Assertions.assertEquals(10, result.getNumberOfElements());
        Assertions.assertEquals(countTotalProducts.longValue(), result.getTotalElements());
        Assertions.assertEquals(3, result.getTotalPages());
    }


    //verifica se o metodo findById não retorna vazio quando o id existe no banco de dados.
    @Test
    public void findByIdShouldReturnNonEmptyOptionalWhenIdExists() {
        Optional<Product> result = repository.findById(existingId);

        Assertions.assertTrue(result.isPresent());
    }

    // Verifica se findById retorna um Optional vazio quando o ID não existe.
    @Test
    public void findByIdShouldReturnEmptyOptionalWhenIdDoesNotExists(){

        Optional<Product> result = repository.findById(nonExistingId);

        Assertions.assertTrue(result.isEmpty());
    }

    //testando save para um novo produto, verificando se o id é auto incrementado.
    @Test
    public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
        Product product = Factory.createProduct();
        product.setId(null);

        product = repository.save(product);

        Assertions.assertNotNull(product.getId());
        Assertions.assertEquals(countTotalProducts + 1, product.getId());
    }


    // vertifica se o update funciona corretamente quando o ID existe no banco de dados.
    @Test
    public void saveShouldUpdateProductWhenIdExists(){

        Product product = repository.findById(existingId).orElseThrow();

        product.setName("Updated Product Name");
        product.setPrice(1200.0);

        repository.saveAndFlush(product);
        entityManager.clear();

        Product result = repository.findById(existingId).orElseThrow();

        Assertions.assertEquals(existingId, result.getId());
        Assertions.assertEquals("Updated Product Name", result.getName());
        Assertions.assertEquals(1200.0, result.getPrice().doubleValue());
    }


    //Teste para verificar se o método delete funciona corretamente quando o ID existe no banco de dados.
    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {

        repository.deleteById(existingId);

        Optional<Product> result = repository.findById(existingId);

        Assertions.assertFalse(result.isPresent());

    }




}
