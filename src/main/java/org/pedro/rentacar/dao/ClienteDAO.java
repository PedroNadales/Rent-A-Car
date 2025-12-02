package org.pedro.rentacar.dao;

import org.pedro.rentacar.db.Database;
import org.pedro.rentacar.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public int insertar(Cliente c) throws SQLException {

        String sql = "INSERT INTO cliente (nombre, dni, telefono, email) VALUES (?, ?, ?, ?)";

        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDni());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getEmail());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    c.setIdCliente(rs.getInt(1));
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public Cliente buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM cliente WHERE id_cliente = ?";

        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return convertir(rs);
            }
        }
        return null;
    }

    public List<Cliente> listar() throws SQLException {
        String sql = "SELECT * FROM cliente";
        List<Cliente> lista = new ArrayList<>();

        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(convertir(rs));
        }
        return lista;
    }

    public boolean actualizar(Cliente c) throws SQLException {
        String sql = "UPDATE cliente SET nombre=?, dni=?, telefono=?, email=? WHERE id_cliente=?";

        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDni());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getEmail());
            ps.setInt(5, c.getIdCliente());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM cliente WHERE id_cliente=?";

        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Cliente> buscarPorTexto(String texto) throws SQLException {
        String sql = "SELECT * FROM cliente WHERE LOWER(nombre) LIKE ? OR LOWER(dni) LIKE ? OR " +
                    "LOWER(telefono) LIKE ? OR LOWER(email) LIKE ?";
        
        List<Cliente> resultados = new ArrayList<>();
        String busqueda = "%" + texto.toLowerCase() + "%";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // Establecer los parámetros de búsqueda
            for (int i = 1; i <= 4; i++) {
                ps.setString(i, busqueda);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultados.add(convertir(rs));
                }
            }
        }
        return resultados;
    }
    
    private Cliente convertir(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id_cliente"),
                rs.getString("nombre"),
                rs.getString("dni"),
                rs.getString("telefono"),
                rs.getString("email")
        );
    }
}
