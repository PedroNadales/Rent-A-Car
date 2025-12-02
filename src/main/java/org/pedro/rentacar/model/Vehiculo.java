package org.pedro.rentacar.model;


import java.math.BigDecimal;
import java.util.Objects;

/*
 * Modelo Vehiculo para Rent-A-Car.
 * Campos principales: id, matricula, marca, modelo, anio, precioDia, estado
 */
public class Vehiculo {

    private int idVehiculo;
    private String matricula;
    private String marca;
    private String modelo;
    private Integer anio;
    private BigDecimal precioDia;
    private EstadoVehiculo estado;
    private String fotoUrl;  // Ruta de la imagen del vehículo

    // Constructor vacío
    public Vehiculo() {}

    // Constructor completo (sin id para creación)
    public Vehiculo(String matricula, String marca, String modelo, Integer anio, BigDecimal precioDia, EstadoVehiculo estado, String fotoUrl) {
        this(matricula, marca, modelo, anio, precioDia, estado);
        this.fotoUrl = fotoUrl;
    }

    public Vehiculo(String matricula, String marca, String modelo, Integer anio, BigDecimal precioDia, EstadoVehiculo estado) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.precioDia = precioDia;
        this.estado = estado;
    }

    // Constructor con id (por ejemplo al leer de la BBDD)
    public Vehiculo(int idVehiculo, String matricula, String marca, String modelo, Integer anio, BigDecimal precioDia, EstadoVehiculo estado, String fotoUrl) {
        this(idVehiculo, matricula, marca, modelo, anio, precioDia, estado);
        this.fotoUrl = fotoUrl;
    }

    public Vehiculo(int idVehiculo, String matricula, String marca, String modelo, Integer anio, BigDecimal precioDia, EstadoVehiculo estado) {
        this.idVehiculo = idVehiculo;
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.precioDia = precioDia;
        this.estado = estado;
    }

    // Getters y setters
    public int getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(int idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public BigDecimal getPrecioDia() {
        return precioDia;
    }

    public void setPrecioDia(BigDecimal precioDia) {
        this.precioDia = precioDia;
    }

    public EstadoVehiculo getEstado() {
        return estado;
    }

    public void setEstado(EstadoVehiculo estado) {
        this.estado = estado;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    @Override
    public String toString() {
        return "Vehiculo{" +
                "idVehiculo=" + idVehiculo +
                ", matricula='" + matricula + '\'' +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", anio=" + anio +
                ", precioDia=" + precioDia +
                ", estado=" + estado +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vehiculo vehiculo = (Vehiculo) o;
        return Objects.equals(matricula, vehiculo.matricula); // matrícula única
    }

    @Override
    public int hashCode() {
        return Objects.hash(matricula);
    }
}
