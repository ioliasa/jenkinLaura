package com.example.demo;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import com.example.demo.model.Producto;
import com.example.demo.model.Usuario;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.repository.UsuarioRepository;

@SpringBootApplication
public class SpringHibernateJpaApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(SpringHibernateJpaApplication.class, args);
	}
	
	@Bean
	CommandLineRunner initData (UsuarioRepository repositorioUsers) {
		return (args) -> {
			repositorioUsers.saveAll(
					Arrays.asList(new Usuario("Pepi Moreno Montes", "32514587H", "pepi@gmail.com", "654875485", "Avenida Sol, 55, Málaga, 52145", "pepi", "pepi123"),
							(new Usuario("Paqui González Jiménez", "65985698l", "paquita@gmail.com", "656568978", "Calle Diamante, 77, Sevilla, 41089","paqui", "paqui123")),
							(new Usuario("Loli Marín Martín","87458254y", "paquita@gmail.com","656568978","Calle Diamantino, 23, Sevilla, 847438", "loli", "loli123"))));
		};
	
	}
	
//	@Bean
//	CommandLineRunner initDataPedido (PedidoRepository repositorioPedido) {
//		return(args) -> {
//			repositorioPedido.saveAll(Arrays.asList(new Pedido("Calle Lalita, 34, Utrera")));
//		};
//	}
	
	@Bean
	CommandLineRunner initDataProducto (ProductoRepository repositorioProducto) {

		//String foto1 = "/img/pngwing.com.png";
		String foto2 = "../img/pngwing.com(1).png";
		String foto3 = "../img/pngwing.com(2).png";
		String foto4 = "../img/pngwing.com(3).png";
		String foto5 = "../img/pngwing.com(4).png";
		String foto6 = "../img/pngwing.com(5).png";
		
		return(args)-> {
			repositorioProducto.saveAll(
					Arrays.asList(new Producto("Bombones rellenos de crema de fresa", 1.20, "/img/pngwing.com.png"),
			(new Producto("Barrita de crema con caramelo", 1.80, foto2)),
			(new Producto("Toffee con caramelo salado", 1.50, foto3)),
			(new Producto("Bombones rellenos de crema de avellanas", 1.20, foto4)),
			(new Producto("Muffin de cacao", 2.00, foto5)),
			(new Producto("Brownie con pepitas de chocolate", 2.20, foto6))));
		};
	}
	
}
