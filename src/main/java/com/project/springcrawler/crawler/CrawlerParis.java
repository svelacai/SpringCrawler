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
public class CrawlerParis implements CrawlerStrategy {

	private static final Logger logger = LoggerFactory.getLogger(CrawlerParis.class);

	@Override
	public boolean puedeManear(String url) {
		return url.contains("paris.cl");
	}

	@Override
	public ProductoDto crawlearProducto(String url) {
		try {
			Document documento = Jsoup.connect(url)
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36").get();

			ProductoDto producto = new ProductoDto();
			producto.setUrlOrigen(url);

			// SKU
			String sku = extraerSkuDeUrl(url);
			producto.setSku(sku);

			// Nombre
			Element elementoNombre = documento.selectFirst("h1.pdp-product-name, .product-title");
			if (elementoNombre != null) {
				producto.setNombre(elementoNombre.text().trim());
			}

			// Precio
			Element elementoPrecio = documento.selectFirst(".price-current, .current-price");
			if (elementoPrecio != null) {
				String textoPrecio = elementoPrecio.text().replaceAll("[^\\d]", "");
				try {
					producto.setPrecioActual(new BigDecimal(textoPrecio));
				} catch (NumberFormatException e) {
					logger.warn("No se pudo parsear el precio: {}", textoPrecio);
				}
			}

			// Imagenes
			Elements elementosImagen = documento
					.select(".product-image img, .pdp-image img, .gallery img, img[data-src*='http']");
			List<String> urlsImagen = new ArrayList<>();
			for (Element img : elementosImagen) {
				String src = obtenerUrlImagenReal(img);
				if (!src.isEmpty() && src.contains("http")) {
					urlsImagen.add(src);
				}
			}
			producto.setUrlsImagenes(urlsImagen);

			// Disponibilidad
			Element elementoStock = documento.selectFirst(".stock-info, .availability");
			if (elementoStock != null && elementoStock.text().toLowerCase().contains("disponible")) {
				producto.setDisponibilidad("Disponible");
			} else {
				producto.setDisponibilidad("Desconocido");
			}

			logger.info("Producto de Paris crawleado exitosamente: {}", producto.getNombre());
			return producto;

		} catch (Exception e) {
			logger.error("Error crawleando producto de Paris: {}", url, e);
			throw new RuntimeException("Error al crawlear producto", e);
		}
	}

	@Override
	public CategoriaDto crawlearCategoria(String url) {
		try {
			Document documento = Jsoup.connect(url)
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36").get();

			CategoriaDto categoria = new CategoriaDto();
			categoria.setUrlOrigen(url);

			// Nombre de categoria
			Element elementoCategoria = documento.selectFirst(".category-title, h1");
			if (elementoCategoria != null) {
				categoria.setNombre(elementoCategoria.text().trim());
			}

			// Ruta de categoria
			Elements migas = documento.select(".breadcrumb a, .breadcrumb-item");
			StringBuilder constructorRuta = new StringBuilder();
			for (Element miga : migas) {
				if (constructorRuta.length() > 0)
					constructorRuta.append(" > ");
				constructorRuta.append(miga.text().trim());
			}
			categoria.setRutaCategoria(constructorRuta.toString());

			// Productos por pagina
			Elements elementosProducto = documento.select(
				"div[data-testid*='-test'], div[data-cnstrc-item-id], div[role='gridcell']"
			);
			if (elementosProducto.isEmpty()) {
				elementosProducto = documento.select("div:has(img):has(a[href*='/p/'])");
			}
			System.out.println(elementosProducto.toString());

			categoria.setProductosPorPagina(elementosProducto.size());

			// Total de paginas
			Element elementoPaginacion = documento.selectFirst(".pagination .last, .pagination-info");
			if (elementoPaginacion != null) {
				String textoPaginacion = elementoPaginacion.text();
				try {
					String[] partes = textoPaginacion.split("\\s+");
					for (String parte : partes) {
						if (parte.matches("\\d+")) {
							categoria.setTotalPaginas(Integer.parseInt(parte));
							break;
						}
					}
				} catch (NumberFormatException e) {
					categoria.setTotalPaginas(1);
				}
			} else {
				categoria.setTotalPaginas(1);
			}

			// Extraer productos de todas las paginas
			List<ProductoDto> productos = new ArrayList<>();

			// Página actual
			for (Element elementoProducto : elementosProducto) {
				ProductoDto producto = extraerProductoDeElemento(elementoProducto);
				if (producto != null) {
					productos.add(producto);
				}
			}

			// Recorrer páginas adicionales
			for (int pagina = 2; pagina <= categoria.getTotalPaginas(); pagina++) {
				String urlPagina = construirUrlPagina(url, pagina);
				List<ProductoDto> productosPagina = extraerProductosDePagina(urlPagina);
				productos.addAll(productosPagina);
			}

			categoria.setProductos(productos);

			logger.info("Categoría de Paris crawleada exitosamente: {} con {} productos", categoria.getNombre(),
					productos.size());
			return categoria;

		} catch (Exception e) {
			logger.error("Error crawleando categoría de Paris: {}", url, e);
			throw new RuntimeException("Error al crawlear categoría", e);
		}
	}

