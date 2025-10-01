package co.edu.uniquindio.BarakaLashes.controlador;

import co.edu.uniquindio.BarakaLashes.modelo.Usuario;
import co.edu.uniquindio.BarakaLashes.servicio.UsuarioServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioServicio usuarioServicio;

    @GetMapping
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioServicio.listarUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "listaUsuarios";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "crearUsuario";
    }

    @PostMapping("/nuevo")
    public String crearUsuario(@ModelAttribute Usuario usuario) {
        try {
            usuarioServicio.crearUsuario(usuario);
            return "redirect:/usuarios?exito";
        } catch (RuntimeException e) {
            return "redirect:/usuarios/nuevo?error=" + e.getMessage();
        }
    }

    @GetMapping("/{id}")
    public String verUsuario(@PathVariable Integer id, Model model) {
        Usuario usuario = usuarioServicio.obtenerUsuarioPorId(id);
        model.addAttribute("usuario", usuario);
        return "verUsuario";
    }

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        Usuario usuario = usuarioServicio.obtenerUsuarioPorId(id);
        model.addAttribute("usuario", usuario);
        return "editarUsuario";
    }

    @PostMapping("/{id}/editar")
    public String actualizarUsuario(@PathVariable Integer id, @ModelAttribute Usuario usuario) {
        try {
            usuarioServicio.actualizarUsuario(id, usuario);
            return "redirect:/usuarios?actualizado";
        } catch (RuntimeException e) {
            return "redirect:/usuarios/" + id + "/editar?error=" + e.getMessage();
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarUsuario(@PathVariable Integer id) {
        try {
            usuarioServicio.eliminarUsuario(id);
            return "redirect:/usuarios?eliminado";
        } catch (RuntimeException e) {
            return "redirect:/usuarios?error=" + e.getMessage();
        }
    }

    @GetMapping("/buscar")
    public String buscarUsuario(@RequestParam String email, Model model) {
        try {
            Usuario usuario = usuarioServicio.obtenerUsuarioPorEmail(email);
            model.addAttribute("usuario", usuario);
            return "verUsuario";
        } catch (RuntimeException e) {
            return "redirect:/usuarios?error=" + e.getMessage();
        }
    }
}