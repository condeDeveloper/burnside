# burnside

Quantos colares diferentes dá para montar com 16 contas de duas cores?

A resposta óbvia é `2^16 = 65.536`. A resposta certa é **4.116**, porque um colar girado
continua o mesmo colar. E se ele puder ser virado do avesso — um bracelete —, são
**2.250**.

Contar isso na marra significa gerar as 65.536 sequências, reduzir cada uma a uma forma
canônica e ver quantas sobram. O **lema de Burnside** dá o mesmo número sem gerar nada:
basta tirar a **média de pontos fixos** das simetrias.

Este repositório faz as duas coisas e compara.

```bash
mvn -B -ntp verify                              # 21 testes
mvn -B -ntp exec:java -Dexec.args="colares"     # colares e braceletes até 16 contas
mvn -B -ntp exec:java -Dexec.args="media"       # de onde a média sai
mvn -B -ntp exec:java -Dexec.args="juiz"        # fórmula x contagem bruta
mvn -B -ntp exec:java -Dexec.args="primitivos"  # os que não se repetem
mvn -B -ntp exec:java -Dexec.args="fundo"       # até 5 cores (job próprio)
```

Java 21, sem dependência fora de JUnit e AssertJ.

## O juiz não conhece o lema

A `Enumeracao` constrói todas as `k^n` sequências, gira cada uma de todas as maneiras,
guarda o menor código que encontrar e conta quantos códigos distintos sobraram. Não sabe o
que é grupo, órbita, totiente ou ponto fixo. Só sabe girar e comparar.

```
pares (contas, cores) conferidos: 36 no CI, 69 no job de fundo — todos iguais
o maior caso enumerado: 12 contas em 4 cores = 16.777.216 sequências
```

```
contas  cores   colar fórmula   colar bruta   bracelete fórmula   bracelete bruta
6       2       14              14            13                  13
8       2       36              36            30                  30
5       3       51              51            39                  39
6       3       130             130           92                  92
4       4       70              70            55                  55
10      2       108             108           78                  78
```

## Colares e braceletes

```
contas  sequências  colares  braceletes  simétricos
1       2           2        2           2
2       4           3        3           3
4       16          6        6           6
6       64          14       13          12
8       256         36       30          24
10      1024        108      78          48
12      4096        352      224         96
14      16384       1182     687         192
16      65536       4116     2250        384
```

Até cinco contas, colares e braceletes empatam: toda peça pequena é igual ao próprio
avesso. A partir de seis eles se separam, e a diferença cresce depressa — com 16 contas,
metade dos colares some ao permitir virar a peça.

A coluna "simétricos" conta os colares que são iguais ao próprio avesso. Eles são
exatamente os que não ganham par quando a reflexão entra, e saem de
`2 × braceletes − colares`.

## De onde a média sai

Com 12 contas e 2 cores, os pontos fixos de cada rotação:

```
rotação   mdc(d,12)   parados
0         12          4096
1         1           2
2         2           4
3         3           8
4         4           16
5         1           2
6         6           64
7         1           2
8         4           16
9         3           8
10        2           4
11        1           2
                      ----
soma                  4224      ÷ 12 = 352 colares
```

A rotação `0` é a identidade: ela não mexe em peça nenhuma, então fixa todas as 4.096.
**Ela sozinha responde por 4.096 dos 4.224 pontos fixos** — as outras onze rotações
juntas somam 128.

É por isso que o número de colares fica tão perto de `2^n / n`: o lema é quase só a
identidade dividida pelo tamanho do grupo, e todo o resto é correção de segunda ordem.

## Os que não se repetem

Um colar é **aperiódico** quando não se repete antes de dar a volta inteira. `ABABAB` não
é; `AABABB` é.

```
contas   colares   aperiódicos   fatia
5        8         6             75,00%
6        14        9             64,29%
7        20        18            90,00%
8        36        30            83,33%
11       188       186           98,94%
12       352       335           95,17%
13       632       630           99,68%
14       1182      1161          98,22%
16       4116      4080          99,13%
```

A fatia **não sobe de forma monótona**. Ela cai de 75% para 64,29% entre 5 e 6, de 90%
para 83,33% entre 7 e 8, de 98,94% para 95,17% entre 11 e 12. O padrão é claro quando se
olha a fatoração: **n primo não tem divisor próprio, então o único jeito de um colar se
repetir é ser monocromático** — sempre exatamente 2 colares, em qualquer primo. Já um n
composto tem vários períodos possíveis, e a fatia desaba.

## O que as medidas me corrigiram

**Chutei um número dentro do arquivo que existe para impedir chute.** O job de fundo
confere a própria medida antes de terminar, e eu escrevi `52` como o número esperado de
pares conferidos sem ter rodado. São **69**. A conferência pegou na primeira execução e
quebrou o job — funcionou exatamente como devia, contra o próprio autor.

**Esperei que a fatia de aperiódicos subisse sempre.** Ela oscila, e oscila com estrutura:
sobe nos primos e cai nos compostos. Eu ia escrever "quanto mais contas, mais raro um
colar se repetir", que é verdade na tendência e falso em metade dos passos.

**Canonicalizar por texto custou quase cinco minutos.** A primeira versão montava um
`Arrays.toString` para cada rotação de cada peça e comparava strings. Com 16 milhões de
sequências no caso maior, isso é uma alocação por rotação. Trocar o texto pelo **próprio
código da peça lida como número na base k** derrubou a conferência de **4m48 para 55s**,
sem mudar uma classe sequer — e depois para 2,6s ao deixar o caso de 4 cores só no job de
fundo.

**As reflexões não são todas iguais.** Com `n` ímpar, toda reflexão passa por uma conta e
pelo meio do lado oposto, e tem `(n+1)/2` órbitas. Com `n` par, metade delas passa por
**duas contas** e metade passa por **dois lados**, com contagens diferentes. Usar uma
fórmula só para os dois casos dá braceletes a menos nos pares — e a contagem bruta
acusaria na hora, que é para isso que ela está ali.

## Quatro achados

1. **A identidade carrega o lema sozinha:** com 12 contas e 2 cores, ela responde por
   **4.096 dos 4.224** pontos fixos, e as outras onze rotações somam 128.
2. A fórmula bateu com a contagem bruta em **69 pares** de (contas, cores), incluindo um
   caso de **16.777.216 sequências** enumeradas uma a uma.
3. **A fatia de colares aperiódicos oscila**, não cresce: cai de 75% para 64,29% ao passar
   de 5 para 6 contas, e de 98,94% para 95,17% ao passar de 11 para 12. Em `n` primo os
   únicos colares periódicos são os **dois** monocromáticos, sempre.
4. Com 16 contas e duas cores, **65.536 sequências viram 4.116 colares e 2.250
   braceletes**: permitir virar a peça do avesso corta quase metade, e só **384** colares
   sobrevivem por serem iguais ao próprio reverso.
