package br.com.conde.burnside;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * O lema de Burnside: o numero de classes e a MEDIA de pontos fixos das
 * simetrias.
 *
 * <pre>classes = (1 / |G|) * soma, sobre g em G, de quantas pecas g nao mexe</pre>
 *
 * <p>Para colar de n contas em k cores, o grupo e o das n rotacoes. A rotacao
 * de d casas nao mexe num colar exatamente quando o colar se repete a cada
 * mdc(d,n) contas, e existem k^mdc(d,n) colares assim. Agrupando as rotacoes
 * pelo mdc sai a forma fechada com a totiente.
 *
 * <p>Para bracelete entram tambem as n reflexoes, e elas se comportam de
 * maneiras diferentes conforme n seja par ou impar — essa diferenca e o ponto
 * em que e mais facil errar, e por isso ela e medida contra a enumeracao.
 */
public final class Burnside {

    private Burnside() {
    }

    /** Quantos colares de n contas em k cores a rotacao de d casas deixa parados. */
    public static BigInteger fixosDaRotacao(int n, int k, int d) {
        return BigInteger.valueOf(k).pow(mdc(d, n));
    }

    /**
     * Quantos deixa parados a reflexao de numero r.
     *
     * <p>Com n impar, toda reflexao passa por uma conta e pelo meio do lado
     * oposto: ela tem (n+1)/2 orbitas. Com n par, metade das reflexoes passa
     * por duas contas (n/2 + 1 orbitas) e metade passa por dois lados
     * (n/2 orbitas).
     */
    public static BigInteger fixosDaReflexao(int n, int k, int r) {
        int orbitas;
        if (n % 2 == 1) {
            orbitas = (n + 1) / 2;
        } else {
            orbitas = r % 2 == 0 ? n / 2 + 1 : n / 2;
        }
        return BigInteger.valueOf(k).pow(orbitas);
    }

    /** Colares: classes sob rotacao. */
    public static BigInteger colares(int n, int k) {
        checar(n, k);
        BigInteger soma = BigInteger.ZERO;
        for (int d = 0; d < n; d++) {
            soma = soma.add(fixosDaRotacao(n, k, d));
        }
        return soma.divide(BigInteger.valueOf(n));
    }

    /** A mesma conta na forma fechada, agrupando as rotacoes pela totiente. */
    public static BigInteger colaresPelaTotiente(int n, int k) {
        checar(n, k);
        BigInteger soma = BigInteger.ZERO;
        for (int d = 1; d <= n; d++) {
            if (n % d == 0) {
                soma = soma.add(BigInteger.valueOf(totiente(n / d))
                        .multiply(BigInteger.valueOf(k).pow(d)));
            }
        }
        return soma.divide(BigInteger.valueOf(n));
    }

    /** Braceletes: classes sob rotacao e reflexao. */
    public static BigInteger braceletes(int n, int k) {
        checar(n, k);
        BigInteger soma = BigInteger.ZERO;
        for (int d = 0; d < n; d++) {
            soma = soma.add(fixosDaRotacao(n, k, d));
        }
        for (int r = 0; r < n; r++) {
            soma = soma.add(fixosDaReflexao(n, k, r));
        }
        return soma.divide(BigInteger.valueOf(2L * n));
    }

    /**
     * Colares APERIODICOS: os que nao se repetem antes de dar a volta inteira.
     * Pela inversao de Mobius sobre a contagem total.
     */
    public static BigInteger colaresPrimitivos(int n, int k) {
        checar(n, k);
        BigInteger soma = BigInteger.ZERO;
        for (int d = 1; d <= n; d++) {
            if (n % d == 0) {
                soma = soma.add(BigInteger.valueOf(mobius(n / d))
                        .multiply(BigInteger.valueOf(k).pow(d)));
            }
        }
        return soma.divide(BigInteger.valueOf(n));
    }

    /** Os pontos fixos de cada simetria, para mostrar de onde a media sai. */
    public static List<BigInteger> pontosFixosDasRotacoes(int n, int k) {
        List<BigInteger> lista = new ArrayList<>();
        for (int d = 0; d < n; d++) {
            lista.add(fixosDaRotacao(n, k, d));
        }
        return lista;
    }

    private static void checar(int n, int k) {
        if (n < 1) {
            throw new IllegalArgumentException("precisa de pelo menos uma conta: " + n);
        }
        if (k < 1) {
            throw new IllegalArgumentException("precisa de pelo menos uma cor: " + k);
        }
    }

    public static int mdc(int a, int b) {
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return Math.abs(a);
    }

    public static int totiente(int n) {
        int quantos = 0;
        for (int i = 1; i <= n; i++) {
            if (mdc(i, n) == 1) {
                quantos++;
            }
        }
        return quantos;
    }

    public static int mobius(int n) {
        if (n == 1) {
            return 1;
        }
        int resto = n;
        int primos = 0;
        for (int p = 2; (long) p * p <= resto; p++) {
            if (resto % p == 0) {
                resto /= p;
                if (resto % p == 0) {
                    return 0;
                }
                primos++;
            }
        }
        if (resto > 1) {
            primos++;
        }
        return primos % 2 == 0 ? 1 : -1;
    }
}
