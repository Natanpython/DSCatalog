package com.estudo.aula.controller;

import com.estudo.aula.dto.ProductDTO;
import com.estudo.aula.services.Exceptions.DatabaseException;
import com.estudo.aula.services.Exceptions.ResourceNotFoundException;
import com.estudo.aula.services.ProductService;
import com.estudo.aula.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService service;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductDTO productDTO;
    private PageImpl<ProductDTO> page;

    private Long existingId;
    private Long nonExistingId;

    private Long dependentId;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;

        productDTO = Factory.createProductDTO();
        page = new PageImpl<>(List.of(productDTO));

        //Get
        Mockito.when(service.findAllPaged(any())).thenReturn(page);
        Mockito.when(service.findById(existingId)).thenReturn(productDTO);
        Mockito.when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        //update
        Mockito.when(service.update(eq(existingId), any())).thenReturn(productDTO);
        Mockito.when(service.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);

        //delete
        Mockito.doNothing().when(service).delete(existingId);
        Mockito.doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
        Mockito.doThrow(DatabaseException.class).when(service).delete(dependentId);

        // insert
        Mockito.when(service.insert(any(ProductDTO.class))).thenReturn(productDTO);

    }

    //verifica se o endpoint retorna status 200 e se o retorno é uma página de produtos
    @Test
    public void findAllShouldReturnPage() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/products")
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());

    }

    //verifica se o endpoint retorna status 200 e se o retorno é um produto existente
    @Test
    public void findByIdShouldReturnProductWhenIdExists() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/products/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
    }

    //verifica se o endpoint retorna status 404 quando o id não existe
    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/products/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }

    //verifica se o update retorna status 200 e se o retorno é um produto existente
    @Test
    public void updateShouldReturnProductDTOWhenIdExists() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(put("/products/{id}", existingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
    }

    //verifica se o update retorna status 404 quando o id não existe
    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(put("/products/{id}", nonExistingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());

    }

    //verifica se o delete retorna status 204 quando o id existe
    @Test
    public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
        ResultActions resultActions =
                mockMvc.perform(delete("/products/{id}", existingId));
        resultActions.andExpect(status().isNoContent());
        Mockito.verify(service).delete(existingId);

    }

    //verifica se o delete retorna status 404 quando o id não existe
    @Test
    public void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        ResultActions resultActions =
                mockMvc.perform(delete("/products/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isNotFound());
        Mockito.verify(service).delete(nonExistingId);
    }

    //verifica se o delete retorna status 400 quando o id é dependente
    @Test
    public void deleteShouldReturnBadRequestWhenIdHasDependencies() throws Exception {
        ResultActions result =
                mockMvc.perform(delete("/products/{id}", dependentId)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isBadRequest());
        Mockito.verify(service).delete(dependentId);
    }

    //verifica se o insert retorna status 201 e se o retorno é um produto existente
    @Test
    public void insertShouldReturnProductDTOCreatedWhenValidData() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(post("/products")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());

        Mockito.verify(service).insert(any(ProductDTO.class));
    }

}
