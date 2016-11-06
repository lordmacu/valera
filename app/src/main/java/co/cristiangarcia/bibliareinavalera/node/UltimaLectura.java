package co.cristiangarcia.bibliareinavalera.node;

import co.cristiangarcia.bibliareinavalera.BuildConfig;
import co.cristiangarcia.bibliareinavalera.util.LibrosHelper;

public class UltimaLectura {
    private Lectura marcado = null;
    private Lectura visto = null;

    public static class Lectura {
        private int capitulo;
        private int libro;

        public Lectura(int libro, int capitulo) {
            this.libro = libro;
            this.capitulo = capitulo;
        }

        public int getIdLibro() {
            return this.libro;
        }

        public int getCapitulo() {
            return this.capitulo;
        }

        public String getLectura() {
            Libro nodo = LibrosHelper.getLibro(this.libro);
            if (nodo != null) {
                return nodo.getName() + " " + this.capitulo;
            }
            return BuildConfig.FLAVOR;
        }
    }

    public UltimaLectura(Lectura visto, Lectura marcado) {
        this.visto = visto;
        this.marcado = marcado;
    }

    public boolean hasData() {
        return hasVisto() || hasMarcado();
    }

    public boolean hasVisto() {
        return this.visto != null;
    }

    public boolean hasMarcado() {
        return this.marcado != null;
    }

    public Lectura getVisto() {
        return this.visto;
    }

    public Lectura getMarcado() {
        return this.marcado;
    }
}
