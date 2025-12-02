package org.pedro.rentacar.dao;

import org.pedro.rentacar.model.EstadoVehiculo;
import org.pedro.rentacar.model.Reserva;
import org.pedro.rentacar.model.Vehiculo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaVehiculoDAO {

    public boolean addVehiculoToReserva(Reserva r, Vehiculo v, Connection conn) throws SQLException {
        String sql = "INSERT INTO reserva_vehiculo (id_reserva, id_vehiculo) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getIdReserva());
            ps.setInt(2, v.getIdVehiculo());
            return ps.executeUpdate() > 0;
        }
    }

    public void removeAllVehiculosFromReserva(int idReserva, Connection conn) throws SQLException {
        String sql = "DELETE FROM reserva_vehiculo WHERE id_reserva=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ps.executeUpdate();
        }
    }

    public List<Vehiculo> findVehiculosByReserva(int idReserva, Connection conn) throws SQLException {
        String sql = "SELECT v.* FROM vehiculo v JOIN reserva_vehiculo rv ON v.id_vehiculo = rv.id_vehiculo WHERE rv.id_reserva=?";
        List<Vehiculo> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Vehiculo(
                            rs.getInt("id_vehiculo"),
                            rs.getString("matricula"),
                            rs.getString("marca"),
                            rs.getString("modelo"),
                            (Integer) rs.getObject("anio"),
                            rs.getBigDecimal("precio_dia"),
                            rs.getString("estado").equalsIgnoreCase("alquilado") ?
                                    EstadoVehiculo.ALQUILADO :
                                    rs.getString("estado").equalsIgnoreCase("mantenimiento") ?
                                            EstadoVehiculo.MANTENIMIENTO : EstadoVehiculo.DISPONIBLE
                    ));
                }
            }
        }
        return lista;
    }
}
