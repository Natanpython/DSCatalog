package com.estudo.aula.repositories;

import com.estudo.aula.entities.Product;
import com.estudo.aula.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

@DataJpaTest
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository repository;


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


    //Teste para verificar se o método delete funciona corretamente quando o ID existe no banco de dados.
    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {

        repository.deleteById(existingId);

        Optional<Product> result = repository.findById(existingId);

        Assertions.assertFalse(result.isPresent());

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

}
