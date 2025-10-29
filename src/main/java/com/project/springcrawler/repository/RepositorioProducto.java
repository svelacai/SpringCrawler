package com.project.springcrawler.repository;

import com.project.springcrawler.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RepositorioProducto extends JpaRepository<Producto, Long> {
	Optional<Producto> findBySku(String sku);
}