package org.pedro.rentacar.dao;

import org.pedro.rentacar.db.Database;
import org.pedro.rentacar.model.Cliente;
import org.pedro.rentacar.model.Reserva;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {

    // Crear reserva
    public boolean create(Reserva r, Connection conn) throws SQLException {
        String sql = "INSERT INTO reserva (id_cliente, fecha_inicio, fecha_fin, total) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getCliente().getIdCliente());
            ps.setDate(2, Date.valueOf(r.getFechaInicio()));
            ps.setDate(3, Date.valueOf(r.getFechaFin()));
            ps.setBigDecimal(4, r.getTotal());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    r.setIdReserva(rs.getInt(1));
                    return true;
                }
            }
        }
        return false;
    }

    // Eliminar reserva
    public boolean delete(int idReserva, Connection conn) throws SQLException {
        String sql = "DELETE FROM reserva WHERE id_reserva=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            return ps.executeUpdate() > 0;
        }
    }

    // ---------------------------------------------------------
    // LISTAR TODAS LAS RESERVAS
    // ---------------------------------------------------------
    public List<Reserva> listar() throws SQLException {

        String sql = """
        SELECT r.id_reserva, r.fecha_inicio, r.fecha_fin, r.total,
               c.id_cliente, c.nombre, c.dni, c.telefono, c.email
        FROM reserva r
        JOIN cliente c ON r.id_cliente = c.id_cliente
        ORDER BY r.id_reserva
    """;

        List<Reserva> lista = new ArrayList<>();

        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Cliente cliente = new Cliente(
                        rs.getInt("id_cliente"),
                        rs.getString("nombre"),
                        rs.getString("dni"),
                        rs.getString("telefono"),
                        rs.getString("email")
                );

                Reserva reserva = new Reserva(
                        rs.getInt("id_reserva"),
                        cliente,
                        rs.getDate("fecha_inicio").toLocalDate(),
                        rs.getDate("fecha_fin").toLocalDate(),
                        rs.getBigDecimal("total")
                );

                lista.add(reserva);
            }
        }

        return lista;
    }

}
