package org.pedro.rentacar.controller;

import org.pedro.rentacar.model.Reserva;
import org.pedro.rentacar.model.Vehiculo;
import org.pedro.rentacar.service.ReservaService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/*
 * Controlador para manejar operaciones sobre reservas
 */
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController() throws SQLException {
        this.reservaService = new ReservaService();
    }

    // Crear reserva con vehículos usando la conexión pasada
    public boolean crearReserva(Reserva reserva, List<Vehiculo> vehiculos, Connection conn) {
        try {
            return reservaService.crearReservaConVehiculos(reserva, vehiculos, conn);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Devolver vehículos de una reserva
    public boolean devolverVehiculos(Reserva reserva, Connection conn) {
        try {
            return reservaService.devolverVehiculos(reserva, conn);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cancelar reserva
    public boolean cancelarReserva(Reserva reserva, Connection conn) {
        try {
            return reservaService.cancelarReserva(reserva, conn);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
