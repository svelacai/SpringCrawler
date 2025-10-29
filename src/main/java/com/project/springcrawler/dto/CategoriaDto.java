package com.project.springcrawler.dto;

import java.util.List;

public class CategoriaDto {
	private String nombre;
	private String rutaCategoria;
	private Integer totalPaginas;
	private Integer productosPorPagina;
	private String urlOrigen;
	private List<ProductoDto> productos;

	public CategoriaDto() {
	}

	public CategoriaDto(String nombre, String rutaCategoria, String urlOrigen) {
		this.nombre = nombre;
		this.rutaCategoria = rutaCategoria;
		this.urlOrigen = urlOrigen;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getRutaCategoria() {
		return rutaCategoria;
	}

	public void setRutaCategoria(String rutaCategoria) {
		this.rutaCategoria = rutaCategoria;
	}

	public Integer getTotalPaginas() {
		return totalPaginas;
	}

	public void setTotalPaginas(Integer totalPaginas) {
		this.totalPaginas = totalPaginas;
	}

	public Integer getProductosPorPagina() {
		return productosPorPagina;
	}

	public void setProductosPorPagina(Integer productosPorPagina) {
		this.productosPorPagina = productosPorPagina;
	}

	public String getUrlOrigen() {
		return urlOrigen;
	}

	public void setUrlOrigen(String urlOrigen) {
		this.urlOrigen = urlOrigen;
	}

	public List<ProductoDto> getProductos() {
		return productos;
	}

	public void setProductos(List<ProductoDto> productos) {
		this.productos = productos;
	}
}