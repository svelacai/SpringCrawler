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
public class CrawlerMercadoLibre implements CrawlerStrategy {

	private static final Logger logger = LoggerFactory.getLogger(CrawlerMercadoLibre.class);

	@Override
	public boolean puedeManear(String url) {
		return url.contains("mercadolibre.com");
	}

	@Override
	public ProductoDto crawlearProducto(String url) {
		try {
			Document documento = Jsoup.connect(url)
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36").get();

			ProductoDto producto = new ProductoDto();
			producto.setUrlOrigen(url);

			// SKU
			String sku = extraerSku(url);
			producto.setSku(sku);

			// Nombre
			Element elementoNombre = documento.selectFirst("h1.ui-pdp-title");
			if (elementoNombre != null) {
				producto.setNombre(elementoNombre.text().trim());
			}

			// Precio
			Element elementoPrecio = documento.selectFirst(".andes-money-amount__fraction");
			if (elementoPrecio != null) {
				String textoPrecio = elementoPrecio.text().replaceAll("[^\\d,]", "").replace(",", ".");
				try {
					producto.setPrecioActual(new BigDecimal(textoPrecio));
				} catch (NumberFormatException e) {
					logger.warn("No se pudo parsear el precio: {}", textoPrecio);
				}
			}

			// Imagenes
			Elements elementosImagen = documento
					.select(".ui-pdp-gallery__figure img, .ui-pdp-gallery img, img[src*='http']");
			List<String> urlsImagen = new ArrayList<>();
			for (Element img : elementosImagen) {
				String src = obtenerUrlImagenReal(img);
				if (!src.isEmpty() && !esImagenPlaceholder(src)) {
					urlsImagen.add(src);
				}
			}

			producto.setUrlsImagenes(urlsImagen);

			// Disponibilidad
			Element elementoDisponibilidad = documento.selectFirst(".ui-pdp-buybox__quantity__available");
			if (elementoDisponibilidad != null) {
				producto.setDisponibilidad("Disponible");
			} else {
				producto.setDisponibilidad("Desconocido");
			}

			logger.info("Producto crawleado exitosamente: {}", producto.getNombre());
			return producto;

		} catch (Exception e) {
			logger.error("Error crawleando producto de MercadoLibre: {}", url, e);
			throw new RuntimeException("Error al crawlear producto", e);
		}
	}

	@Override
	public CategoriaDto crawlearCategoria(String url) {
		throw new UnsupportedOperationException("Crawleo de categorías no implementado para MercadoLibre");
	}

	private String extraerSku(String url) {
		String[] partes = url.split("/");
		for (String parte : partes) {
			if (parte.startsWith("MLA")) {
				return parte;
			}
		}
		return "SKU_DESCONOCIDO";
	}

	private boolean esImagenPlaceholder(String src) {
		return src.startsWith("data:image") || src.contains("placeholder") || src.contains("lazy");
	}

	private String obtenerUrlImagenReal(Element img) {
		String dataSrcset = img.attr("data-zoom").trim();
		if (!dataSrcset.isEmpty()) {
			return extraerPrimeraUrlDeSrcset(dataSrcset);
		}
		return "";
	}

	private String extraerPrimeraUrlDeSrcset(String srcset) {
		String[] partes = srcset.split(",");
		if (partes.length > 0) {
			String primeraParte = partes[0].trim();
			return primeraParte.split(" ")[0];
		}
		return "";
	}

}