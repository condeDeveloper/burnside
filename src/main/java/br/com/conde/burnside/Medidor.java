package br.com.conde.burnside;

import java.math.BigInteger;
import java.util.List;

/**
 * Roda as tabelas do README.
 *
 * <pre>
 * mvn -B -ntp exec:java -Dexec.args="colares"
 * mvn -B -ntp exec:java -Dexec.args="media"
 * mvn -B -ntp exec:java -Dexec.args="juiz"
 * mvn -B -ntp exec:java -Dexec.args="primitivos"
 * mvn -B -ntp exec:java -Dexec.args="fundo"     (enumeracao cara, job proprio)
 * </pre>
 */
public final class Medidor {

    public static final int CONTAS = 16;
    public static final int MAX_CONTAS_JUIZ = 12;
    public static final int MAX_CORES_JUIZ = 3;
    public static final int MAX_CONTAS_FUNDO = 16;
    public static final int MAX_CORES_FUNDO = 5;

    public static void main(String[] args) {
        String comando = args.length > 0 ? args[0] : "tudo";
        switch (comando) {
            case "colares" -> colares();
            case "media" -> media();
            case "juiz" -> juiz();
            case "primitivos" -> primitivos();
            case "fundo" -> fundo();
            case "tudo" -> {
                colares();
                media();
                juiz();
                primitivos();
            }
            default -> {
                System.out.println("comando desconhecido: " + comando);
                System.exit(2);
            }
        }
    }

    private static void titulo(String texto) {
        System.out.println();
        System.out.println(texto);
        System.out.println("-".repeat(texto.length()));
    }

    private static void colares() {
        titulo("Colares e braceletes de duas cores, ate " + CONTAS + " contas");
        System.out.printf("%-8s %-12s %-12s %-12s %s%n",
                "contas", "sequencias", "colares", "braceletes", "simetricos");
        for (Varredura.Caso c : Varredura.tabela(CONTAS, 2)) {
            System.out.printf("%-8d %-12d %-12d %-12d %d%n",
                    c.contas(), c.sequencias(), c.colares(), c.braceletes(),
                    Varredura.colaresSimetricos(c.contas(), 2));
        }
        System.out.println();
        System.out.println("\"simetricos\" = colares iguais ao proprio avesso, que nao ganham par");
    }

    private static void media() {
        titulo("De onde sai a media: os pontos fixos de cada rotacao, n = 12, k = 2");
        int n = 12;
        int k = 2;
        List<BigInteger> fixos = Burnside.pontosFixosDasRotacoes(n, k);
        System.out.printf("%-10s %-10s %s%n", "rotacao", "mdc(d,n)", "parados");
        BigInteger soma = BigInteger.ZERO;
        for (int d = 0; d < n; d++) {
            System.out.printf("%-10d %-10d %s%n", d, Burnside.mdc(d, n), fixos.get(d));
            soma = soma.add(fixos.get(d));
        }
        System.out.printf("%nsoma: %s   dividida por %d: %s%n",
                soma, n, soma.divide(BigInteger.valueOf(n)));
        System.out.printf("pela forma fechada com a totiente: %s%n",
                Burnside.colaresPelaTotiente(n, k));
    }

    private static void juiz() {
        titulo("A formula contra a contagem bruta");
        int quantos = Varredura.conferir(MAX_CONTAS_JUIZ, MAX_CORES_JUIZ);
        System.out.printf("pares (contas, cores) conferidos: %d, todos iguais%n", quantos);
        System.out.println();
        System.out.println("alguns deles, lado a lado:");
        System.out.printf("%-8s %-8s %-14s %-14s %-14s %s%n",
                "contas", "cores", "colar formula", "colar bruta", "brac formula", "brac bruta");
        for (int[] par : new int[][] {{6, 2}, {8, 2}, {5, 3}, {6, 3}, {4, 4}, {10, 2}}) {
            int n = par[0];
            int k = par[1];
            System.out.printf("%-8d %-8d %-14s %-14d %-14s %d%n",
                    n, k, Burnside.colares(n, k), Enumeracao.colares(n, k),
                    Burnside.braceletes(n, k), Enumeracao.braceletes(n, k));
        }
    }

    private static void primitivos() {
        titulo("Colares que nao se repetem antes de dar a volta, duas cores");
        System.out.printf("%-8s %-14s %-14s %s%n", "contas", "colares", "aperiodicos", "fatia");
        for (int n = 1; n <= CONTAS; n++) {
            long todos = Burnside.colares(n, 2).longValueExact();
            long livres = Burnside.colaresPrimitivos(n, 2).longValueExact();
            System.out.printf("%-8d %-14d %-14d %.2f%%%n",
                    n, todos, livres, 100.0 * livres / todos);
        }
        System.out.printf("%nconferidos contra a enumeracao: %d pares%n",
                Varredura.conferirPrimitivos(MAX_CONTAS_JUIZ, 3));
    }

    private static void fundo() {
        titulo("Enumeracao ate " + MAX_CONTAS_FUNDO + " contas e " + MAX_CORES_FUNDO + " cores");
        long comeco = System.nanoTime();
        int quantos = Varredura.conferir(MAX_CONTAS_FUNDO, MAX_CORES_FUNDO);
        System.out.printf("pares conferidos: %d, todos iguais%n", quantos);
        long maior = 0;
        int[] onde = null;
        for (int[] par : Varredura.paresQueCabem(MAX_CONTAS_FUNDO, MAX_CORES_FUNDO)) {
            long seq = Enumeracao.quantasSequencias(par[0], par[1]);
            if (seq > maior) {
                maior = seq;
                onde = par;
            }
        }
        System.out.printf("o maior caso enumerado: %d contas em %d cores = %d sequencias%n",
                onde[0], onde[1], maior);
        System.out.printf("%ntempo: %.1f s%n", (System.nanoTime() - comeco) / 1e9);

        if (quantos != 69) {
            System.out.printf("MEDIDA MUDOU: eram 69 pares e vieram %d%n", quantos);
            System.exit(1);
        }
        System.out.println("a medida do README bateu");
    }
}
