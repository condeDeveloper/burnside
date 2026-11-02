package br.com.conde.burnside;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BurnsideTest {

    @Test
    @DisplayName("casos de cabeceira que se conta no dedo")
    void cabeceira() {
        // uma conta: um colar por cor
        assertThat(Burnside.colares(1, 2)).isEqualTo(BigInteger.TWO);
        assertThat(Burnside.colares(1, 5)).isEqualTo(BigInteger.valueOf(5));
        // duas contas, duas cores: AA, AB, BB
        assertThat(Burnside.colares(2, 2)).isEqualTo(BigInteger.valueOf(3));
        // tres contas, duas cores: AAA, AAB, ABB, BBB
        assertThat(Burnside.colares(3, 2)).isEqualTo(BigInteger.valueOf(4));
        // uma cor so: sempre um colar
        for (int n = 1; n <= 10; n++) {
            assertThat(Burnside.colares(n, 1)).as("n = %d", n).isEqualTo(BigInteger.ONE);
        }
    }

    @Test
    @DisplayName("as duas formas do lema dao o mesmo numero")
    void asDuasFormas() {
        for (int n = 1; n <= 30; n++) {
            for (int k = 1; k <= 6; k++) {
                assertThat(Burnside.colaresPelaTotiente(n, k))
                        .as("n = %d, k = %d", n, k)
                        .isEqualTo(Burnside.colares(n, k));
            }
        }
    }

    @Test
    @DisplayName("bracelete nunca passa colar, e nunca fica abaixo da metade")
    void braceleteEntreMetadeEOTodo() {
        for (int n = 1; n <= 20; n++) {
            for (int k = 2; k <= 5; k++) {
                BigInteger colares = Burnside.colares(n, k);
                BigInteger braceletes = Burnside.braceletes(n, k);
                assertThat(braceletes).as("n=%d k=%d", n, k).isLessThanOrEqualTo(colares);
                assertThat(braceletes.multiply(BigInteger.TWO))
                        .as("n=%d k=%d", n, k)
                        .isGreaterThanOrEqualTo(colares);
            }
        }
    }

    @Test
    @DisplayName("com n primo, a conta vira (k^n - k)/n + k")
    void formulaDoPrimo() {
        for (int p : new int[] {2, 3, 5, 7, 11, 13}) {
            for (int k = 2; k <= 5; k++) {
                BigInteger esperado = BigInteger.valueOf(k).pow(p)
                        .subtract(BigInteger.valueOf(k))
                        .divide(BigInteger.valueOf(p))
                        .add(BigInteger.valueOf(k));
                assertThat(Burnside.colares(p, k)).as("p=%d k=%d", p, k).isEqualTo(esperado);
            }
        }
    }

    @Test
    @DisplayName("a rotacao zero deixa TODAS as pecas paradas")
    void rotacaoZeroFixaTudo() {
        for (int n = 1; n <= 12; n++) {
            for (int k = 2; k <= 4; k++) {
                assertThat(Burnside.fixosDaRotacao(n, k, 0))
                        .as("n=%d k=%d", n, k)
                        .isEqualTo(BigInteger.valueOf(k).pow(n));
            }
        }
    }

    @Test
    @DisplayName("a soma de aperiodicos sobre os divisores devolve o total")
    void aperiodicosSomamOTotal() {
        for (int n = 1; n <= 20; n++) {
            for (int k = 2; k <= 4; k++) {
                BigInteger soma = BigInteger.ZERO;
                for (int d = 1; d <= n; d++) {
                    if (n % d == 0) {
                        soma = soma.add(Burnside.colaresPrimitivos(d, k)
                                .multiply(BigInteger.valueOf(d)));
                    }
                }
                assertThat(soma).as("n=%d k=%d", n, k)
                        .isEqualTo(BigInteger.valueOf(k).pow(n));
            }
        }
    }

    @Test
    @DisplayName("entrada sem sentido e recusada na porta")
    void recusaEntradaInvalida() {
        assertThatThrownBy(() -> Burnside.colares(0, 2)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Burnside.colares(3, 0)).isInstanceOf(IllegalArgumentException.class);
    }
}
