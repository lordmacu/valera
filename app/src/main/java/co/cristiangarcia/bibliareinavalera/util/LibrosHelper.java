package co.cristiangarcia.bibliareinavalera.util;

import java.util.ArrayList;
import co.cristiangarcia.bibliareinavalera.BuildConfig;
import co.cristiangarcia.bibliareinavalera.node.Libro;

public class LibrosHelper {
    private static ArrayList<Libro> listAT = null;
    private static ArrayList<Libro> listNT = null;

    public static ArrayList<Libro> getLibrosAT() {
        return getListAT();
    }

    public static ArrayList<Libro> getLibrosNT() {
        return getListNT();
    }

    public static Libro getLibro(int id) {
        ArrayList<Libro> libros;
        if (id < 40) {
            libros = getListAT();
            if (libros != null) {
                return (Libro) libros.get(id - 1);
            }
        }
        libros = getListNT();
        if (libros != null) {
            return (Libro) libros.get(id - 40);
        }
        return null;
    }

    public static String getTitleLibCaps(int libro, int capitulo, int versiculoi, int versiculof) {
        Libro nlibro = getLibro(libro);
        if (nlibro == null || versiculoi == 0) {
            return BuildConfig.FLAVOR;
        }
        return nlibro.getName() + " " + capitulo + ":" + versiculoi + (versiculoi < versiculof ? "-" + versiculof : BuildConfig.FLAVOR);
    }

    public static String getUrlLibCaps(int libro, int capitulo, int versiculoi, int versiculof) {
        Libro nlibro = getLibro(libro);
        if (nlibro == null || versiculoi == 0) {
            return BuildConfig.FLAVOR;
        }
        return "http://tusversiculos.com/v/" + nlibro.getId() + "/" + capitulo + "/" + versiculoi + "/" + (versiculoi < versiculof ? versiculof + "/" : BuildConfig.FLAVOR);
    }

    private static ArrayList<Libro> getListAT() {
        if (listAT == null) {
            listAT = new ArrayList();
            listAT.add(new Libro(1, "G\u00e9nesis", 50));
            listAT.add(new Libro(2, "\u00c9xodo", 40));
            listAT.add(new Libro(3, "Lev\u00edtico", 27));
            listAT.add(new Libro(4, "N\u00fameros", 36));
            listAT.add(new Libro(5, "Deuteronomio", 34));
            listAT.add(new Libro(6, "Josu\u00e9", 24));
            listAT.add(new Libro(7, "Jueces", 21));
            listAT.add(new Libro(8, "Rut", 4));
            listAT.add(new Libro(9, "1 Samuel", 31));
            listAT.add(new Libro(10, "2 Samuel", 24));
            listAT.add(new Libro(11, "1 Reyes", 22));
            listAT.add(new Libro(12, "2 Reyes", 25));
            listAT.add(new Libro(13, "1 Cr\u00f3nicas", 29));
            listAT.add(new Libro(14, "2 Cr\u00f3nicas", 36));
            listAT.add(new Libro(15, "Esdras", 10));
            listAT.add(new Libro(16, "Nehem\u00edas", 13));
            listAT.add(new Libro(17, "Ester", 10));
            listAT.add(new Libro(18, "Job", 42));
            listAT.add(new Libro(19, "Salmos", 150));
            listAT.add(new Libro(20, "Proverbios", 31));
            listAT.add(new Libro(21, "Eclesiast\u00e9s", 12));
            listAT.add(new Libro(22, "Cantares", 8));
            listAT.add(new Libro(23, "Isa\u00edas", 66));
            listAT.add(new Libro(24, "Jerem\u00edas", 52));
            listAT.add(new Libro(25, "Lamentaciones", 5));
            listAT.add(new Libro(26, "Ezequiel", 48));
            listAT.add(new Libro(27, "Daniel", 12));
            listAT.add(new Libro(28, "Oseas", 14));
            listAT.add(new Libro(29, "Joel", 3));
            listAT.add(new Libro(30, "Am\u00f3s", 9));
            listAT.add(new Libro(31, "Abd\u00edas", 1));
            listAT.add(new Libro(32, "Jon\u00e1s", 4));
            listAT.add(new Libro(33, "Miqueas", 7));
            listAT.add(new Libro(34, "Nah\u00fam", 3));
            listAT.add(new Libro(35, "Habacuc", 3));
            listAT.add(new Libro(36, "Sofon\u00edas", 3));
            listAT.add(new Libro(37, "Hageo", 2));
            listAT.add(new Libro(38, "Zacar\u00edas", 14));
            listAT.add(new Libro(39, "Malaqu\u00edas", 4));
        }
        return listAT;
    }

    private static ArrayList<Libro> getListNT() {
        if (listNT == null) {
            listNT = new ArrayList();
            listNT.add(new Libro(40, "Mateo", 28));
            listNT.add(new Libro(41, "Marcos", 16));
            listNT.add(new Libro(42, "Lucas", 24));
            listNT.add(new Libro(43, "Juan", 21));
            listNT.add(new Libro(44, "Hechos", 28));
            listNT.add(new Libro(45, "Romanos", 16));
            listNT.add(new Libro(46, "1 Corintios", 16));
            listNT.add(new Libro(47, "2 Corintios", 13));
            listNT.add(new Libro(48, "G\u00e1latas", 6));
            listNT.add(new Libro(49, "Efesios", 6));
            listNT.add(new Libro(50, "Filipenses", 4));
            listNT.add(new Libro(51, "Colosenses", 4));
            listNT.add(new Libro(52, "1 Tesalonicenses", 5));
            listNT.add(new Libro(53, "2 Tesalonicenses", 3));
            listNT.add(new Libro(54, "1 Timoteo", 6));
            listNT.add(new Libro(55, "2 Timoteo", 4));
            listNT.add(new Libro(56, "Tito", 3));
            listNT.add(new Libro(57, "Filem\u00f3n", 1));
            listNT.add(new Libro(58, "Hebreos", 13));
            listNT.add(new Libro(59, "Santiago", 5));
            listNT.add(new Libro(60, "1 Pedro", 5));
            listNT.add(new Libro(61, "2 Pedro", 3));
            listNT.add(new Libro(62, "1 Juan", 5));
            listNT.add(new Libro(63, "2 Juan", 1));
            listNT.add(new Libro(64, "3 Juan", 1));
            listNT.add(new Libro(65, "Judas", 1));
            listNT.add(new Libro(66, "Apocalipsis", 22));
        }
        return listNT;
    }
}
