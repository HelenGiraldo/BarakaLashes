package co.edu.uniquindio.BarakaLashes.controlador;

import co.edu.uniquindio.BarakaLashes.modelo.Usuario;
import co.edu.uniquindio.BarakaLashes.repositorio.UsuarioRepositorio;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Optional;

/**
 * @Slf4j (de Lombok) genera un logger 'log' para registrar eventos.
 * SLF4J (Simple Logging Facade for Java) permite crear logs independientes
 * del framework subyacente (Logback, Log4j, etc.).
 * Ejemplo de uso: log.info("Mensaje {}", valor), log.warn(...), log.error(...).
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UsuarioRepositorio usuarioRepositorio;

    @GetMapping("/login")
    public String mostrarLogin(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            if ("no_autenticado".equals(error)) {
                model.addAttribute("error", "Debes iniciar sesión para acceder a esa página");
            } else {
                model.addAttribute("error", "Credenciales inválidas");
            }
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        try {
            // Logs para depuración: ver exactamente lo que llega
            log.info(">>> Login request received. raw email='{}', raw password='{}'", email, password);

            // Normalizar entradas: quitar espacios alrededor
            String emailNorm = email == null ? "" : email.trim();
            String passwordNorm = password == null ? "" : password.trim();

            log.info(">>> Normalized: email='{}', passwordLength={}", emailNorm, passwordNorm.length());

            // 1) Chequeo hardcoded del admin (más robusto)
            if ("admin@gmail.com".equalsIgnoreCase(emailNorm) && "admin123".equals(passwordNorm)) {
                log.info(">>> Autenticación ADMIN correcta");
                session.setAttribute("usuarioEmail", "admin@gmail.com");
                session.setAttribute("usuarioNombre", "Administrador");
                session.setAttribute("usuarioRol", "ADMIN");
                session.setAttribute("usuarioAutenticado", true);
                return "redirect:/usuarios";
            }

            // 2) Si no es admin, seguir con BD
            Optional<Usuario> usuarioOpt = usuarioRepositorio.findByEmail(emailNorm);

            if (usuarioOpt.isEmpty()) {
                log.warn("Usuario no encontrado en BD: {}", emailNorm);
                model.addAttribute("error", "Usuario no encontrado");
                return "login";
            }

            Usuario usuario = usuarioOpt.get();

            // Comparar contraseña (sin encriptar). Normalizamos también la de la BD por si trae espacios
            String passBD = usuario.getPassword() == null ? "" : usuario.getPassword().trim();

            if (!passwordNorm.equals(passBD)) {
                log.warn("Contraseña incorrecta para: {} (password enviada len={}, passwordBD len={})",
                        emailNorm, passwordNorm.length(), passBD.length());
                model.addAttribute("error", "Contraseña incorrecta");
                return "login";
            }

            // Guardar información del usuario en la sesión
            session.setAttribute("usuarioEmail", usuario.getEmail());
            session.setAttribute("usuarioId", usuario.getIdUsuario());
            session.setAttribute("usuarioNombre", usuario.getNombre());
            session.setAttribute("usuarioAutenticado", true);

            log.info("Login exitoso para usuario normal: {}", emailNorm);
            return "redirect:/home";

        } catch (Exception e) {
            log.error("Error en login: {}", e.getMessage(), e);
            model.addAttribute("error", "Error al iniciar sesión");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        log.info("Usuario cerrando sesión: {}", session.getAttribute("usuarioEmail"));
        session.invalidate();
        return "redirect:/login?logout=true";
    }
}