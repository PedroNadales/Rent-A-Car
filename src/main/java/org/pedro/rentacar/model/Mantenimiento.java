package org.pedro.rentacar.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/*
 * Modelo Mantenimiento para el historial de cada vehículo.
 */
public class Mantenimiento {

    private int idMantenimiento;
    private int idVehiculo; // FK a Vehiculo
    private String tipo;
    private LocalDate fecha;
    private BigDecimal coste;

    public Mantenimiento() {}

    public Mantenimiento(int idVehiculo, String tipo, LocalDate fecha, BigDecimal coste) {
        this.idVehiculo = idVehiculo;
        this.tipo = tipo;
        this.fecha = fecha;
        this.coste = coste;
    }

    public Mantenimiento(int idMantenimiento, int idVehiculo, String tipo, LocalDate fecha, BigDecimal coste) {
        this.idMantenimiento = idMantenimiento;
        this.idVehiculo = idVehiculo;
        this.tipo = tipo;
        this.fecha = fecha;
        this.coste = coste;
    }

    public int getIdMantenimiento() {
        return idMantenimiento;
    }

    public void setIdMantenimiento(int idMantenimiento) {
        this.idMantenimiento = idMantenimiento;
    }

    public int getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(int idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getCoste() {
        return coste;
    }

    public void setCoste(BigDecimal coste) {
        this.coste = coste;
    }

    @Override
    public String toString() {
        return "Mantenimiento{" +
                "idMantenimiento=" + idMantenimiento +
                ", idVehiculo=" + idVehiculo +
                ", tipo='" + tipo + '\'' +
                ", fecha=" + fecha +
                ", coste=" + coste +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mantenimiento that = (Mantenimiento) o;
        return idMantenimiento == that.idMantenimiento;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idMantenimiento);
    }
}

