package org.EduardoMango.entities;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class PrestamoEntity {

    private int id;
    private int usuario_id;
    private int libro_id;
    private LocalDate fecha_prestamo;
    private LocalDate fecha_devolucion;
}
