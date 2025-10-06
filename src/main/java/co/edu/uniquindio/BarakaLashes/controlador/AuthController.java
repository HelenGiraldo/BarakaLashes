package co.edu.uniquindio.BarakaLashes.controlador;

import co.edu.uniquindio.BarakaLashes.DTO.RegistroDTO;
import co.edu.uniquindio.BarakaLashes.servicio.AuthServicio;
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
     * Mostrar formulario de login (simple)
     */
    @GetMapping("/login")
    public String mostrarLogin(Model model) {
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
        log.info("=== INTENTANDO REGISTRAR USUARIO ===");
        log.info("Email: {}", registroDTO.getEmail());
        log.info("Nombre: {}", registroDTO.getNombre());

        try {
            authServicio.registrar(registroDTO);
            log.info(" REGISTRO EXITOSO");
            redirectAttributes.addFlashAttribute("success", "¡Registro exitoso! Ahora puedes iniciar sesión");
            return "redirect:/auth/login";
        } catch (Exception e) {
            log.error(" ERROR EN REGISTRO: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("registroDTO", registroDTO);
            return "redirect:/auth/registro";
        }
    }

    /**
     * Página de inicio simple
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/auth/login";
    }

    /**
     * Procesar login simple
     */
    @PostMapping("/login")
    public String procesarLogin(@RequestParam String email,
                                @RequestParam String password,
                                jakarta.servlet.http.HttpSession session,
                                RedirectAttributes redirectAttributes) {
        try {
            boolean valido = authServicio.validarCredenciales(email, password);

            if (valido) {
                // Guardar email en sesión para identificar al usuario en el historial
                session.setAttribute("email", email);
                redirectAttributes.addFlashAttribute("success", "Login exitoso");
                return "redirect:/citas/nueva";
            } else {
                redirectAttributes.addFlashAttribute("error", "Credenciales incorrectas");
                return "redirect:/auth/login";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Credenciales incorrectas");
            return "redirect:/auth/login";

        }
    }
}