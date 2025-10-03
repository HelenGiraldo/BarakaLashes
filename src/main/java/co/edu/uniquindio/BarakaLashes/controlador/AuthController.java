package co.edu.uniquindio.BarakaLashes.controlador;

import co.edu.uniquindio.BarakaLashes.DTO.RegistroDTO;
import co.edu.uniquindio.BarakaLashes.servicio.AuthServicio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public String mostrarLogin(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        if (error != null) {
            model.addAttribute("error", "Email o contraseña incorrectos");
        }
        if (logout != null) {
            model.addAttribute("success", "Sesión cerrada exitosamente");
        }

        return "login";
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
                                   RedirectAttributes redirectAttributes) {
        log.info("=== RECIBIENDO DATOS DEL FORMULARIO ===");
        log.info("Nombre: {}", registroDTO.getNombre());
        log.info("Apellido: {}", registroDTO.getApellido());
        log.info("Cédula: {}", registroDTO.getCedula());
        log.info("Email: {}", registroDTO.getEmail());
        log.info("Teléfono: {}", registroDTO.getTelefono());
        log.info("Password: {}", registroDTO.getPassword() != null ? "***" : "NULL");
        log.info("ConfirmarPassword: {}", registroDTO.getConfirmarPassword() != null ? "***" : "NULL");

        try {
            authServicio.registrar(registroDTO);
            redirectAttributes.addFlashAttribute("success", "¡Registro exitoso! Ahora puedes iniciar sesión");
            return "redirect:/auth/login";
        } catch (Exception e) {
            log.error("Error en registro: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("registroDTO", registroDTO);
            return "redirect:/auth/registro";
        }
    }

    /**
     * Página principal después del login
     */
    @GetMapping("/success")
    public String loginSuccessRedirect() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/citas/mis-citas";
        }
    }

    /**
     * Página de inicio
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/auth/login";
    }
}