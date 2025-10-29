package com.project.springcrawler.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "categorias")
public class Categoria {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nombre;

	@Column(name = "ruta_categoria")
	private String rutaCategoria;

	@Column(name = "total_paginas")
	private Integer totalPaginas;

	@Column(name = "productos_por_pagina")
	private Integer productosPorPagina;

	@Column(name = "url_origen")
	private String urlOrigen;

	@Column(name = "nombre_sitio")
	private String nombreSitio;

	@Column(name = "fecha_creacion")
	private LocalDateTime fechaCreacion;

	@PrePersist
	protected void alCrear() {
		fechaCreacion = LocalDateTime.now();
	}

	public Categoria() {
	}

	public Categoria(String nombre, String rutaCategoria, String urlOrigen, String nombreSitio) {
		this.nombre = nombre;
		this.rutaCategoria = rutaCategoria;
		this.urlOrigen = urlOrigen;
		this.nombreSitio = nombreSitio;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getNombreSitio() {
		return nombreSitio;
	}

	public void setNombreSitio(String nombreSitio) {
		this.nombreSitio = nombreSitio;
	}

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
}