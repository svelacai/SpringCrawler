package com.project.springcrawler.controller;

import com.project.springcrawler.dto.CategoriaDto;
import com.project.springcrawler.dto.ProductoDto;
import com.project.springcrawler.service.ServicioCrawler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/crawler")
@Tag(name = "Crawler", description = "API para crawlear productos y categorías")
public class CrawlerController {
    
    private static final Logger logger = LoggerFactory.getLogger(CrawlerController.class);
    
    @Autowired
    private ServicioCrawler servicioCrawler;
    
    @PostMapping("/product")
    @Operation(summary = "Crawlear producto", description = "Extrae información de un producto desde una URL")
    public ResponseEntity<ProductoDto> crawlearProducto(
            @Parameter(description = "URL del producto a crawlear") @RequestParam String url) {
        try {
            logger.info("Solicitud de crawleo de producto recibida para URL: {}", url);
            ProductoDto producto = servicioCrawler.crawlearYGuardarProducto(url);
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            logger.error("Error crawleando producto: {}", url, e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/category")
    @Operation(summary = "Crawlear categoría", description = "Extrae información de una categoría completa")
    public ResponseEntity<CategoriaDto> crawlearCategoria(
            @Parameter(description = "URL de la categoría a crawlear") @RequestParam String url) {
        try {
            logger.info("Solicitud de crawleo de categoría recibida para URL: {}", url);
            CategoriaDto categoria = servicioCrawler.crawlearYGuardarCategoria(url);
            return ResponseEntity.ok(categoria);
        } catch (Exception e) {
            logger.error("Error crawleando categoría: {}", url, e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/health")
    @Operation(summary = "Estado del servicio", description = "Verifica si el servicio está funcionando")
    public ResponseEntity<String> salud() {
        return ResponseEntity.ok("Servicio crawler funcionando");
    }
}