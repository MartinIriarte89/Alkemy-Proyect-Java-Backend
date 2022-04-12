package com.disneyapi.servicio.base;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BaseServicio <T, ID, R extends JpaRepository<T, ID>>{
	
	protected final R repositorio;

	public T guardar(T t) {
		return repositorio.save(t);
	}

	public Optional<T> buscarPorId(ID id) {
		return repositorio.findById(id);
	}

	public List<T> buscarTodos() {
		return repositorio.findAll();
	}

	public Page<T> buscarTodos(Pageable pageable) {
		return repositorio.findAll(pageable);
	}

	public T editar(T t) {
		return repositorio.save(t);
	}

	public void borrar(T t) {
		repositorio.delete(t);
	}

	public void borrarPorId(ID id) {
		repositorio.deleteById(id);
	}

	public boolean existePorId(ID id) {
		return repositorio.existsById(id);
	}
}
