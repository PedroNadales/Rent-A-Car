package org.pedro.rentacar.dao;

import org.pedro.rentacar.db.Database;
import org.pedro.rentacar.model.EstadoVehiculo;
import org.pedro.rentacar.model.Vehiculo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehiculoDAO {

    public int insertar(Vehiculo v, Connection conn) throws SQLException {
        String sql = "INSERT INTO vehiculo (matricula, marca, modelo, anio, precio_dia, estado, foto_url) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, v.getMatricula());
            ps.setString(2, v.getMarca());
            ps.setString(3, v.getModelo());
            ps.setObject(4, v.getAnio(), Types.INTEGER);
            ps.setBigDecimal(5, v.getPrecioDia());
            ps.setString(6, v.getEstado().name().toLowerCase());
            ps.setString(7, v.getFotoUrl());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    v.setIdVehiculo(rs.getInt(1));
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public Vehiculo buscarPorId(int id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM vehiculo WHERE id_vehiculo=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return convertir(rs);
            }
        }
        return null;
    }

    public Vehiculo buscarPorMatricula(String matricula, Connection conn) throws SQLException {
        String sql = "SELECT * FROM vehiculo WHERE matricula=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, matricula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return convertir(rs);
            }
        }
        return null;
    }

    public List<Vehiculo> listar() throws SQLException {
        List<Vehiculo> lista = new ArrayList<>();
        String sql = "SELECT * FROM vehiculo";

        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(convertir(rs));
        }
        return lista;
    }
    
    public List<Vehiculo> listarVehiculosDisponibles(Connection conn) throws SQLException {
        List<Vehiculo> lista = new ArrayList<>();
        String sql = "SELECT * FROM vehiculo WHERE estado = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, EstadoVehiculo.DISPONIBLE.name().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(convertir(rs));
                }
            }
        }
        return lista;
    }


    public boolean updateEstado(int idVehiculo, EstadoVehiculo estado, Connection conn) throws SQLException {
        String sql = "UPDATE vehiculo SET estado = ? WHERE id_vehiculo = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, estado.name().toLowerCase());
            stmt.setInt(2, idVehiculo);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean actualizar(Vehiculo v, Connection conn) throws SQLException {
        String sql = "UPDATE vehiculo SET matricula = ?, marca = ?, modelo = ?, anio = ?, " +
                    "precio_dia = ?, estado = ?, foto_url = ? WHERE id_vehiculo = ?";
                    
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, v.getMatricula());
            ps.setString(2, v.getMarca());
            ps.setString(3, v.getModelo());
            ps.setObject(4, v.getAnio(), Types.INTEGER);
            ps.setBigDecimal(5, v.getPrecioDia());
            ps.setString(6, v.getEstado().name().toLowerCase());
            ps.setString(7, v.getFotoUrl());
            ps.setInt(8, v.getIdVehiculo());
            
            return ps.executeUpdate() > 0;
        }
    }
    
    public List<Vehiculo> buscarPorTexto(String texto) throws SQLException {
        List<Vehiculo> resultados = new ArrayList<>();
        String sql = "SELECT * FROM vehiculo WHERE LOWER(matricula) LIKE ? OR LOWER(marca) LIKE ? OR " +
                    "LOWER(modelo) LIKE ? OR anio LIKE ?";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String busqueda = "%" + texto.toLowerCase() + "%";
            String busquedaAnio = "%" + texto + "%";
            
            // Establecer los parámetros de búsqueda
            ps.setString(1, busqueda);
            ps.setString(2, busqueda);
            ps.setString(3, busqueda);
            ps.setString(4, busquedaAnio);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultados.add(convertir(rs));
                }
            }
        }
        return resultados;
    }
    
    private Vehiculo convertir(ResultSet rs) throws SQLException {
        Vehiculo v = new Vehiculo();
        v.setIdVehiculo(rs.getInt("id_vehiculo"));
        v.setMatricula(rs.getString("matricula"));
        v.setMarca(rs.getString("marca"));
        v.setModelo(rs.getString("modelo"));
        v.setAnio(rs.getInt("anio"));
        v.setPrecioDia(rs.getBigDecimal("precio_dia"));
        v.setEstado(EstadoVehiculo.valueOf(rs.getString("estado").toUpperCase()));
        v.setFotoUrl(rs.getString("foto_url"));
        return v;
    }
}
