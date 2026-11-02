package br.com.conde.burnside;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Os numeros do README, presos. Todos sairam do {@link Medidor}. */
class MedidasTest {

    @Test
    @DisplayName("a tabela de duas cores ate dezesseis contas")
    void tabelaDeDuasCores() {
        long[] colares = {2, 3, 4, 6, 8, 14, 20, 36, 60, 108, 188, 352, 632, 1182, 2192, 4116};
        long[] braceletes = {2, 3, 4, 6, 8, 13, 18, 30, 46, 78, 126, 224, 380, 687, 1224, 2250};
        for (int n = 1; n <= 16; n++) {
            assertThat(Burnside.colares(n, 2).longValueExact())
                    .as("colares de %d contas", n).isEqualTo(colares[n - 1]);
            assertThat(Burnside.braceletes(n, 2).longValueExact())
                    .as("braceletes de %d contas", n).isEqualTo(braceletes[n - 1]);
        }
    }

    @Test
    @DisplayName("com 16 contas, 65.536 sequencias viram 4.116 colares e 2.250 braceletes")
    void dezesseisContas() {
        assertThat(Burnside.colares(16, 2).longValueExact()).isEqualTo(4_116);
        assertThat(Burnside.braceletes(16, 2).longValueExact()).isEqualTo(2_250);
        assertThat(Varredura.colaresSimetricos(16, 2)).isEqualTo(384);
        assertThat(1L << 16).isEqualTo(65_536);
    }

    @Test
    @DisplayName("a identidade sozinha responde por 4.096 dos 4.224 pontos fixos")
    void aIdentidadeDomina() {
        List<BigInteger> fixos = Burnside.pontosFixosDasRotacoes(12, 2);
        assertThat(fixos).hasSize(12);

        BigInteger soma = BigInteger.ZERO;
        for (BigInteger x : fixos) {
            soma = soma.add(x);
        }
        assertThat(soma.longValueExact()).isEqualTo(4_224);
        assertThat(fixos.get(0).longValueExact()).isEqualTo(4_096);

        BigInteger semIdentidade = soma.subtract(fixos.get(0));
        assertThat(semIdentidade.longValueExact()).isEqualTo(128);
        assertThat(soma.divide(BigInteger.valueOf(12)).longValueExact()).isEqualTo(352);
    }

    @Test
    @DisplayName("a formula bate com a contagem bruta em 36 pares")
    void juiz() {
        assertThat(Varredura.conferir(Medidor.MAX_CONTAS_JUIZ, Medidor.MAX_CORES_JUIZ))
                .isEqualTo(36);
    }

    @Test
    @DisplayName("os aperiodicos batem com a enumeracao em 36 pares")
    void juizDosAperiodicos() {
        assertThat(Varredura.conferirPrimitivos(Medidor.MAX_CONTAS_JUIZ, 3)).isEqualTo(36);
    }

    @Test
    @DisplayName("a fatia de aperiodicos nao sobe de forma monotona")
    void fatiaOscila() {
        double[] fatias = new double[17];
        for (int n = 1; n <= 16; n++) {
            double todos = Burnside.colares(n, 2).doubleValue();
            double livres = Burnside.colaresPrimitivos(n, 2).doubleValue();
            fatias[n] = 100.0 * livres / todos;
        }
        // sobe de 5 para 7 e CAI nos compostos pelo caminho
        assertThat(fatias[5]).isGreaterThan(fatias[6]);
        assertThat(fatias[7]).isGreaterThan(fatias[8]);
        assertThat(fatias[11]).isGreaterThan(fatias[12]);
        assertThat(fatias[13]).isGreaterThan(fatias[14]);

        assertThat(fatias[5]).isCloseTo(75.0, org.assertj.core.data.Offset.offset(0.01));
        assertThat(fatias[6]).isCloseTo(64.29, org.assertj.core.data.Offset.offset(0.01));
        assertThat(fatias[13]).isCloseTo(99.68, org.assertj.core.data.Offset.offset(0.01));
    }

    @Test
    @DisplayName("em n primo, todo colar menos os monocromaticos e aperiodico")
    void primoQuaseTodoAperiodico() {
        for (int p : new int[] {2, 3, 5, 7, 11, 13}) {
            long todos = Burnside.colares(p, 2).longValueExact();
            long livres = Burnside.colaresPrimitivos(p, 2).longValueExact();
            assertThat(todos - livres).as("p = %d", p).isEqualTo(2);
        }
    }

    @Test
    @DisplayName("a contagem de aperiodicos de 16 contas: 4.080 dos 4.116")
    void aperiodicosDeDezesseis() {
        assertThat(Burnside.colaresPrimitivos(16, 2).longValueExact()).isEqualTo(4_080);
        assertThat(Burnside.colares(16, 2).longValueExact() - 4_080).isEqualTo(36);
    }
}
