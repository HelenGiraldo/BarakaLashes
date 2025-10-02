package co.edu.uniquindio.BarakaLashes.controlador;

import co.edu.uniquindio.BarakaLashes.DTO.LoginDTO;
import co.edu.uniquindio.BarakaLashes.DTO.RegistroDTO;
import co.edu.uniquindio.BarakaLashes.modelo.Usuario;
import co.edu.uniquindio.BarakaLashes.servicio.AuthServicio;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthServicio authServicio;

    /**
     * Mostrar formulario de login
     */
    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        return "login";
    }

    /**
     * Procesar login
     */
    @PostMapping("/login")
    public String procesarLogin(@ModelAttribute LoginDTO loginDTO,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = authServicio.login(loginDTO);

            // Guardar usuario en sesión
            session.setAttribute("usuario", usuario);
            session.setAttribute("email", usuario.getEmail());
            session.setAttribute("rol", usuario.getRol());

            log.info("Usuario {} autenticado exitosamente. Rol: {}",
                    usuario.getEmail(), usuario.getRol());

            redirectAttributes.addFlashAttribute("success",
                    "¡Bienvenido " + usuario.getNombre() + "!");

            // Redirigir según el rol
            if (usuario.getRol() == co.edu.uniquindio.BarakaLashes.modelo.RolUsuario.ADMIN) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/citas/mis-citas";
            }

        } catch (Exception e) {
            log.error("Error en login: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/login";
        }
    }

    /**
     * Mostrar formulario de registro
     */
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("registroDTO", new RegistroDTO());
        return "registro";
    }

    /**
     * Procesar registro
     */
    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute RegistroDTO registroDTO,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = authServicio.registrar(registroDTO);

            // Auto-login después del registro
            session.setAttribute("usuario", usuario);
            session.setAttribute("email", usuario.getEmail());
            session.setAttribute("rol", usuario.getRol());

            redirectAttributes.addFlashAttribute("success",
                    "¡Registro exitoso! Bienvenido " + usuario.getNombre());

            return "redirect:/citas/mis-citas";

        } catch (Exception e) {
            log.error("Error en registro: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("registroDTO", registroDTO);
            return "redirect:/auth/registro";
        }
    }

    /**
     * Cerrar sesión
     */
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        String email = (String) session.getAttribute("email");
        session.invalidate();

        log.info("Usuario {} cerró sesión", email);
        redirectAttributes.addFlashAttribute("success", "Sesión cerrada exitosamente");
        return "redirect:/auth/login";
    }
}