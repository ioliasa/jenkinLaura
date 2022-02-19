package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.LineaPedido;
import com.example.demo.repository.LineaPedidoRepository;

@Service
public class LineaPedidoService {
	
	@Autowired
	private LineaPedidoRepository repositorio;
	
	public void borraLineaPedido(long idPedido) {
		repositorio.deleteById(idPedido);
	}

	public LineaPedido creaLineaPedido(LineaPedido lineaPedido) {
		return repositorio.save(lineaPedido);
	}
}
