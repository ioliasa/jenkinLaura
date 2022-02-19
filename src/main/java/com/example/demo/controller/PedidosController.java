package com.example.demo.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.LineaPedido;
import com.example.demo.model.Pedido;
import com.example.demo.model.Producto;
import com.example.demo.model.Usuario;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.service.PedidoService;
import com.example.demo.service.ProductoService;
import com.example.demo.service.UsuarioService;

@Controller
public class PedidosController {

	@Autowired
	private PedidoService servicePedido;

	@Autowired
	private UsuarioService serviceUsuario;

	@Autowired
	private ProductoService serviceProducto;
	
	@Autowired
	private PedidoRepository repositorioPedido;
	
	private String resumenPedido = "resumenPedido";
	private String domicilio = "domicilio";
	private String envioPedido = "envio";
	private String menuPersonal = "menuPersonal";
	private String nueva = "nueva";

	/**
	 * Método principal para comprobar que el usuario se haya logueado y no esté
	 * accediendo directamente a una parte interna de la aplicación. En el login
	 * habremos cambiado el valor de la variable que usamos para ello a true. Se
	 * utilizará este método cada vez que entremos en una vista nueva para comprobar
	 * la sesión.
	 */
	public String compruebaSesion(String destino) {
		if (!serviceUsuario.isLogueado()) {
			return "login";
		}
		return destino;
	}

	/**
	 * Añadimos la lista de pedidos del usuario logueado.
	 * @param model
	 * @param usuario
	 * @return volvemos a la consulta de pedidos
	 */
	@RequestMapping(value = "/menuPersonal/submit", method = RequestMethod.POST, params = "pedidos")
	public String consultaPedidos(Model model) {
		compruebaSesion(menuPersonal);
		model.addAttribute("listaPedidos", servicePedido.encuentraPedidosDeUsuario(serviceUsuario.getUserId()));
		return "consultaPedidos";
	}

	/**
	 * Se listan los productos de nuestro catálogo, enlazados con un html que se
	 * autogenera, de forma que si añadimos nuevos productos al catálogo, la vista
	 * del mismo se modificará automáticamente.
	 * 
	 * @param model
	 * @param usuario
	 * @return
	 */
	@RequestMapping(value = "/menuPersonal/submit", method = RequestMethod.POST, params = "newPedido")
	public String nuevoPedidoMenu(Model model) {
		compruebaSesion(menuPersonal);
		Usuario usuario1 = serviceUsuario.findById(serviceUsuario.getUserId());
		model.addAttribute("usuario", usuario1);
		model.addAttribute("listaProductos", serviceProducto.findAll());

		return "newPedido";
	}

