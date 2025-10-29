package com.project.springcrawler.dto;

import java.math.BigDecimal;
import java.util.List;

public class ProductoDto {
	private String sku;
	private String nombre;
	private BigDecimal precioActual;
	private BigDecimal precioAnterior;
	private List<String> urlsImagenes;
	private String disponibilidad;
	private String urlOrigen;

	public ProductoDto() {
	}

	public ProductoDto(String sku, String nombre, BigDecimal precioActual, String disponibilidad, String urlOrigen) {
		this.sku = sku;
		this.nombre = nombre;
		this.precioActual = precioActual;
		this.disponibilidad = disponibilidad;
		this.urlOrigen = urlOrigen;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public BigDecimal getPrecioActual() {
		return precioActual;
	}

	public void setPrecioActual(BigDecimal precioActual) {
		this.precioActual = precioActual;
	}

	public BigDecimal getPrecioAnterior() {
		return precioAnterior;
	}

	public void setPrecioAnterior(BigDecimal precioAnterior) {
		this.precioAnterior = precioAnterior;
	}

	public List<String> getUrlsImagenes() {
		return urlsImagenes;
	}

	public void setUrlsImagenes(List<String> urlsImagenes) {
		this.urlsImagenes = urlsImagenes;
	}

	public String getDisponibilidad() {
		return disponibilidad;
	}

	public void setDisponibilidad(String disponibilidad) {
		this.disponibilidad = disponibilidad;
	}

	public String getUrlOrigen() {
		return urlOrigen;
	}

	public void setUrlOrigen(String urlOrigen) {
		this.urlOrigen = urlOrigen;
	}
}