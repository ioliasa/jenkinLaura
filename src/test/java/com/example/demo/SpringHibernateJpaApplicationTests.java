package com.example.demo;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.model.Usuario;


@SpringBootTest(classes = SpringHibernateJpaApplicationTests.class)
public class SpringHibernateJpaApplicationTests{
	private Usuario u;
	
	
	@Test
	public void testGetContrasena() {
		u = new Usuario("laura", "laura");
		assertTrue("laura".equals(u.getContrasena()));
	}
	
	@Test
	public void testGetNombreUser() {
		u = new Usuario("laura", "laura");
		assertTrue("laura".equals(u.getNombreUser()));
	}
	
	@Test
	public void testGetDireccion() {
		u = new Usuario("Laura", "laura", "calle Preciosa, 23, utrera, 41710");
		assertTrue("calle Preciosa, 23, utrera, 41710".equals(u.getDireccion()));
	}
	
}
