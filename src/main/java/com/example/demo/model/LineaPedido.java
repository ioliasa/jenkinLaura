package com.example.demo.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "lineaPedidos")
public class LineaPedido {

	@Column(name = "cantidad")
	private int cantidad;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long idLinea;

	@ManyToOne
	private Producto producto;

	@Column(name = "nombreProducto")
	private String nombreProducto;

	@Column(name = "precioProducto")
	private double precioProducto;

	@Column(name = "precioCantidad")
	private double precioCantidad;

	public LineaPedido(int cantidad, Producto producto) {
		this.cantidad = cantidad;
		this.producto = producto;
	}

	public LineaPedido() {
	}

	public LineaPedido(int cantidad, long idLinea, Producto producto, String nombreProducto, double precioProducto,
			double precioCantidad) {
		this.cantidad = cantidad;
		this.idLinea = idLinea;
		this.producto = producto;
		this.nombreProducto = nombreProducto;
		this.precioProducto = precioProducto;
		this.precioCantidad = precioCantidad;
	}

	public String getNombreProducto() {
		return nombreProducto;
	}

	/**
	 * Seteo directamente el nombre del producto para poder mostrarlo en la vista
	 * @param nombreProducto
	 */
	public void setNombreProducto(String nombreProducto) {
		this.nombreProducto = this.producto.getNombre();
	}

	public double getPrecioProducto() {
		return precioProducto;
	}

	/**
	 * Aquí seteo directamente el precio del producto para poder mostrarlo más fácilmente después en la vista
	 * Se usará una vez que se haya introducido el producto
	 */
	public void setPrecioProducto() {
		this.precioProducto = this.producto.getPrecio();
	}

	public double getPrecioCantidad() {
		return precioCantidad;
	}

	/**
	 * Seteo directamente el precio por cantidad del producto para solo llamarlo en la vista
	 * Se seteará una vez que se introduzca la cantidad y el producto
	 * @param precioCantidad
	 */
	public void setPrecioCantidad() {
		this.precioCantidad = (Math.round((producto.getPrecio() * cantidad) * 100d) / 100d);
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public long getIdLinea() {
		return idLinea;
	}

	public void setIdLinea(long idLinea) {
		this.idLinea = idLinea;
	}

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	@Override
	public int hashCode() {
		return Objects.hash(idLinea);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LineaPedido other = (LineaPedido) obj;
		return idLinea == other.idLinea;
	}

	@Override
	public String toString() {
		return "LineaPedido [cantidad=" + cantidad + ", id=" + idLinea + ", producto=" + producto
				+ "]";
	}

}
