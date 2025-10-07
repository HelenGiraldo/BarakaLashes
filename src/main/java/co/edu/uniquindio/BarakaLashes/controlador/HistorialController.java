package co.edu.uniquindio.BarakaLashes.controlador;

import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;
import co.edu.uniquindio.BarakaLashes.modelo.Servicio;
import co.edu.uniquindio.BarakaLashes.servicio.CitaServicio;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Controller
@RequestMapping("/historial")
@RequiredArgsConstructor
public class HistorialController {

    private final CitaServicio citaServicio;

    @GetMapping
    public String mostrarHistorial(Model model, HttpSession session) {
        try {
            // Obtener email de la sesión
            String email = (String) session.getAttribute("usuarioEmail");

            if (email == null || email.isEmpty()) {
                email = (String) session.getAttribute("email");
            }

            // Verificar autenticación
            if (email == null || email.isEmpty()) {
                log.warn("Usuario no autenticado intentando acceder al historial");
                log.warn("Atributos en sesión: {}", session.getAttributeNames());
                return "redirect:/auth/login?error=no_autenticado";
            }

            log.info("=== ACCESO A HISTORIAL - Usuario: {} ===", email);

            // Obtener las citas del usuario
            List<CitaDTO> citas = citaServicio.obtenerHistorialCitas(email);

            log.info("=== Total de citas encontradas: {} ===", citas.size());

            // Debug: mostrar detalles de cada cita
            if (!citas.isEmpty()) {
                citas.forEach(cita -> {
                    log.info("Cita ID: {}, Nombre: {}, Fecha: {}, Servicios: {}",
                            cita.getIdCita(),
                            cita.getNombreCita(),
                            cita.getFechaCita(),
                            cita.getServicios() != null ? cita.getServicios().size() : 0);
                });
            } else {
                log.info(" No se encontraron citas para el usuario: {}", email);
            }

            // Pasar al modelo
            model.addAttribute("citas", citas);
            model.addAttribute("titulo", "Mis Citas");

            return "historial";

        } catch (Exception e) {
            log.error(" Error al cargar historial: {}", e.getMessage(), e);
            model.addAttribute("error", "Error al cargar el historial: " + e.getMessage());
            model.addAttribute("citas", List.of());
            model.addAttribute("titulo", "Mis Citas");
            return "historial";
        }
    }

    @PostMapping("/cancelar/{id}")
    public String cancelarCita(@PathVariable Integer id,
                               RedirectAttributes redirectAttributes,
                               HttpSession session) {
        try {
            // Obtener email de la sesión
            String email = (String) session.getAttribute("usuarioEmail");
            if (email == null || email.isEmpty()) {
                email = (String) session.getAttribute("email");
            }

            // Verificar autenticación
            if (email == null || email.isEmpty()) {
                log.warn("Usuario no autenticado intentando cancelar cita");
                redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para cancelar una cita");
                return "redirect:/auth/login";
            }

            log.info("=== CANCELANDO CITA {} para usuario: {} ===", id, email);

            // Obtener la cita para verificar que pertenece al usuario
            CitaDTO cita = citaServicio.obtenerCita(id);

            if (!cita.getEmailCliente().equals(email)) {
                throw new Exception("No tienes permiso para cancelar esta cita");
            }

            citaServicio.cancelarCita(id);

            redirectAttributes.addFlashAttribute("success", "Cita cancelada exitosamente");
            log.info("Cita {} cancelada con éxito", id);

        } catch (Exception e) {
            log.error("Error al cancelar cita {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error",
                    "No se pudo cancelar la cita: " + e.getMessage());
        }

        return "redirect:/historial";
    }

    @GetMapping("/modificar/{id}")
    public String mostrarModificarCita(@PathVariable Integer id,
                                       Model model,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        try {
            // Verificar autenticación
            String email = (String) session.getAttribute("usuarioEmail");
            if (email == null || email.isEmpty()) {
                email = (String) session.getAttribute("email");
            }

            if (email == null || email.isEmpty()) {
                log.warn(" Usuario no autenticado intentando modificar cita");
                return "redirect:/auth/login?error=no_autenticado";
            }

            log.info("=== ACCESO A MODIFICAR CITA {} ===", id);

            // Obtener la cita
            CitaDTO cita = citaServicio.obtenerCita(id);

            // Verificar que la cita pertenece al usuario
            if (!cita.getEmailCliente().equals(email)) {
                log.warn("Usuario {} intentó modificar cita que no le pertenece", email);
                redirectAttributes.addFlashAttribute("error", "No tienes permiso para modificar esta cita");
                return "redirect:/historial";
            }

            // NUEVO: Verificar que la cita se puede modificar
            if (!cita.isModificable()) {
                log.warn(" Usuario {} intentó modificar cita con estado {}", email, cita.getEstadoCita());
                redirectAttributes.addFlashAttribute("error",
                        "No puedes modificar una cita que está " + cita.getEstadoCita().name().toLowerCase());
                return "redirect:/historial";
            }

            model.addAttribute("cita", cita);
            return "modificarCita";

        } catch (Exception e) {
            log.error(" Error al cargar modificarCita: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error al cargar la cita: " + e.getMessage());
            return "redirect:/historial";
        }
    }

    @PostMapping("/modificar/{id}")
    public String procesarModificarCita(@PathVariable Integer id,
                                        @RequestParam String nombreCita,
                                        @RequestParam String fechaCita,
                                        @RequestParam(required = false) String descripcionCita,
                                        @RequestParam(required = false) Set<Servicio> serviciosSeleccionados,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes) {
        try {
            // Verificar autenticación
            String email = (String) session.getAttribute("usuarioEmail");
            if (email == null || email.isEmpty()) {
                email = (String) session.getAttribute("email");
            }

            if (email == null || email.isEmpty()) {
                log.warn("Usuario no autenticado intentando modificar cita");
                return "redirect:/auth/login?error=no_autenticado";
            }

            log.info("=== PROCESANDO MODIFICACIÓN DE CITA {} ===", id);

            // Obtener la cita existente
            CitaDTO citaExistente = citaServicio.obtenerCita(id);

            // Verificar que la cita pertenece al usuario
            if (!citaExistente.getEmailCliente().equals(email)) {
                log.warn("Usuario {} intentó modificar cita que no le pertenece", email);
                redirectAttributes.addFlashAttribute("error", "No tienes permiso para modificar esta cita");
                return "redirect:/historial";
            }

            // Crear DTO con los nuevos datos
            CitaDTO citaActualizada = new CitaDTO();
            citaActualizada.setNombreCita(nombreCita);
            citaActualizada.setDescripcionCita(descripcionCita);
            citaActualizada.setFechaCita(LocalDateTime.parse(fechaCita));
            citaActualizada.setServiciosSeleccionados(serviciosSeleccionados);
            citaActualizada.setEstadoCita(citaExistente.getEstadoCita());
            citaActualizada.setEmailCliente(email);

            citaServicio.actualizarCita(id, citaActualizada);

            redirectAttributes.addFlashAttribute("success", "Cita modificada exitosamente");
            log.info("Cita {} modificada con éxito", id);

        } catch (Exception e) {
            log.error("Error al modificar cita {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error",
                    "No se pudo modificar la cita: " + e.getMessage());
        }

        return "redirect:/historial";
    }
}