package co.edu.uniquindio.BarakaLashes.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@Table(name = "empleado")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer idEmpleado;
    String nombre;
    String apellido;
    String email;
    String telefono;
    String direccion;
    Double sueldo;

}
