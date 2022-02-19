package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

	/**
	 * Setea la dirección del pedido una vez que se ha creado el pedido
	 * @param id
	 * @param direccion
	 */
   @Modifying
   @Query(value = "update pedido set direccion = :direccion where id = :id",nativeQuery = true)
    void updateDireccionById(@Param("id") Integer id, @Param("direccion") String direccion);
	
	/**
	 * Encuantra el pedido concreto
	 * @param id
	 * @return
	 */
	public List<Pedido> findByUsuarioId(long id);
}
