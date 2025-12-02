package org.pedro.rentacar.dao;

import org.pedro.rentacar.db.Database;
import org.pedro.rentacar.model.Mantenimiento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MantenimientoDAO {

    public boolean create(Mantenimiento m) {
        String sql = "INSERT INTO mantenimiento (id_vehiculo, tipo, fecha, coste) VALUES (?, ?, ?, ?)";

        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, m.getIdVehiculo());
            ps.setString(2, m.getTipo());
            ps.setDate(3, Date.valueOf(m.getFecha()));
            ps.setBigDecimal(4, m.getCoste());

            int filas = ps.executeUpdate();
            if (filas == 0) return false;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) m.setIdMantenimiento(rs.getInt(1));
            }
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Mantenimiento read(int id) {
        String sql = "SELECT * FROM mantenimiento WHERE id_mantenimiento = ?";

        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Mantenimiento(
                        rs.getInt("id_mantenimiento"),
                        rs.getInt("id_vehiculo"),
                        rs.getString("tipo"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getBigDecimal("coste")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Mantenimiento> findByVehiculo(int idVehiculo) {
        List<Mantenimiento> lista = new ArrayList<>();
        String sql = "SELECT * FROM mantenimiento WHERE id_vehiculo = ?";

        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idVehiculo);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(new Mantenimiento(
                        rs.getInt("id_mantenimiento"),
                        rs.getInt("id_vehiculo"),
                        rs.getString("tipo"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getBigDecimal("coste")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean update(Mantenimiento m) {
        String sql = "UPDATE mantenimiento SET id_vehiculo=?, tipo=?, fecha=?, coste=? WHERE id_mantenimiento=?";

        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, m.getIdVehiculo());
            ps.setString(2, m.getTipo());
            ps.setDate(3, Date.valueOf(m.getFecha()));
            ps.setBigDecimal(4, m.getCoste());
            ps.setInt(5, m.getIdMantenimiento());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM mantenimiento WHERE id_mantenimiento=?";

        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
