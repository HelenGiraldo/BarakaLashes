package co.edu.uniquindio.BarakaLashes.controlador;

import co.edu.uniquindio.BarakaLashes.DTO.Cita.ResumenCitasDTO;
import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;
import co.edu.uniquindio.BarakaLashes.modelo.Cita;
import co.edu.uniquindio.BarakaLashes.modelo.EstadoCita;
import co.edu.uniquindio.BarakaLashes.servicio.CitaServicio;
import co.edu.uniquindio.BarakaLashes.servicio.UsuarioServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Controller
@RequestMapping("/empleado")
@RequiredArgsConstructor
public class EmpleadoCitaController {

    private final CitaServicio citaServicio;
    private final UsuarioServicio usuarioServicio;

    //  DASHBOARD EMPLEADO
    @GetMapping("/dashboard")
    public String dashboardEmpleado(Model model) {
        try {
            LocalDate hoy = LocalDate.now();

            // Resumen de hoy
            ResumenCitasDTO resumenHoy = citaServicio.obtenerResumenCitas(hoy, hoy, null);
            model.addAttribute("resumenHoy", resumenHoy);

            // Próximas citas de hoy
            List<CitaDTO> citasHoy = citaServicio.obtenerCitasPorRango(hoy, hoy, null, null);
            model.addAttribute("citasHoy", citasHoy);

            // Citas de la semana
            List<CitaDTO> citasSemana = citaServicio.obtenerCitasPorRango(
                    hoy, hoy.plusDays(7), null, null);
            model.addAttribute("citasSemana", citasSemana);

        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar dashboard: " + e.getMessage());
        }

        return "empleado/dashboard";
    }

    //  CALENDARIO EMPLEADO
    @GetMapping("/calendario")
    public String calendarioEmpleado(Model model) {
        LocalDate hoy = LocalDate.now();
        model.addAttribute("fechaInicio", hoy.withDayOfMonth(1));
        model.addAttribute("fechaFin", hoy.withDayOfMonth(hoy.lengthOfMonth()));

        // Añadir clientes para filtros si el servicio existe
        try {
            model.addAttribute("clientes", usuarioServicio.listarUsuarios());
        } catch (Exception e) {
            // Si el método no existe, simplemente no añadimos los clientes
            System.out.println("Método listarTodosLosUsuarios no disponible");
        }

        return "empleado/calendario";
    }

    //  LISTA DE CITAS EMPLEADO - CON filtro de clientes Y estadísticas
    @GetMapping("/citas")
    public String listarTodasLasCitas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Integer clienteId,
            Model model) {

        try {
            // Valores por defecto
            if (fechaInicio == null) fechaInicio = LocalDate.now().minusDays(30);
            if (fechaFin == null) fechaFin = LocalDate.now().plusDays(30);

            // Obtener TODAS las citas
            List<CitaDTO> citas = citaServicio.obtenerCitasPorRango(
                    fechaInicio, fechaFin, clienteId, null);

            // Aplicar filtro de estado si existe
            if (estado != null && !estado.isEmpty()) {
                EstadoCita estadoCita = EstadoCita.valueOf(estado.toUpperCase());
                citas = citas.stream()
                        .filter(c -> c.getEstadoCita() == estadoCita)
                        .collect(Collectors.toList());
            }

            // Calcular estadísticas para las tarjetas
            long total = citas.size();
            long confirmadas = citas.stream()
                    .filter(c -> c.getEstadoCita() == EstadoCita.CONFIRMADA)
                    .count();
            long pendientes = citas.stream()
                    .filter(c -> c.getEstadoCita() == EstadoCita.PENDIENTE)
                    .count();
            long canceladas = citas.stream()
                    .filter(c -> c.getEstadoCita() == EstadoCita.CANCELADA)
                    .count();

            // Datos para la vista
            model.addAttribute("citas", citas);
            model.addAttribute("fechaInicio", fechaInicio);
            model.addAttribute("fechaFin", fechaFin);
            model.addAttribute("estadoSeleccionado", estado);
            model.addAttribute("clienteSeleccionado", clienteId);

            // Estadísticas para las tarjetas
            model.addAttribute("totalCitas", total);
            model.addAttribute("confirmadas", confirmadas);
            model.addAttribute("pendientes", pendientes);
            model.addAttribute("canceladas", canceladas);

            // Para los dropdowns de filtros
            model.addAttribute("estados", EstadoCita.values());

            try {
                model.addAttribute("clientes", usuarioServicio.listarUsuarios());
            } catch (Exception e) {
                // Si el método no existe, no pasa nada
                System.out.println("No se pudieron cargar los clientes: " + e.getMessage());
            }

        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar citas: " + e.getMessage());
            model.addAttribute("citas", Collections.emptyList());
            // Valores por defecto para estadísticas en caso de error
            model.addAttribute("totalCitas", 0);
            model.addAttribute("confirmadas", 0);
            model.addAttribute("pendientes", 0);
            model.addAttribute("canceladas", 0);
        }

        return "empleado/listaCitas";
    }

    //  DETALLE DE CITA
    @GetMapping("/citas/{idCita}")
    public String verDetalleCita(@PathVariable int idCita, Model model) {
        try {
            CitaDTO cita = citaServicio.obtenerCita(idCita);
            model.addAttribute("cita", cita);
            return "empleado/detalleCita";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar cita: " + e.getMessage());
            return "redirect:/empleado/citas";
        }
    }

    //  EDITAR CITA
    @GetMapping("/citas/{idCita}/editar")
    public String editarCita(@PathVariable int idCita, Model model) {
        try {
            CitaDTO cita = citaServicio.obtenerCita(idCita);
            model.addAttribute("cita", cita);
            return "empleado/editarCita";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar cita para editar: " + e.getMessage());
            return "redirect:/empleado/citas";
        }
    }

    // CANCELAR CITA
    @PostMapping("/citas/{idCita}/cancelar")
    public String cancelarCita(@PathVariable int idCita, RedirectAttributes redirectAttributes) {
        try {
            citaServicio.cancelarCita(idCita);
            redirectAttributes.addFlashAttribute("success", "Cita cancelada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cancelar cita: " + e.getMessage());
        }
        return "redirect:/empleado/citas";
    }



}