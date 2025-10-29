package com.project.springcrawler.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "producto")
public class Producto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String sku;

	private String nombre;

	@Column(name = "precio_actual")
	private BigDecimal precioActual;

	@Column(name = "precio_anterior")
	private BigDecimal precioAnterior;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "imagenes_producto", 
					 joinColumns = @JoinColumn(name = "producto_id"))
	@Column(name = "url_imagen", length = 1000)
	@OrderColumn(name = "orden")
	private List<String> urlsImagenes;

	private String disponibilidad;

	@Column(name = "url_origen")
	private String urlOrigen;

	@Column(name = "nombre_sitio")
	private String nombreSitio;

	@Column(name = "fecha_creacion")
	private LocalDateTime fechaCreacion;

	@Column(name = "fecha_actualizacion")
	private LocalDateTime fechaActualizacion;

	@PrePersist
	protected void alCrear() {
		fechaCreacion = LocalDateTime.now();
		fechaActualizacion = LocalDateTime.now();
	}

	@PreUpdate
	protected void alActualizar() {
		fechaActualizacion = LocalDateTime.now();
	}

	public Producto() {
	}

	public Producto(String sku, String nombre, BigDecimal precioActual, String disponibilidad, String urlOrigen,
			String nombreSitio) {
		this.sku = sku;
		this.nombre = nombre;
		this.precioActual = precioActual;
		this.disponibilidad = disponibilidad;
		this.urlOrigen = urlOrigen;
		this.nombreSitio = nombreSitio;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public LocalDateTime getFechaActualizacion() {
		return fechaActualizacion;
	}

	public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
		this.fechaActualizacion = fechaActualizacion;
	}
}