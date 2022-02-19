package com.example.demo.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.demo.model.Usuario;
import com.example.demo.service.UsuarioService;

@Controller
public class UsuarioController {

	/**
	 * Contiene la lista de usuarios que se generen
	 */
	@Autowired
	private UsuarioService servicio;

	/**
	 * En caso de no estar registrado el usuario en la aplicación, lo cual
	 * comprobaremos gracias a la sesión, se guiará al login. En caso contrario y,
	 * además, no tener coincidir el usuario con la contraseña, nos dirigiría al
	 * login pero con un mensaje de error. Se añade /login/submit para que entre por
	 * esta dirección cuando entramos desde una dirección cuando el user no está
	 * logueado.
	 * 
	 * @param login
	 * @param model
	 * @return
	 */
	@GetMapping({ "/login", "/", "/login/submit", "/menuPersonal/submit", "/newProducto/submit",
			"/resumenPedido/submit", "/pedido/editar" })
	public String login(String login, Model model) {
		model.addAttribute("usuario", new Usuario());
		return "login";
	}

	/**
	 * Comprobar si el usuario está o no registrado gracias a un método del servicio
	 * para que este controlador no sea demasiado grande. Además se han incluído
	 * aquí las comprobaciones de las validaciones que hemos establecido con
	 * mensajes de error. Se ha utilizado la anotación requestparam para poder
	 * traernos los valores de los inputs del formulario. Se introduce al usuario en
	 * la sesión para posteriormente recuperarlo y manejar sus pedidos.
	 * 
	 * @return devuelve al login si el usuario no es correo y al memú del usuario
	 *         cuando los datos sean correctos
	 */
	@PostMapping(value = "/login/submit")
	public String nuevoUser(Model model, @Valid @ModelAttribute("usuario") Usuario usuario,
			BindingResult bindingResult) {
		Usuario usuario1 = servicio.findByNameAndPassword(usuario);
		if (usuario1 != null && !bindingResult.hasErrors()) {
			model.addAttribute("usuario", usuario1);
			servicio.setUserId(usuario1.getId());
			servicio.setLogueado(true);// cambio el valor de este atributo para saber que se ha logueado un usuario
			return "menuPersonal";
		} else {
			return "login";
		}
	}

}
