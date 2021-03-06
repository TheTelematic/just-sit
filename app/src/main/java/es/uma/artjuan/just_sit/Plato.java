package es.uma.artjuan.just_sit;

/**
 * Created by Juanca y Arturo on 10/05/2017.
 */

public class Plato {
    private String id=null;
    private String nombre=null;
    private String precio=null;
    private boolean marcado=false;
    private int cantidad=0;

    public int getCantidad() {
        return cantidad;
    }

    // Se supone que si ya existe una instancia de esta clase, este metodo ahorra la actualizacion manual
    public void setCantidad(int cantidad) {
        this.cantidad += cantidad;
    }


    public Plato(String id,String precio, String nombre, boolean marcado) {
        super();
        this.id=id;
        this.precio = precio;
        this.nombre = nombre;
        this.marcado = marcado;
    }

    public Plato(String id,String precio, String nombre, boolean marcado, int cantidad) {
        super();
        this.id=id;
        this.precio = precio;
        this.nombre = nombre;
        this.marcado = marcado;
        this.cantidad = cantidad;
    }

    public void setId(String id){
        this.id=id;
    }

    public String getId(){
        return id;
    }

    public String getNombre(){
        return nombre;
    }

    public String getPrecio(){
        return precio;
    }

    public void setNombre(String nombre){
        this.nombre=nombre;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public boolean isMarcado() {
        return marcado;
    }

    public void setMarcado(boolean marcado){
        this.marcado=marcado;
    }


}