	private ProductoDto extraerProductoDeElemento(Element elementoProducto) {
		try {
			ProductoDto producto = new ProductoDto();

			// SKU desde data-cnstrc-item-id
			String sku = elementoProducto.attr("data-cnstrc-item-id");
			producto.setSku(sku.isEmpty() ? "PARIS_" + System.currentTimeMillis() : "PARIS_" + sku);

			// Nombre completo: marca + nombre del producto
			String marca = elementoProducto.selectFirst(".ui-font-semibold.ui-line-clamp-2") != null ? 
					elementoProducto.selectFirst(".ui-font-semibold.ui-line-clamp-2").text().trim() : "";
			String nombreProducto = elementoProducto.selectFirst(".ui-line-clamp-2.ui-text-xs, .ui-line-clamp-2:not(.ui-font-semibold)") != null ?
					elementoProducto.selectFirst(".ui-line-clamp-2.ui-text-xs, .ui-line-clamp-2:not(.ui-font-semibold)").text().trim() : "";
			
			String nombreCompleto = elementoProducto.attr("data-cnstrc-item-name");
			if (nombreCompleto.isEmpty()) {
				nombreCompleto = (marca + " " + nombreProducto).trim();
			}
			producto.setNombre(nombreCompleto);

			// Precio actual desde data-cnstrc-item-price
			String precioStr = elementoProducto.attr("data-cnstrc-item-price");
			if (!precioStr.isEmpty()) {
				try {
					producto.setPrecioActual(new BigDecimal(precioStr));
				} catch (NumberFormatException e) {
					logger.warn("No se pudo parsear el precio: {}", precioStr);
				}
			}

			// Precio anterior 
			Element precioAnterior = elementoProducto.selectFirst(".ui-line-through");
			if (precioAnterior != null) {
				String precioAntStr = precioAnterior.text().replaceAll("[^\\d]", "");
				try {
					producto.setPrecioAnterior(new BigDecimal(precioAntStr));
				} catch (NumberFormatException e) {
					logger.warn("No se pudo parsear el precio anterior: {}", precioAntStr);
				}
			}

			// Todas las imágenes del producto
			Elements imagenes = elementoProducto.select("img");
			List<String> urlsImagen = new ArrayList<>();
			for (Element img : imagenes) {
				String src = obtenerUrlImagenReal(img);
				if (!src.isEmpty() && src.contains("http") && !urlsImagen.contains(src)) {
					urlsImagen.add(src);
				}
			}
			producto.setUrlsImagenes(urlsImagen);

			// URL del producto
			Element enlace = elementoProducto.selectFirst("a");
			if (enlace != null) {
				String href = enlace.attr("href");
				if (!href.isEmpty() && !href.startsWith("http")) {
					href = "https://www.paris.cl" + href;
				}
				producto.setUrlOrigen(href);
			}

			producto.setDisponibilidad("Disponible");
			return producto;

		} catch (Exception e) {
			logger.warn("Error extrayendo producto del elemento: {}", e.getMessage());
			return null;
		}
	}

	private String construirUrlPagina(String urlBase, int numeroPagina) {
		if (urlBase.contains("?")) {
			return urlBase + "&page=" + numeroPagina;
		} else {
			return urlBase + "?page=" + numeroPagina;
		}
	}

	private List<ProductoDto> extraerProductosDePagina(String url) {
		List<ProductoDto> productos = new ArrayList<>();
		try {
			Document documento = Jsoup.connect(url)
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36").get();
			System.out.println(documento.html());

			Elements elementosProducto = documento.select(
				"div[data-testid*='-test'], div[data-cnstrc-item-id], div[role='gridcell']"
			);
			if (elementosProducto.isEmpty()) {
				elementosProducto = documento.select("div:has(img):has(a[href*='/p/'])");
			}
			for (Element elementoProducto : elementosProducto) {
				ProductoDto producto = extraerProductoDeElemento(elementoProducto);
				if (producto != null) {
					productos.add(producto);
				}
			}
			logger.info("Extraídos {} productos de la página: {}", productos.size(), url);
		} catch (Exception e) {
			logger.error("Error extrayendo productos de la página: {}", url, e);
		}
		return productos;
	}

	private String extraerSkuDeUrl(String url) {
		String[] partes = url.split("/");
		for (String parte : partes) {
			if (parte.matches("\\d+")) {
				return "PARIS_" + parte;
			}
		}
		return "PARIS_DESCONOCIDO";
	}

	private String obtenerUrlImagenReal(Element img) {
		String srcset = img.attr("srcset").trim();
		if (!srcset.isEmpty()) {
			return extraerPrimeraUrlDeSrcset(srcset);
		}
		
		String dataSrcset = img.attr("data-srcset").trim();
		if (!dataSrcset.isEmpty()) {
			return extraerPrimeraUrlDeSrcset(dataSrcset);
		}
		
		String src = img.attr("src").trim();
		if (!src.isEmpty() && !esImagenPlaceholder(src)) {
			return src;
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

	private boolean esImagenPlaceholder(String src) {
		return src.startsWith("data:image") || src.contains("placeholder") || src.contains("lazy");
	}
}