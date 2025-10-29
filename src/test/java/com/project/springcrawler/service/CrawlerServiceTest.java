package com.project.springcrawler.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.springcrawler.crawler.CrawlerStrategy;
import com.project.springcrawler.dto.CategoriaDto;
import com.project.springcrawler.dto.ProductoDto;
import com.project.springcrawler.entity.Categoria;
import com.project.springcrawler.entity.Producto;
import com.project.springcrawler.repository.RepositorioCategoria;
import com.project.springcrawler.repository.RepositorioProducto;

@ExtendWith(MockitoExtension.class)
class CrawlerServiceTest {

    @Mock
    private RepositorioProducto repositorioProducto;

    @Mock
    private RepositorioCategoria repositorioCategoria;

    @Mock
    private CrawlerStrategy estrategiaCrawler;

    @InjectMocks
    private ServicioCrawler servicioCrawler;

    private ProductoDto dtoProducto;
    private CategoriaDto dtoCategoria;
    private Producto producto;

    @BeforeEach
    void configurar() {
        servicioCrawler.estrategiasCrawler = Arrays.asList(estrategiaCrawler);
        
        dtoProducto = new ProductoDto();
        dtoProducto.setSku("TEST123");
        dtoProducto.setNombre("Producto Prueba");
        dtoProducto.setPrecioActual(new BigDecimal("99.99"));
        dtoProducto.setDisponibilidad("Disponible");
        dtoProducto.setUrlOrigen("https://test.com/producto");
        
        producto = new Producto();
        producto.setSku("TEST123");
        producto.setNombre("Producto Prueba");
        producto.setPrecioActual(new BigDecimal("99.99"));
        
        dtoCategoria = new CategoriaDto();
        dtoCategoria.setNombre("Categoria Prueba");
        dtoCategoria.setUrlOrigen("https://test.com/categoria");
    }

    @Test
    void crawlearYGuardarProducto_ProductoNuevo_Exitoso() {
        when(estrategiaCrawler.puedeManear(anyString())).thenReturn(true);
        when(estrategiaCrawler.crawlearProducto(anyString())).thenReturn(dtoProducto);
        when(repositorioProducto.findBySku(anyString())).thenReturn(Optional.empty());
        when(repositorioProducto.save(any(Producto.class))).thenReturn(producto);

        ProductoDto resultado = servicioCrawler.crawlearYGuardarProducto("https://test.com/producto");

        assertNotNull(resultado);
        assertEquals("TEST123", resultado.getSku());
        verify(repositorioProducto).save(any(Producto.class));
    }

    @Test
    void crawlearYGuardarProducto_ProductoExistente_ActualizaProducto() {
        Producto productoExistente = new Producto();
        productoExistente.setSku("TEST123");
        productoExistente.setPrecioActual(new BigDecimal("89.99"));
        
        when(estrategiaCrawler.puedeManear(anyString())).thenReturn(true);
        when(estrategiaCrawler.crawlearProducto(anyString())).thenReturn(dtoProducto);
        when(repositorioProducto.findBySku("TEST123")).thenReturn(Optional.of(productoExistente));
        when(repositorioProducto.save(any(Producto.class))).thenReturn(productoExistente);

        ProductoDto resultado = servicioCrawler.crawlearYGuardarProducto("https://test.com/producto");

        assertNotNull(resultado);
        verify(repositorioProducto).save(productoExistente);
        assertEquals(new BigDecimal("89.99"), productoExistente.getPrecioAnterior());
    }

    @Test
    void crawlearYGuardarProducto_EstrategiaNoEncontrada_LanzaExcepcion() {
        when(estrategiaCrawler.puedeManear(anyString())).thenReturn(false);

        IllegalArgumentException excepcion = assertThrows(
            IllegalArgumentException.class,
            () -> servicioCrawler.crawlearYGuardarProducto("https://desconocido.com/producto")
        );

        assertTrue(excepcion.getMessage().contains("No se encontró estrategia de crawler"));
    }

    @Test
    void crawlearYGuardarCategoria_Exitoso() {
        ProductoDto productoCategoria = new ProductoDto();
        productoCategoria.setSku("CAT123");
        productoCategoria.setNombre("Producto Categoria");
        dtoCategoria.setProductos(Arrays.asList(productoCategoria));

        when(estrategiaCrawler.puedeManear(anyString())).thenReturn(true);
        when(estrategiaCrawler.crawlearCategoria(anyString())).thenReturn(dtoCategoria);
        when(repositorioCategoria.save(any(Categoria.class))).thenReturn(new Categoria());
        when(repositorioProducto.findBySku(anyString())).thenReturn(Optional.empty());
        when(repositorioProducto.save(any(Producto.class))).thenReturn(new Producto());

        CategoriaDto resultado = servicioCrawler.crawlearYGuardarCategoria("https://test.com/categoria");

        assertNotNull(resultado);
        assertEquals("Categoria Prueba", resultado.getNombre());
        verify(repositorioCategoria).save(any(Categoria.class));
        verify(repositorioProducto).save(any(Producto.class));
    }

    @Test
    void crawlearYGuardarCategoria_EstrategiaNoEncontrada_LanzaExcepcion() {
        when(estrategiaCrawler.puedeManear(anyString())).thenReturn(false);

        IllegalArgumentException excepcion = assertThrows(
            IllegalArgumentException.class,
            () -> servicioCrawler.crawlearYGuardarCategoria("https://desconocido.com/categoria")
        );

        assertTrue(excepcion.getMessage().contains("No se encontró estrategia de crawler"));
    }
}