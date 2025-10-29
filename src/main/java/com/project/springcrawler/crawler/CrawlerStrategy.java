package com.project.springcrawler.crawler;

import com.project.springcrawler.dto.CategoriaDto;
import com.project.springcrawler.dto.ProductoDto;

public interface CrawlerStrategy {
	
	ProductoDto crawlearProducto(String url);

	CategoriaDto crawlearCategoria(String url);

	boolean puedeManear(String url);
}