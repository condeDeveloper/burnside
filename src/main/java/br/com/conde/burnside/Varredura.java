package br.com.conde.burnside;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/** Varre todos os pares (n, k) que cabem e confronta a formula com a contagem bruta. */
public final class Varredura {

    public record Caso(int contas, int cores, long colares, long braceletes, long sequencias) {
    }

    private Varredura() {
    }

    /** Todos os pares (n,k) cuja enumeracao cabe no teto. */
    public static List<int[]> paresQueCabem(int maxContas, int maxCores) {
        List<int[]> pares = new ArrayList<>();
        for (int n = 1; n <= maxContas; n++) {
            for (int k = 1; k <= maxCores; k++) {
                if (Enumeracao.quantasSequencias(n, k) != Long.MAX_VALUE) {
                    pares.add(new int[] {n, k});
                }
            }
        }
        return pares;
    }

    /**
     * Confere a formula de Burnside contra a enumeracao em todo par que cabe.
     *
     * @return quantos pares foram conferidos
     */
    public static int conferir(int maxContas, int maxCores) {
        int conferidos = 0;
        for (int[] par : paresQueCabem(maxContas, maxCores)) {
            int n = par[0];
            int k = par[1];

            BigInteger porFormula = Burnside.colares(n, k);
            long porForcaBruta = Enumeracao.colares(n, k);
            if (porFormula.longValueExact() != porForcaBruta) {
                throw new IllegalStateException("colares discordaram em n=" + n + ", k=" + k
                        + ": formula " + porFormula + ", bruta " + porForcaBruta);
            }

            BigInteger braceletesFormula = Burnside.braceletes(n, k);
            long braceletesBruta = Enumeracao.braceletes(n, k);
            if (braceletesFormula.longValueExact() != braceletesBruta) {
                throw new IllegalStateException("braceletes discordaram em n=" + n + ", k=" + k
                        + ": formula " + braceletesFormula + ", bruta " + braceletesBruta);
            }

            if (!Burnside.colaresPelaTotiente(n, k).equals(porFormula)) {
                throw new IllegalStateException(
                        "as duas formas do lema discordaram em n=" + n + ", k=" + k);
            }
            conferidos++;
        }
        return conferidos;
    }

    /** Confere a contagem de aperiodicos contra a enumeracao. */
    public static int conferirPrimitivos(int maxContas, int maxCores) {
        int conferidos = 0;
        for (int[] par : paresQueCabem(maxContas, maxCores)) {
            int n = par[0];
            int k = par[1];
            BigInteger porMobius = Burnside.colaresPrimitivos(n, k);
            long bruta = Enumeracao.sequenciasAperiodicas(n, k);
            if (porMobius.longValueExact() * n != bruta) {
                throw new IllegalStateException("primitivos discordaram em n=" + n + ", k=" + k
                        + ": formula diz " + porMobius + " classes (" + porMobius.longValueExact() * n
                        + " sequencias), bruta achou " + bruta + " sequencias");
            }
            conferidos++;
        }
        return conferidos;
    }

    /** A tabela de colares e braceletes para duas cores. */
    public static List<Caso> tabela(int maxContas, int cores) {
        List<Caso> linhas = new ArrayList<>();
        for (int n = 1; n <= maxContas; n++) {
            linhas.add(new Caso(n, cores,
                    Burnside.colares(n, cores).longValueExact(),
                    Burnside.braceletes(n, cores).longValueExact(),
                    (long) Math.pow(cores, n)));
        }
        return linhas;
    }

    /** Quantos colares sao simetricos, isto e, iguais ao proprio avesso. */
    public static long colaresSimetricos(int n, int k) {
        return 2L * Burnside.braceletes(n, k).longValueExact()
                - Burnside.colares(n, k).longValueExact();
    }
}
