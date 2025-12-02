package org.pedro.rentacar.service;

import org.pedro.rentacar.dao.ReservaDAO;
import org.pedro.rentacar.dao.ReservaVehiculoDAO;
import org.pedro.rentacar.dao.VehiculoDAO;
import org.pedro.rentacar.model.Reserva;
import org.pedro.rentacar.model.Vehiculo;
import org.pedro.rentacar.model.EstadoVehiculo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ReservaService {

    private final ReservaDAO reservaDAO;
    private final ReservaVehiculoDAO reservaVehiculoDAO;
    private final VehiculoDAO vehiculoDAO;

    public ReservaService() {
        this.reservaDAO = new ReservaDAO();
        this.reservaVehiculoDAO = new ReservaVehiculoDAO();
        this.vehiculoDAO = new VehiculoDAO();
    }

    /**
     * Crear una reserva junto con los vehículos asociados y marcar los vehículos como ALQUILADOS,
     * usando la conexión proporcionada.
     */
    public boolean crearReservaConVehiculos(Reserva reserva, List<Vehiculo> vehiculos, Connection conn) throws SQLException {
        // Crear reserva
        if (!reservaDAO.create(reserva, conn)) {
            return false;
        }

        // Asociar vehículos y cambiarlos a ALQUILADO
        for (Vehiculo v : vehiculos) {
            if (!reservaVehiculoDAO.addVehiculoToReserva(reserva, v, conn)
                    || !vehiculoDAO.updateEstado(v.getIdVehiculo(), EstadoVehiculo.ALQUILADO, conn)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Cancelar una reserva y devolver los vehículos a DISPONIBLE
     */
    public boolean cancelarReserva(Reserva reserva, Connection conn) throws SQLException {
        // Devolver vehículos
        List<Vehiculo> vehiculos = reservaVehiculoDAO.findVehiculosByReserva(reserva.getIdReserva(), conn);
        for (Vehiculo v : vehiculos) {
            vehiculoDAO.updateEstado(v.getIdVehiculo(), EstadoVehiculo.DISPONIBLE, conn);
        }

        // Eliminar relaciones y reserva
        reservaVehiculoDAO.removeAllVehiculosFromReserva(reserva.getIdReserva(), conn);
        reservaDAO.delete(reserva.getIdReserva(), conn);

        return true;
    }

    /**
     * Devolver los vehículos de una reserva sin eliminar la reserva
     */
    public boolean devolverVehiculos(Reserva reserva, Connection conn) throws SQLException {
        List<Vehiculo> vehiculos = reservaVehiculoDAO.findVehiculosByReserva(reserva.getIdReserva(), conn);
        for (Vehiculo v : vehiculos) {
            vehiculoDAO.updateEstado(v.getIdVehiculo(), EstadoVehiculo.DISPONIBLE, conn);
        }
        return true;
    }

    public List<Reserva> listarReservas(Connection conn) throws SQLException {
        return reservaDAO.listar();
    }

}
