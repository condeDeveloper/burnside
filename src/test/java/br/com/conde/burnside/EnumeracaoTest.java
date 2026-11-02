package br.com.conde.burnside;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EnumeracaoTest {

    @Test
    @DisplayName("girar a peca nao muda a forma canonica")
    void rotacaoNaoMudaCanonica() {
        int[] peca = {0, 1, 1, 0, 1};
        long esperado = Enumeracao.canonica(peca, 2, false);
        for (int d = 1; d < peca.length; d++) {
            int[] girada = new int[peca.length];
            for (int i = 0; i < peca.length; i++) {
                girada[i] = peca[(i + d) % peca.length];
            }
            assertThat(Enumeracao.canonica(girada, 2, false)).as("giro %d", d).isEqualTo(esperado);
        }
    }

    @Test
    @DisplayName("sem reflexao, a peca e o avesso dela podem ser canonicas diferentes")
    void avessoSoImportaComReflexao() {
        int[] peca = {0, 0, 1, 0, 1, 1};
        int[] avesso = {1, 1, 0, 1, 0, 0};
        assertThat(Enumeracao.canonica(peca, 2, false))
                .isNotEqualTo(Enumeracao.canonica(avesso, 2, false));
        assertThat(Enumeracao.canonica(peca, 2, true))
                .isEqualTo(Enumeracao.canonica(avesso, 2, true));
    }

    @Test
    @DisplayName("o periodo e o menor divisor que a peca repete")
    void periodo() {
        assertThat(Enumeracao.periodo(new int[] {0, 1, 0, 1})).isEqualTo(2);
        assertThat(Enumeracao.periodo(new int[] {0, 0, 0, 0})).isEqualTo(1);
        assertThat(Enumeracao.periodo(new int[] {0, 1, 1, 0})).isEqualTo(4);
        assertThat(Enumeracao.periodo(new int[] {0, 1, 2, 0, 1, 2})).isEqualTo(3);
    }

    @Test
    @DisplayName("a contagem bruta bate com o que se conta no dedo")
    void contagemBruta() {
        assertThat(Enumeracao.colares(3, 2)).isEqualTo(4);
        assertThat(Enumeracao.colares(4, 2)).isEqualTo(6);
        assertThat(Enumeracao.colares(6, 2)).isEqualTo(14);
        assertThat(Enumeracao.braceletes(6, 2)).isEqualTo(13);
    }

    @Test
    @DisplayName("o teto existe e e respeitado, em vez de travar")
    void teto() {
        assertThat(Enumeracao.quantasSequencias(10, 2)).isEqualTo(1024);
        assertThat(Enumeracao.quantasSequencias(40, 5)).isEqualTo(Long.MAX_VALUE);
        assertThatThrownBy(() -> Enumeracao.colares(40, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("demais");
    }

    @Test
    @DisplayName("toda sequencia tem periodo que divide o comprimento")
    void periodoDivide() {
        for (int n = 1; n <= 10; n++) {
            long total = Enumeracao.quantasSequencias(n, 2);
            int[] peca = new int[n];
            for (long codigo = 0; codigo < total; codigo++) {
                long resto = codigo;
                for (int i = 0; i < n; i++) {
                    peca[i] = (int) (resto % 2);
                    resto /= 2;
                }
                assertThat(n % Enumeracao.periodo(peca)).as("n=%d codigo=%d", n, codigo).isZero();
            }
        }
    }
}