	/**
	 * Creo un endPoint donde se pase tanto la lista genérica de productos para
	 * poder listarlos en el catálogo como la lista de productos del mismo usuario
	 * para que estos se modifiquen. Recogeré el listado de productos y le
	 * introduciré la cantidad contenida dentro del array de cantidades que
	 * conseguimos dentro del formulario. Inicializo i en 0 puesto y recorro los
	 * productos puesto que la lista de productos y la de cantidades tiene la misma
	 * longitud. Marcamos entonces el pedido realizado por su id guardándolo en un
	 * atributo del servicio de pedidos para, posteriormente, poder recuperar el
	 * pedido con ese id del usuario logueado.
	 * 
	 * @param model
	 * @param usuario
	 * @param listaProductos
	 */
	@PostMapping("/newPedido/submit")
	public String enviaListaDePedidos(Model model, @RequestParam("cantidad") Integer[] cantidades,
			@RequestParam("envio") String envio) {
		compruebaSesion("newPedido");
		double precioTotal = 0;

		//PEDIDO
		Pedido pedidoNuevo = new Pedido();
		servicePedido.setPedidoId(pedidoNuevo.getId());
		int i = 0;
		
		//CREO UNA LINEA DE PEDIDO POR CADA PRODUCTO PEDIDO
		// AÑADO LOS PRODUCTOS SELECCIONADOS AL PEDIDO
		for (Producto producto : serviceProducto.findAll()) {
			//LINEA DE PEDIDO
			LineaPedido lp = new LineaPedido();
			
			if ((cantidades[i] == null) || (cantidades[i] == 0)) {
				i++;
			} else {
				lp.setCantidad(cantidades[i]);
				lp.setProducto(producto);
				lp.setPrecioCantidad();
				lp.setNombreProducto(producto.getNombre());
				lp.setPrecioProducto();
				
				
				//Guardo la linea de pedido en la bbd
				//LineaPedido lpGuardado = serviceLineaPedido.creaLineaPedido(lp);
				
				//Añado la linea de pedido al nuevo pedido creado
				pedidoNuevo.addLineaPedido(lp);
				
				precioTotal += Math.round((producto.getPrecio() * cantidades[i]) * 100d) / 100d;
				i++;
			}
		}
		if(pedidoNuevo.getLineasPedido().isEmpty()) {
			servicePedido.borraPedidoDeUsuario(pedidoNuevo.getId());
			return menuPersonal;
		}
		model.addAttribute("precioTotal", Math.round((precioTotal) * 100d) / 100d);

		// GESTIONO EL ENVÍO
		if (envio.equals(domicilio)) {// si se indica que será en un domicilio diferente al del usuario o no se ha
										// añadido dirección cuando se creó el usuario, se preguntará más adelante
			pedidoNuevo.setDireccion(serviceUsuario.findById(serviceUsuario.getUserId()).getDireccion());
			model.addAttribute(envioPedido, domicilio);
			if (serviceUsuario.findById(serviceUsuario.getUserId()).getDireccion() == null) {
				pedidoNuevo.setDireccion(nueva);
				model.addAttribute(envioPedido, null);
			} else {
				model.addAttribute(domicilio, pedidoNuevo.getDireccion());
			}
		} else if (envio.equals("recoger")) {
			pedidoNuevo.setDireccion("Recogida en tienda");
			model.addAttribute(envioPedido, "recogida");
		} else {
			pedidoNuevo.setDireccion(nueva);
			model.addAttribute(envioPedido, null);
		}

		// AÑADO EL PEDIDO AL USUARIO LOGUEADO
		// HE CAMBIADO LA IMPLEMENTACION DEL METODO DE CREAR PEDIDO PARA QUE DEVUELVA EL PEDIDO CREADO (COMO DEBE SER)
		Usuario user = serviceUsuario.findById(serviceUsuario.getUserId());
		pedidoNuevo.setUsuario(user);
		Pedido guardado = servicePedido.creaPedido(pedidoNuevo); // este método ya busca dentro al usuario logueado y le añade el pedido 
		
		model.addAttribute("listLineasPedido", guardado.getLineasPedido());
		
		//OPCION 1
		model.addAttribute("idPedido", guardado.getId());
		//OPCION 2
		//servicePedido.setPedidoId(pedidoNuevo.getId());
		
		model.addAttribute("telefono", serviceUsuario.findById(serviceUsuario.getUserId()).getTelefono());
		model.addAttribute("email", serviceUsuario.findById(serviceUsuario.getUserId()).getEmail());

		return resumenPedido;
		//return "redirect:/resumenPedido";
	}
	
//	@GetMapping("/resumenPedido/submit/{id}")
//	public String recogeGetPedido(Model model, @PathVariable long id) {
//		model.addAttribute("idPedido", id);
//		Pedido guardado = servicePedido.encuentraPedidoDeUsuario(id);
//		model.addAttribute("telefono", serviceUsuario.findById(serviceUsuario.getUserId()).getTelefono());
//		model.addAttribute("email", serviceUsuario.findById(serviceUsuario.getUserId()).getEmail());
//		model.addAttribute("listLineasPedido", guardado.getLineasPedido());
//		return resumenPedido;
//	}
	
	/**
	 * Método para controlar el resumen del pedido. Es necesario enviar siempre el
	 * envio puesto que si no, dará un null pointer exception al ser requerido
	 * siempre en la plantilla thymeleaf. Si el nulo se mandará y se cambiará la
	 * dirección de envío. Si no, solo se enviará pero no se mostrará en la
	 * plantilla; aún así es necesario para la comprobación en la misma. Una vez
	 * terminado, volvemos al menú personal donde se habrá añadido el nuevo pedido.
	 * Le indicamos que el parámetro de la nueva dirección sea OPCIONAL en caso de
	 * que haya una dirección establecida.
	 * 
	 * En el resumen de pedidos no muestro los precios puesto que se estarán
	 * editando las cantidades.
	 * 
	 * @param model
	 * @param nuevaDireccion
	 * @return vuelta al menú personal del usuario
	 */
	@PostMapping("/resumenPedido/submit")
	public String resumenPedido(Model model,
			@RequestParam(value = "nuevaDireccion", required = false) String nuevaDireccion, @RequestParam(value="idPedido") String idPedido) {
		compruebaSesion("resumenPedido");
		Pedido pedido = servicePedido.encuentraPedidoDeUsuario(Long.parseLong(idPedido));
		if (pedido.getDireccion().equals(nueva)) {
			pedido.setDireccion(nuevaDireccion);
		}
		model.addAttribute("listaPedidos", servicePedido.encuentraPedidosDeUsuario(servicePedido.getPedidoId()));
		return menuPersonal;
	}

	/**
	 * Al ser una url necesitamos un getMapping en vez de un PostMapping. En caso de
	 * que el usuario marque todos los pedidos a 0, se devolverá null en el método
	 * encuentraPedidoDeUsuario, con lo que borramos directamente el pedido y no
	 * pasamos por el resumen.
	 * 
	 * @param model
	 * @param id
	 * @return recogemos las modificaciones del usuario
	 */
	@GetMapping("/pedido/editar/{id}")
	public String editarPedidoGet(Model model, @PathVariable long id) {
		compruebaSesion("editarPedidos");
		Pedido pedidoAEditar = servicePedido.encuentraPedidoDeUsuario(id);
		if (pedidoAEditar == null) {
			servicePedido.borraPedidoDeUsuario(id);
			return menuPersonal;
		}
		//servicePedido.setPedidoId(pedidoAEditar.getId());
		servicePedido.setPedidoId(id);
		model.addAttribute("listLineasPedidos", pedidoAEditar.getLineasPedido());
		model.addAttribute("idPedido", id);
		return "editarPedido";
	}
	

