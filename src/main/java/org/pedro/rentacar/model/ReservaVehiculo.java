package org.pedro.rentacar.model;

import java.util.Objects;

/*
 * Modelo ReservaVehiculo (tabla intermedia)
 * Representa los vehículos asociados a una reserva (clave compuesta).
 */
public class ReservaVehiculo {

    private int idReserva;
    private int idVehiculo;

    public ReservaVehiculo() {}

    public ReservaVehiculo(int idReserva, int idVehiculo) {
        this.idReserva = idReserva;
        this.idVehiculo = idVehiculo;
    }

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public int getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(int idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReservaVehiculo that = (ReservaVehiculo) o;
        return idReserva == that.idReserva && idVehiculo == that.idVehiculo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idReserva, idVehiculo);
    }

    @Override
    public String toString() {
        return "ReservaVehiculo{" +
                "idReserva=" + idReserva +
                ", idVehiculo=" + idVehiculo +
                '}';
    }
}

