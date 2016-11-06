package co.cristiangarcia.bibliareinavalera.node;

public class Libro {
    public static final int FRECUENTES = 3;
    public static final int TESTAMENTO_ANTIGUO = 1;
    public static final int TESTAMENTO_NUEVO = 2;
    private int id;
    private String name;
    private int numcap;

    public Libro(int id, String name, int numcap) {
        this.id = id;
        this.name = name;
        this.numcap = numcap;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getTipo() {
        return this.id < 40 ? TESTAMENTO_ANTIGUO : TESTAMENTO_NUEVO;
    }

    public int getNumCap() {
        return this.numcap;
    }
}
