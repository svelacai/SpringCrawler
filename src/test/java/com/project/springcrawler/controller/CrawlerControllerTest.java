package com.project.springcrawler.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.project.springcrawler.dto.CategoriaDto;
import com.project.springcrawler.dto.ProductoDto;
import com.project.springcrawler.service.ServicioCrawler;

@WebMvcTest(CrawlerController.class)
class CrawlerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServicioCrawler servicioCrawler;

    @Test
    void crawlearProducto_Success() throws Exception {
        ProductoDto producto = new ProductoDto();
        producto.setSku("TEST123");
        producto.setNombre("Producto Test");
        producto.setPrecioActual(new BigDecimal("100"));

        when(servicioCrawler.crawlearYGuardarProducto(anyString())).thenReturn(producto);

        mockMvc.perform(post("/api/crawler/product")
                .param("url", "https://test.com/product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("TEST123"))
                .andExpect(jsonPath("$.nombre").value("Producto Test"));
    }

    @Test
    void crawlearProducto_Error() throws Exception {
        when(servicioCrawler.crawlearYGuardarProducto(anyString()))
                .thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/crawler/product")
                .param("url", "https://test.com/product"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crawlearCategoria_Success() throws Exception {
        CategoriaDto categoria = new CategoriaDto();
        categoria.setNombre("Categoria Test");
        categoria.setProductos(new ArrayList<>());

        when(servicioCrawler.crawlearYGuardarCategoria(anyString())).thenReturn(categoria);

        mockMvc.perform(post("/api/crawler/category")
                .param("url", "https://test.com/category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Categoria Test"));
    }

    @Test
    void crawlearCategoria_Error() throws Exception {
        when(servicioCrawler.crawlearYGuardarCategoria(anyString()))
                .thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/crawler/category")
                .param("url", "https://test.com/category"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void salud_Success() throws Exception {
        mockMvc.perform(get("/api/crawler/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Servicio crawler funcionando"));
    }
}