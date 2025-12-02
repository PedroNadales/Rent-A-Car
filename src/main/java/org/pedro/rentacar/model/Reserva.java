package org.pedro.rentacar.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/*
 * Modelo Reserva para Rent-A-Car
 */
public class Reserva {

    private int idReserva;
    private Cliente cliente;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal total;
    private List<Vehiculo> vehiculos;

    public Reserva() {
        this.vehiculos = new ArrayList<>();
    }

    // Constructor para crear reservas (sin id)
    public Reserva(Cliente cliente, LocalDate fechaInicio, LocalDate fechaFin, BigDecimal total) {
        this();
        this.cliente = cliente;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.total = total;
    }

    // Constructor con id (por ejemplo al leer de BBDD)
    public Reserva(int idReserva, Cliente cliente, LocalDate fechaInicio, LocalDate fechaFin, BigDecimal total) {
        this();
        this.idReserva = idReserva;
        this.cliente = cliente;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.total = total;
    }

    public int getIdReserva() { return idReserva; }
    public void setIdReserva(int idReserva) { this.idReserva = idReserva; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public List<Vehiculo> getVehiculos() { return vehiculos; }
    public void setVehiculos(List<Vehiculo> vehiculos) { this.vehiculos = vehiculos; }
    
    public void agregarVehiculo(Vehiculo vehiculo) { this.vehiculos.add(vehiculo); }

    @Override
    public String toString() {
        return "Reserva{" +
                "idReserva=" + idReserva +
                ", cliente=" + cliente +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", total=" + total +
                '}';
    }
}
