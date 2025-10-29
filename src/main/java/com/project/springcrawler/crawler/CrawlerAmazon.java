package com.project.springcrawler.crawler;

import com.project.springcrawler.dto.CategoriaDto;
import com.project.springcrawler.dto.ProductoDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class CrawlerAmazon implements CrawlerStrategy {

	private static final Logger logger = LoggerFactory.getLogger(CrawlerAmazon.class);

	@Override
	public boolean puedeManear(String url) {
		return url.contains("amazon.com") || url.contains("amazon.es") || url.contains("amazon.co.uk");
	}

	@Override
	public ProductoDto crawlearProducto(String url) {
		try {
			Document documento = Jsoup.connect(url)
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
					.header("Accept-Language", "en-US,en;q=0.9").get();

			ProductoDto producto = new ProductoDto();
			producto.setUrlOrigen(url);

			// SKU 
			String sku = extraerAsin(url);
			producto.setSku(sku);

			// Nombre
			Element elementoNombre = documento.selectFirst("#productTitle, .product-title");
			if (elementoNombre != null) {
				producto.setNombre(elementoNombre.text().trim());
			}

			// Precio
			Element elementoPrecio = documento.selectFirst(".a-price-whole, .a-offscreen, .a-price .a-offscreen");
			if (elementoPrecio != null) {
				String textoPrecio = elementoPrecio.text().replaceAll("[^\\d.,]", "").replace(",", "");
				try {
					producto.setPrecioActual(new BigDecimal(textoPrecio));
				} catch (NumberFormatException e) {
					logger.warn("No se pudo parsear el precio: {}", textoPrecio);
				}
			}

			// Precio anterior
			Element elementoPrecioAnterior = documento
					.selectFirst(".a-text-price .a-offscreen, .a-price.a-text-price .a-offscreen");
			if (elementoPrecioAnterior != null) {
				String textoPrecioAnterior = elementoPrecioAnterior.text().replaceAll("[^\\d.,]", "").replace(",", "");
				try {
					producto.setPrecioAnterior(new BigDecimal(textoPrecioAnterior));
				} catch (NumberFormatException e) {
					logger.warn("No se pudo parsear el precio anterior: {}", textoPrecioAnterior);
				}
			}

			// Imagenes
			Elements elementosImagen = documento.select("#landingImage, .a-dynamic-image, #imgTagWrapperId img");
			List<String> urlsImagen = new ArrayList<>();
			elementosImagen.forEach(response -> {
				String src = response.attr("src");
				if (!src.isEmpty()) {
					urlsImagen.add(src);
				}
			});
			producto.setUrlsImagenes(urlsImagen);

			// Disponibilidad
			Element elementoDisponibilidad = documento
					.selectFirst("#availability span, .a-color-success, .a-color-state");
			if (elementoDisponibilidad != null) {
				String textoDisponibilidad = elementoDisponibilidad.text().toLowerCase();
				if (textoDisponibilidad.contains("in stock") || textoDisponibilidad.contains("available")) {
					producto.setDisponibilidad("Disponible");
				} else if (textoDisponibilidad.contains("out of stock")) {
					producto.setDisponibilidad("Sin Stock");
				} else {
					producto.setDisponibilidad("Desconocido");
				}
			} else {
				producto.setDisponibilidad("Desconocido");
			}

			logger.info("Producto de Amazon crawleado exitosamente: {}", producto.getNombre());
			return producto;

		} catch (Exception e) {
			logger.error("Error crawleando producto de Amazon: {}", url, e);
			throw new RuntimeException("Error al crawlear producto", e);
		}
	}

	@Override
	public CategoriaDto crawlearCategoria(String url) {
		try {
			Document documento = Jsoup.connect(url)
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
					.header("Accept-Language", "en-US,en;q=0.9").get();

			CategoriaDto categoria = new CategoriaDto();
			categoria.setUrlOrigen(url);

			// Nombre de categoría
			Element elementoCategoria = documento.selectFirst(".a-color-state, .nav-search-label, h1");
			if (elementoCategoria != null) {
				categoria.setNombre(elementoCategoria.text().trim());
			}

			// Ruta de categoría desde migas de pan
			Elements migas = documento.select(".a-breadcrumb a");
			StringBuilder constructorRuta = new StringBuilder();
			for (Element miga : migas) {
				if (constructorRuta.length() > 0)
					constructorRuta.append(" > ");
				constructorRuta.append(miga.text().trim());
			}
			categoria.setRutaCategoria(constructorRuta.toString());

			// Productos por página
			Elements elementosProducto = documento.select("[data-component-type='s-search-result']");
			categoria.setProductosPorPagina(elementosProducto.size());

			// Total de páginas
			Element elementoPaginacion = documento.selectFirst(".a-pagination .a-last a, .a-pagination .a-disabled");
			if (elementoPaginacion != null) {
				String href = elementoPaginacion.attr("href");
				if (href.contains("page=")) {
					try {
						String numeroPagina = href.substring(href.indexOf("page=") + 5);
						numeroPagina = numeroPagina.split("&")[0];
						categoria.setTotalPaginas(Integer.parseInt(numeroPagina));
					} catch (Exception e) {
						categoria.setTotalPaginas(1);
					}
				}
			} else {
				categoria.setTotalPaginas(1);
			}

			// Extraer productos de la página actual
			List<ProductoDto> productos = new ArrayList<>();
			for (Element elementoProducto : elementosProducto) {
				ProductoDto producto = extraerProductoDeElemento(elementoProducto);
				if (producto != null) {
					productos.add(producto);
				}
			}
			categoria.setProductos(productos);

			logger.info("Categoría de Amazon crawleada exitosamente: {} con {} productos", categoria.getNombre(),
					productos.size());
			return categoria;

		} catch (Exception e) {
			logger.error("Error crawleando categoría de Amazon: {}", url, e);
			throw new RuntimeException("Error al crawlear categoría", e);
		}
	}

	private ProductoDto extraerProductoDeElemento(Element elementoProducto) {
		try {
			ProductoDto producto = new ProductoDto();

			// Nombre
			Element elementoNombre = elementoProducto.selectFirst("h2 a span, .a-size-medium");
			if (elementoNombre != null) {
				producto.setNombre(elementoNombre.text().trim());
			}

			// Precio
			Element elementoPrecio = elementoProducto.selectFirst(".a-price-whole, .a-offscreen");
			if (elementoPrecio != null) {
				String textoPrecio = elementoPrecio.text().replaceAll("[^\\d.,]", "").replace(",", "");
				try {
					producto.setPrecioActual(new BigDecimal(textoPrecio));
				} catch (NumberFormatException e) {
					logger.warn("No se pudo parsear el precio: {}", textoPrecio);
				}
			}

			// Imagen
			Element elementoImagen = elementoProducto.selectFirst("img");
			if (elementoImagen != null) {
				List<String> urlsImagen = new ArrayList<>();
				urlsImagen.add(elementoImagen.attr("src"));
				producto.setUrlsImagenes(urlsImagen);
			}

			// SKU - extraer de atributos de datos o generar
			String dataAsin = elementoProducto.attr("data-asin");
			if (!dataAsin.isEmpty()) {
				producto.setSku(dataAsin);
			} else {
				producto.setSku("AMAZON_" + Math.abs(producto.getNombre().hashCode()));
			}

			producto.setDisponibilidad("Disponible");

			return producto;

		} catch (Exception e) {
			logger.warn("Error extrayendo producto del elemento", e);
			return null;
		}
	}

	private String extraerAsin(String url) {
		// Extraer ASIN de la URL de Amazon
		if (url.contains("/dp/")) {
			String[] partes = url.split("/dp/");
			if (partes.length > 1) {
				String asin = partes[1].split("/")[0].split("\\?")[0];
				return asin;
			}
		}
		return "AMAZON_DESCONOCIDO";
	}
}