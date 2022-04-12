package com.disneyapi.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.disneyapi.modelo.ProductoAudiovisual;

public interface ProductoAudiovisualRepositorio
		extends JpaRepository<ProductoAudiovisual, Long>, JpaSpecificationExecutor<ProductoAudiovisual> {

}
