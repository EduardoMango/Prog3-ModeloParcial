package org.EduardoMango.view;

import org.EduardoMango.entities.PrestamoEntity;
import org.EduardoMango.entities.UsuarioEntity;
import org.EduardoMango.services.LibroService;
import org.EduardoMango.services.PrestamoService;
import org.EduardoMango.services.UsuarioService;

import java.util.Scanner;

public class Menu {

    public static final LibroService libroService = LibroService.getInstance();
    public static final PrestamoService prestamoService = PrestamoService.getInstance();
    public static final UsuarioService usuarioService = UsuarioService.getInstance();

    public static void run(){

        int opc = 0;
        while(true){
            opc = menu();
            switch (opc){
                case 1 -> listarUsuarios();
                case 2 -> listarPrestamos();
                case 3 -> listarLibros();
                case 4 -> altaUsuario();
                case 5 -> bajaUsuario();
                case 6 -> listarUsuariosConPrestamosActivos();
                case 7 -> listarPrestamosActivos();
                case 8 -> generarPrestamo();
                case 9 -> devolverPrestamo();
                case 10 -> visualizarLibroMasPrestado();
                case 11 -> visualizarTotalLibrosDisponibles();
                case 12 -> visualizarUsuarioConMasPrestamos();
                case 13 -> promedioPrestamosPorUsuarioConPrestamos();
                case 14 -> System.exit(opc);
            }
        }
    }

    public static int menu(){
        Scanner sc = new Scanner(System.in);

        System.out.println("\nMENU:\n");
        System.out.println("1. Listar usuarios");
        System.out.println("2. Listar prestamos");
        System.out.println("3. Listar libros");
        System.out.println("4. Alta de usuario");
        System.out.println("5. Baja de usuario");
        System.out.println("6. Listar usuarios con prestamos activos");
        System.out.println("7. Listar prestamos activos");
        System.out.println("8. Generar Prestamo");
        System.out.println("9. Devolver Prestamo");
        System.out.println("10. Visualizar libro mas prestado");
        System.out.println("11. Visualizar total de libros disponibles");
        System.out.println("12. Visualizar usuario con mayor cantidad de prestamos historicos");
        System.out.println("13. Visualizar promedio de prestamos de usuarios con prestamos");
        System.out.println("14. Salir");

        return sc.nextInt();
    }

    public static void listarUsuarios(){
        usuarioService.findAll()
                .forEach(System.out::println);
    }

    public static void listarPrestamos(){
        prestamoService.findAll()
                .forEach(System.out::println);
    }
    public static void listarLibros(){
        libroService.findAll()
                .forEach(System.out::println);
    }

    public static void altaUsuario(){
        Scanner sc = new Scanner(System.in);

        System.out.println("Ingrese el nombre del usuario: ");
        String nombre = sc.nextLine();
        System.out.println("Ingrese el email del usuario: ");
        String email = sc.nextLine();
        usuarioService.save(UsuarioEntity.builder()
                .nombre(nombre)
                .email(email)
                .id(0).build());
    }

    public static void bajaUsuario(){
        listarUsuarios();
        Scanner sc = new Scanner(System.in);
        System.out.println("Ingrese el id del usuario a eliminar: ");
        usuarioService.delete(sc.nextInt());
    }

    public static void listarUsuariosConPrestamosActivos(){
        System.out.println("Usuarios con prestamos activos: ");
        usuarioService.findAllConPrestamosActivos()
                .forEach(System.out::println);
    }

    public static void listarPrestamosActivos(){
        System.out.println("Prestamos activos: ");
        prestamoService.findAllActivos()
                .forEach(System.out::println);
    }

    public static void generarPrestamo(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Ingrese el id del usuario:");
        int id_usuario = sc.nextInt();
        if (!usuarioService.isBelowMaxPrestamos(id_usuario))
            System.out.println("Limite de prestamos alcanzados");
        else {
            System.out.println("Ingrese el id del libro a prestar");
            int id_libro = sc.nextInt();

            prestamoService.save(PrestamoEntity.builder()
                    .usuario_id(id_usuario)
                    .libro_id(id_libro)
                    .id(0)
                    .build());
        }
    }

    public static void devolverPrestamo(){
        Scanner sc = new Scanner(System.in);

        listarPrestamosActivos();
        System.out.println("Ingrese el id del prestamo a devolver");
        int id_prestamo = sc.nextInt();
        prestamoService.returnPrestamo(id_prestamo);
    }

    public static void visualizarLibroMasPrestado(){
        System.out.println(libroService.findByMaxPrestamos());
    }

    public static void visualizarTotalLibrosDisponibles(){
        System.out.println("Libros Disponibles: ");

        libroService.findAllDisponible().forEach(System.out::println);

        System.out.println("Total de libros disponibles: " + libroService.totalLibrosDisponibles());
    }

    public static void visualizarUsuarioConMasPrestamos(){
        System.out.println(usuarioService.findByMaxPrestamos());
    }

    public static void promedioPrestamosPorUsuarioConPrestamos(){
        System.out.println("Promedio de prestamos por usuario con prestamos: " + prestamoService.promedioPrestamoPorUsuarioConPrestamos() );

    }
}
