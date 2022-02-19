package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "pedidos")
public class Pedido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "direccion")
	private String direccion;

	@OneToMany(cascade = CascadeType.ALL)
	private List<LineaPedido> lineasPedido = new ArrayList<>();

	@ManyToOne
	private Usuario usuario;

	public Pedido(String direccion) {
		this.direccion = direccion;
	}

	public Pedido(long id) {
		this.id = id;
	}

	public Pedido() {
	}

	public long getId() {
		return id;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<LineaPedido> getLineasPedido() {
		return lineasPedido;
	}

	public void setLineasPedido(List<LineaPedido> lineasPedido) {
		this.lineasPedido = lineasPedido;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pedido other = (Pedido) obj;
		return id == other.id;
	}

	@Override
	public String toString() {
		return "Pedido [id=" + id + ", direccion=" + direccion + "]";
	}

	public void addLineaPedido(LineaPedido lp) {
		this.lineasPedido.add(lp);
	}

}
