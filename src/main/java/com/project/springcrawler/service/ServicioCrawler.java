package com.project.springcrawler.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.springcrawler.crawler.CrawlerStrategy;
import com.project.springcrawler.dto.CategoriaDto;
import com.project.springcrawler.dto.ProductoDto;
import com.project.springcrawler.entity.Categoria;
import com.project.springcrawler.entity.Producto;
import com.project.springcrawler.repository.RepositorioCategoria;
import com.project.springcrawler.repository.RepositorioProducto;

@Service
public class ServicioCrawler {

	private static final Logger logger = LoggerFactory.getLogger(ServicioCrawler.class);

	@Autowired
	public List<CrawlerStrategy> estrategiasCrawler;

	@Autowired
	public RepositorioProducto repositorioProducto;

	@Autowired
	private RepositorioCategoria repositorioCategoria;

	public ProductoDto crawlearYGuardarProducto(String url) {
		logger.info("Iniciando crawleo de producto para URL: {}", url);

		CrawlerStrategy estrategia = encontrarEstrategia(url);
		if (estrategia == null) {
			throw new IllegalArgumentException("No se encontró estrategia de crawler para URL: " + url);
		}

		ProductoDto dtoProducto = estrategia.crawlearProducto(url);
		Producto producto = convertirAEntidad(dtoProducto, obtenerNombreSitio(url));

		Optional<Producto> productoExistente = repositorioProducto.findBySku(producto.getSku());
		if (productoExistente.isPresent()) {
			Producto existente = productoExistente.get();
			actualizarCamposProducto(existente, producto);
			repositorioProducto.save(existente);
			logger.info("Producto existente actualizado: {}", existente.getSku());
		} else {
			repositorioProducto.save(producto);
			logger.info("Nuevo producto guardado: {}", producto.getSku());
		}

		return dtoProducto;
	}

	public CategoriaDto crawlearYGuardarCategoria(String url) {
		logger.info("Iniciando crawleo de categoría para URL: {}", url);

		CrawlerStrategy estrategia = encontrarEstrategia(url);
		if (estrategia == null) {
			throw new IllegalArgumentException("No se encontró estrategia de crawler para URL: " + url);
		}

		CategoriaDto dtoCategoria = estrategia.crawlearCategoria(url);
		Categoria categoria = convertirAEntidad(dtoCategoria, obtenerNombreSitio(url));
		repositorioCategoria.save(categoria);
		logger.info("Categoría guardada: {}", categoria.getNombre());

		if (dtoCategoria.getProductos() != null) {
			logger.info("Procesando {} productos de la categoría", dtoCategoria.getProductos().size());
			int guardados = 0;
			for (ProductoDto dtoProducto : dtoCategoria.getProductos()) {
				try {
					if (dtoProducto.getNombre() == null || dtoProducto.getNombre().trim().isEmpty()) {
						logger.warn("Producto sin nombre, saltando: {}", dtoProducto.getSku());
						continue;
					}
					Producto producto = convertirAEntidad(dtoProducto, obtenerNombreSitio(url));
					Optional<Producto> productoExistente = repositorioProducto.findBySku(producto.getSku());
					if (productoExistente.isPresent()) {
						Producto existente = productoExistente.get();
						actualizarCamposProducto(existente, producto);
						repositorioProducto.save(existente);
						logger.debug("Producto actualizado: {}", existente.getSku());
					} else {
						repositorioProducto.save(producto);
						logger.debug("Nuevo producto guardado: {}", producto.getSku());
					}
					guardados++;
				} catch (Exception e) {
					logger.warn("Error al guardar producto de categoría: {} - {}", dtoProducto.getNombre(), e.getMessage());
				}
			}
			logger.info("Guardados {} de {} productos de la categoría", guardados, dtoCategoria.getProductos().size());
		} else {
			logger.warn("No se encontraron productos en la categoría");
		}

		return dtoCategoria;
	}

	private CrawlerStrategy encontrarEstrategia(String url) {
		return estrategiasCrawler.stream().filter(estrategia -> estrategia.puedeManear(url)).findFirst().orElse(null);
	}

	private String obtenerNombreSitio(String url) {
		if (url.contains("mercadolibre.com")) {
			return "MercadoLibre";
		}
		if (url.contains("paris.cl")) {
			return "Paris";
		}
		if (url.contains("amazon.com") || url.contains("amazon.es") || url.contains("amazon.co.uk")) {
			return "Amazon";
		}
		return "Unknown";
	}

	private Producto convertirAEntidad(ProductoDto dto, String nombreSitio) {
		Producto producto = new Producto();
		producto.setSku(dto.getSku());
		producto.setNombre(dto.getNombre());
		producto.setPrecioActual(dto.getPrecioActual());
		producto.setPrecioAnterior(dto.getPrecioAnterior());
		producto.setUrlsImagenes(dto.getUrlsImagenes());
		producto.setDisponibilidad(dto.getDisponibilidad());
		producto.setUrlOrigen(dto.getUrlOrigen());
		producto.setNombreSitio(nombreSitio);
		return producto;
	}

	private Categoria convertirAEntidad(CategoriaDto dto, String nombreSitio) {
		Categoria categoria = new Categoria();
		categoria.setNombre(dto.getNombre());
		categoria.setRutaCategoria(dto.getRutaCategoria());
		categoria.setTotalPaginas(dto.getTotalPaginas());
		categoria.setProductosPorPagina(dto.getProductosPorPagina());
		categoria.setUrlOrigen(dto.getUrlOrigen());
		categoria.setNombreSitio(nombreSitio);
		return categoria;
	}

	private void actualizarCamposProducto(Producto existente, Producto productoNuevo) {
		existente.setNombre(productoNuevo.getNombre());
		existente.setPrecioAnterior(existente.getPrecioActual());
		existente.setPrecioActual(productoNuevo.getPrecioActual());
		if (productoNuevo.getUrlsImagenes() != null && !productoNuevo.getUrlsImagenes().isEmpty()) {
			existente.setUrlsImagenes(productoNuevo.getUrlsImagenes());
		}
		existente.setDisponibilidad(productoNuevo.getDisponibilidad());
	}
}