	/**
	 * Método para editar un pedido ya realizado. Le pasamos el id del pedido
	 * seleccionado y accedemos a los productos pedidos. Damos la opción de editar
	 * la cantidad de productos y el modo de envío. Estos datos volverán a guardarse
	 * en el pedido seteando así sus atributos. Además, eliminamos el artículo que
	 * ya no se quiera, es decir, que se haya puesto a 0. Se ha usado un ITERATOR
	 * para no provocar un fallo en caso de que el usuario borre todos los productos
	 * al editarlo, pues con un for no habría objetos que recorrer en la lista y
	 * saltaría un error.
	 * 
	 * @param model
	 * @param id
	 * @param cantidades
	 * @param envio
	 * @return nos devuelve al menú del usuario.
	 */
	@PostMapping("/pedido/editar")
	public String editaPedido(Model model, @RequestParam("cantidad") Integer[] cantidades,
			@RequestParam("envio") String envio, @RequestParam(value="idPedido") String idPedido) {
		compruebaSesion("editarPedidos");
		Pedido pedidoAEditar = servicePedido.encuentraPedidoDeUsuario(Long.parseLong(idPedido));
		Pedido pedidoEditado = new Pedido(Long.parseLong(idPedido));
		double precioTotal = 0;

		// AÑADO LOS PRODUCTOS AL PEDIDO TRAS LAS POSIBLES MODIFICACIONES
		List<LineaPedido> lineasPedido = new ArrayList<>();
		int i = 0;
		Iterator<LineaPedido> it = pedidoAEditar.getLineasPedido().iterator();
		while (it.hasNext()) {
			// Ha sido necesario añadir esta comprobación para no obtener un fallo en caso
			// de que el usuario pusiera a 0 todos los productos del pedido. Con lo que se
			// tendría que eliminar el mismo.
			if (pedidoAEditar.getLineasPedido().isEmpty()) {
				servicePedido.borraPedidoDeUsuario(Long.parseLong(idPedido));
				return menuPersonal;
			}
			LineaPedido lp = it.next();
			if (cantidades[i] == null) {
				i++;
			} else {
				lp.setCantidad(cantidades[i]);
				lp.setPrecioCantidad();
				precioTotal += lp.getPrecioCantidad();
				//Crea un linea con todos los campos para guardarla en bbdd cuando cree el nuevo pedido a guardar y, como tiene el mismo id que uno ya creado, se actualizará cualquier cambio producido
				LineaPedido lpEditada = new LineaPedido(lp.getCantidad(), lp.getIdLinea(), lp.getProducto(), lp.getNombreProducto(), lp.getPrecioProducto(), lp.getPrecioCantidad());
				//lineaRepository.save(lpEditada);
				lineasPedido.add(lpEditada);
				i++;
			}
		}
		
		//Seteo las propiedades que tengo hasta ahora
		pedidoEditado.setLineasPedido(lineasPedido);
		Usuario user = serviceUsuario.findById(serviceUsuario.getUserId());
		pedidoEditado.setUsuario(user);
		
		model.addAttribute("precioTotal", Math.round((precioTotal) * 100d) / 100d);

		// GESTIONO EL ENVÍO
		if (envio.equals("domicilio")) {// si se indica que será en un domicilio diferente al del usuario o no se ha
										// añadido dirección cuando se creó el usuario, se preguntará más adelante
			pedidoEditado.setDireccion(user.getDireccion());
			if (user.getDireccion() == null) {
				pedidoEditado.setDireccion(nueva);
				model.addAttribute(envioPedido, null);

			}
		} else if (envio.equals("recoger")) {
			pedidoEditado.setDireccion("Recogida en tienda");
			model.addAttribute(envioPedido, "recogida");
		} else {
			pedidoEditado.setDireccion(nueva);
			model.addAttribute(envioPedido, null);

		}
		model.addAttribute("idPedido", pedidoEditado.getId());
		model.addAttribute("listLineasPedido", pedidoEditado.getLineasPedido());
		model.addAttribute("telefono", user.getTelefono());
		model.addAttribute("email", user.getEmail());
		
		repositorioPedido.save(pedidoEditado);
		
		return resumenPedido;
	}
	

	/**
	 * Se usa el método del servicio para poder eliminar el pedido indicado. En
	 * dicho método se consigue al usuario logueado y se elimina el pedido de su
	 * lista de pedidos.
	 * 
	 * @param model
	 * @param id
	 * @return vuelve al menú personl del usuario
	 */
	@GetMapping("/pedido/borrar/{id}")
	public String BorrarPedido(Model model, @PathVariable long id) {
		servicePedido.borraPedidoDeUsuario(id);
		return menuPersonal;
	}

}
