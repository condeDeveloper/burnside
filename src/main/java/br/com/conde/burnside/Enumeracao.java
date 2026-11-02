package br.com.conde.burnside;

import java.util.HashSet;
import java.util.Set;

/**
 * O juiz: constroi TODAS as k^n sequencias, reduz cada uma a uma forma
 * canonica e conta quantas formas distintas sobraram.
 *
 * <p>Nao conhece o lema de Burnside, nao conhece totiente, nao conhece grupo.
 * So sabe girar uma sequencia, virar ela do avesso, e guardar a menor das
 * versoes num conjunto. Se o lema e a contagem bruta chegarem ao mesmo numero
 * em todos os casos varridos, a formula esta certa.
 */
public final class Enumeracao {

    /** Teto de sequencias: acima disso a enumeracao e recusada em vez de travar. */
    public static final long TETO = 20_000_000L;

    private Enumeracao() {
    }

    public static long quantasSequencias(int n, int k) {
        long total = 1;
        for (int i = 0; i < n; i++) {
            if (total > TETO / k) {
                return Long.MAX_VALUE;
            }
            total *= k;
        }
        return total;
    }

    /** Classes sob rotacao, contadas na marra. */
    public static long colares(int n, int k) {
        return contar(n, k, false);
    }

    /** Classes sob rotacao e reflexao, contadas na marra. */
    public static long braceletes(int n, int k) {
        return contar(n, k, true);
    }

    private static long contar(int n, int k, boolean comReflexao) {
        long total = quantasSequencias(n, k);
        if (total == Long.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "sequencias demais para enumerar: " + k + "^" + n);
        }
        Set<Long> vistos = new HashSet<>();
        int[] peca = new int[n];
        for (long codigo = 0; codigo < total; codigo++) {
            long resto = codigo;
            for (int i = 0; i < n; i++) {
                peca[i] = (int) (resto % k);
                resto /= k;
            }
            vistos.add(canonica(peca, k, comReflexao));
        }
        return vistos.size();
    }

    /**
     * A forma canonica: o MENOR CODIGO entre todas as rotacoes da peca (e, se
     * pedido, tambem do avesso dela).
     *
     * <p>O codigo e a propria peca lida como numero na base k. Comparar numeros
     * em vez de texto nao muda uma classe sequer, e foi o que derrubou a
     * conferencia de quase cinco minutos para alguns segundos: montar um
     * {@code Arrays.toString} por rotacao alocava uma string para cada uma das
     * dezenas de milhoes de pecas.
     */
    public static long canonica(int[] peca, int k, boolean comReflexao) {
        long melhor = menorRotacao(peca, k);
        if (comReflexao) {
            int n = peca.length;
            int[] avesso = new int[n];
            for (int i = 0; i < n; i++) {
                avesso[i] = peca[n - 1 - i];
            }
            melhor = Math.min(melhor, menorRotacao(avesso, k));
        }
        return melhor;
    }

    private static long menorRotacao(int[] peca, int k) {
        int n = peca.length;
        long melhor = Long.MAX_VALUE;
        for (int d = 0; d < n; d++) {
            long codigo = 0;
            for (int i = 0; i < n; i++) {
                codigo = codigo * k + peca[(i + d) % n];
            }
            if (codigo < melhor) {
                melhor = codigo;
            }
        }
        return melhor;
    }

    /** Quantas sequencias nao se repetem antes de dar a volta inteira. */
    public static long sequenciasAperiodicas(int n, int k) {
        long total = quantasSequencias(n, k);
        if (total == Long.MAX_VALUE) {
            throw new IllegalArgumentException("sequencias demais: " + k + "^" + n);
        }
        long quantas = 0;
        int[] peca = new int[n];
        for (long codigo = 0; codigo < total; codigo++) {
            long resto = codigo;
            for (int i = 0; i < n; i++) {
                peca[i] = (int) (resto % k);
                resto /= k;
            }
            if (periodo(peca) == n) {
                quantas++;
            }
        }
        return quantas;
    }

    /** O menor d que divide n tal que a peca se repete a cada d contas. */
    public static int periodo(int[] peca) {
        int n = peca.length;
        for (int d = 1; d <= n; d++) {
            if (n % d != 0) {
                continue;
            }
            boolean serve = true;
            for (int i = 0; i < n && serve; i++) {
                if (peca[i] != peca[i % d]) {
                    serve = false;
                }
            }
            if (serve) {
                return d;
            }
        }
        return n;
    }
}
