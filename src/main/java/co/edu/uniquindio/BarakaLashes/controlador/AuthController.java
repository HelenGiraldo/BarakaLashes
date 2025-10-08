package co.edu.uniquindio.BarakaLashes.controlador;

import co.edu.uniquindio.BarakaLashes.DTO.RegistroDTO;
import co.edu.uniquindio.BarakaLashes.modelo.Usuario;
import co.edu.uniquindio.BarakaLashes.repositorio.UsuarioRepositorio;
import co.edu.uniquindio.BarakaLashes.servicio.AuthServicio;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthServicio authServicio;
    private final UsuarioRepositorio usuarioRepositorio;

    /**
     * Mostrar formulario de login
     */
    @GetMapping("/login")
    public String mostrarLogin(@RequestParam(required = false) String error,
                               @RequestParam(required = false) String logout,
                               Model model) {
        if (error != null) {
            if ("no_autenticado".equals(error)) {
                model.addAttribute("error", "Debes iniciar sesión para acceder a esa página");
            } else {
                model.addAttribute("error", "Credenciales inválidas");
            }
        }

        if (logout != null) {
            model.addAttribute("success", "Has cerrado sesión correctamente");
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
        log.info("=== INTENTANDO REGISTRAR USUARIO ===");
        log.info("Email: {}", registroDTO.getEmail());
        log.info("Nombre: {}", registroDTO.getNombre());

        try {
            authServicio.registrar(registroDTO);
            log.info("REGISTRO EXITOSO");
            redirectAttributes.addFlashAttribute("success", "¡Registro exitoso! Ahora puedes iniciar sesión");
            return "redirect:/auth/login";
        } catch (Exception e) {
            log.error("ERROR EN REGISTRO: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("registroDTO", registroDTO);
            return "redirect:/auth/registro";
        }
    }

    /**
     * Procesar login
     */
    @PostMapping("/login")
    public String procesarLogin(@RequestParam String email,
                                @RequestParam String password,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        try {
            log.info("=== INTENTO DE LOGIN - Email: {} ===", email);

            boolean valido = authServicio.validarCredenciales(email, password);

            if (valido) {
                // Obtener el usuario para guardar toda su información en sesión
                Optional<Usuario> usuarioOpt = usuarioRepositorio.findByEmail(email);

                if (usuarioOpt.isPresent()) {
                    Usuario usuario = usuarioOpt.get();

                    // GUARDAR EN SESIÓN TODA LA INFORMACIÓN DEL USUARIO
                    session.setAttribute("usuarioEmail", usuario.getEmail());
                    session.setAttribute("email", usuario.getEmail()); // Por compatibilidad
                    session.setAttribute("usuarioId", usuario.getIdUsuario());
                    session.setAttribute("usuarioNombre", usuario.getNombre());
                    session.setAttribute("usuarioAutenticado", true);

                    log.info("LOGIN EXITOSO - Usuario: {} (ID: {})", usuario.getEmail(), usuario.getIdUsuario());
                    log.info("Datos guardados en sesión correctamente");

                    redirectAttributes.addFlashAttribute("success", "¡Bienvenido " + usuario.getNombre() + "!");
                    return "redirect:/citas/nueva";
                } else {
                    log.error(" Usuario encontrado por validación pero no en BD");
                    redirectAttributes.addFlashAttribute("error", "Error en el sistema");
                    return "redirect:/auth/login";
                }
            } else {
                log.warn("Credenciales incorrectas para: {}", email);
                redirectAttributes.addFlashAttribute("error", "Credenciales incorrectas");
                return "redirect:/auth/login";
            }
        } catch (Exception e) {
            log.error(" Error en login: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error al iniciar sesión");
            return "redirect:/auth/login";
        }
    }

    /**
     * Cerrar sesión
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        String email = (String) session.getAttribute("usuarioEmail");
        log.info("=== LOGOUT - Usuario: {} ===", email);
        session.invalidate();
        return "redirect:/auth/login?logout=true";
    }

    /**
     * Página de inicio
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/auth/login";
    }
}