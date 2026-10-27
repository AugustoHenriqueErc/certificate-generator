# Proposta de implementação da CLI de certificados

Status: rascunho para discussão. Este documento descreve o comportamento proposto;
as funcionalidades abaixo ainda precisam ser implementadas. As dependências e
limitações atuais foram verificadas no repositório.

## 1. Contexto e objetivo

Vailton é ex-socorrista do SAMU, atua por conta própria na cobertura de eventos
com ambulância e socorrista e também leciona cursos gratuitos de primeiros
socorros. Atualmente, a emissão dos certificados exige editar no Canva a data,
o local e o nome de cada aluno e baixar os PDFs individualmente.

A ferramenta deve automatizar essa emissão por turma: receber os dados comuns
do curso, ler os nomes em um TXT e gerar certificados individuais em uma pasta.
O processamento será local, sem necessidade de conta ou serviço externo.

## 2. Escopo da primeira versão

- Receber data, local, caminho do TXT, modelo PDF e diretório de saída pela CLI.
- Validar os argumentos e as entradas antes de iniciar a geração.
- Ler um nome por linha em UTF-8, preservando acentos e a grafia informada.
- Gerar um PDF independente por aluno com nome, data e local preenchidos.
- Preservar o desenho e os elementos fixos do certificado.
- Informar arquivos gerados, linhas ignoradas e eventuais falhas.

Interface gráfica, envio por e-mail, banco de dados, QR Code, assinatura digital
criptográfica e PDF/A ficam para versões futuras, caso sejam necessários.
Uma imagem de assinatura já presente no modelo permanece como elemento visual.

## 3. Estado atual do código

| Componente | Situação observada | Mudança planejada |
| --- | --- | --- |
| `Main` | Instancia o gerador com Belo Horizonte, `01/10/2025` e `names`; não usa os argumentos | Interpretar a CLI e apresentar o resultado |
| `CertificateGenerator` | Executa leitura e geração no construtor | Executar o trabalho em um método explícito |
| Caminhos | Usa `.out/names` e saída derivada de local/data em campos estáticos mutáveis | Receber caminhos como `Path` por execução |
| Leitura | Usa `FileReader`, sem charset explícito, e inclui linhas vazias | Usar UTF-8 e registrar a linha de origem |
| PDF | Passa um diretório ao `PdfWriter` e compartilha o escritor entre alunos | Abrir modelo e escritor próprios para cada arquivo |
| Conteúdo | Adiciona somente um parágrafo com o nome; não usa modelo | Preencher as áreas variáveis do certificado |
| `Validator` | Classe vazia | Concentrar validações de entrada |
| `Sanitizer` | Apenas troca espaços e barras | Tratar componentes de caminhos sem alterar o texto impresso |
| Testes | Quatro testes de sanitização | Cobrir leitura, validação e geração real de PDFs |

Os testes atuais de data recebem underscores, portanto ainda não demonstram
que uma data com barras é transformada corretamente. O tratamento atual de
exceções também não fornece ao chamador um resultado confiável da execução.

## 4. Bibliotecas e ferramentas do `pom.xml`

| Dependência atual | Papel na implementação |
| --- | --- |
| iText `kernel` 9.2.0 | Abrir o modelo, criar os PDFs de saída e acessar páginas, fontes e canvas |
| iText `io` 9.2.0 | Suporte à leitura de fontes e recursos usados pelo iText |
| iText `layout` 9.2.0 | Posicionar e alinhar textos nas áreas reservadas |
| iText `forms` 9.2.0 | Alternativa futura se o modelo tiver campos AcroForm |
| iText `pdfa` 9.2.0 | Disponível; sem uso previsto no escopo inicial |
| iText `sign` 9.2.0 | Disponível; sem assinatura digital prevista inicialmente |
| iText `barcodes` 9.2.0 | Disponível; sem códigos de barras ou QR Code no escopo inicial |
| iText `font-asian` e `hyph` 9.2.0 | Sem uso previsto para o modelo inicial em português |
| Guava 33.3.1-jre | Disponível; usar somente onde simplificar uma necessidade concreta |
| `slf4j-simple` 2.0.7, escopo `test` | Backend de logs disponível apenas nos testes |
| `junit-jupiter-api` 5.13.3, escopo `test` | API para escrever os testes |
| `exec-maven-plugin` 3.2.0 | Executar a classe `Main` via Maven |

Leitura de arquivos, datas, caminhos e argumentos simples podem usar a biblioteca
padrão do Java (`Files`, `Path`, `LocalDate` e `String[] args`). Não há biblioteca
de CLI no POM; a proposta inicial é um parser pequeno de opções nomeadas.

O POM declara a API do JUnit, mas não declara explicitamente o Jupiter Engine nem
uma versão do Surefire. Na etapa de testes, configurar ambos para executar JUnit
5 de forma reproduzível e conferir a quantidade de testes executados. Ver a
[documentação do Maven Surefire sobre JUnit](https://maven.apache.org/components/surefire/maven-surefire-plugin/examples/junit-platform.html).

O empacotamento atual não configura um JAR executável com dependências.
Primeiro executar via Maven; distribuir com `java -jar` exige uma etapa posterior
de configuração do build.

## 5. Modelo do certificado

Proposta: exportar do Canva um PDF de uma página com as áreas de nome, data e
local vazias. Manter nesse PDF os textos fixos, logotipos, bordas e a assinatura
visual. Essa abordagem também corresponde à intenção registrada nas anotações
em `diagrams/certificategenerator_notes.xopp`.

Para cada aluno, abrir o modelo com `PdfReader` e criar uma saída distinta com
`PdfWriter`, associados a um novo `PdfDocument`. O iText oferece esse modo de
edição de PDF existente na [API 9.2.0 de PdfDocument](https://api.itextpdf.com/iText/java/9.2.0/com/itextpdf/kernel/pdf/PdfDocument.html).

Adicionar os textos sobre o conteúdo da página usando `PdfCanvas` e os recursos
de alinhamento de `layout`. A [API de PdfCanvas](https://api.itextpdf.com/iText/java/9.2.0/com/itextpdf/kernel/pdf/canvas/PdfCanvas.html)
descreve a escrita em páginas existentes. Exportar do Canva não deve ser tratado
como garantia de que o PDF conterá campos de formulário editáveis.

O layout inicial terá um perfil definido no código, contendo página, dimensões
esperadas, caixas de texto, fonte, tamanho, cor e alinhamento. Os valores reais
dependem do PDF fornecido; não fixar coordenadas antes de inspecioná-lo. Medir
em pontos PDF e considerar a origem das coordenadas, a área visível e a rotação
da página. Rejeitar modelos incompatíveis com esse perfil.

Usar fonte TTF/OTF com cobertura dos caracteres dos nomes e permissão de
incorporação. Configurar a fonte via `PdfFontFactory`, com encoding adequado e
incorporação, conforme a [API 9.2.0 de fontes](https://api.itextpdf.com/iText/java/9.2.0/com/itextpdf/kernel/font/PdfFontFactory.html).
Não assumir que a fonte usada pelo Canva estará disponível para escrever novos
textos. O arquivo da fonte será um recurso do perfil do modelo.

Para nomes longos, medir a largura e reduzir a fonte até o mínimo definido no
perfil. Se ainda não couber, informar a linha e o nome para revisão, sem cortar
o texto ou produzir um certificado com conteúdo sobreposto. Aplicar limites
equivalentes ao local e à data. A inspeção visual do modelo real é um critério
de aceitação da implementação.

## 6. Contrato proposto da CLI

Exemplo para implementação futura; este comando ainda não funciona no protótipo:

```bash
mvn compile exec:java -Dexec.args='--data 22/10/2026 --local "Belo Horizonte" --nomes ./entrada/alunos.txt --modelo ./modelos/primeiros-socorros.pdf --saida ./certificados'
```

| Opção | Regra proposta |
| --- | --- |
| `--data` | Obrigatória; data válida no formato `dd/MM/uuuu` |
| `--local` | Obrigatória; texto não vazio; aspas quando houver espaços |
| `--nomes` | Obrigatória; caminho de arquivo TXT legível |
| `--modelo` | Obrigatória; caminho de PDF legível e compatível com o perfil |
| `--saida` | Opcional; pasta-base, com padrão `./certificados` |
| `--help` | Mostrar opções e exemplo, sem exigir os demais argumentos |

Rejeitar opções desconhecidas, repetidas ou sem valor com uma mensagem objetiva.
Resolver caminhos relativos a partir do diretório em que o comando é executado.
Usar `DateTimeFormatter` com `ResolverStyle.STRICT` para rejeitar datas como
`31/02/2026`. Manter a data como `LocalDate` internamente.

Proposta de apresentação: imprimir a data como `22/10/2026`; confirmar se o
modelo exige uma data por extenso antes de implementar o preenchimento.

## 7. Leitura e regras dos nomes

Exemplo de `alunos.txt`:

```text
Ana Cláudia de Souza
João Pedro Santos
Maria da Conceição
```

Ler explicitamente em UTF-8, aceitando LF e CRLF. Remover um eventual BOM do
início do arquivo e espaços nas extremidades de cada linha. Ignorar linhas
vazias e informar sua quantidade. Não modificar acentos, caixa, apóstrofos ou
hífens dos nomes, nem interpretar linhas como comentários.

Guardar o número original da linha junto ao nome para facilitar a correção de
problemas. Rejeitar arquivo sem nomes válidos e caracteres de controle que não
possam ser impressos no campo. Reportar problemas de decodificação UTF-8.

Proposta para nomes repetidos: manter cada ocorrência e avisar sobre repetições
exatas após a limpeza das extremidades. Homônimos podem ser alunos distintos;
não remover pessoas automaticamente por comparação de nomes.

## 8. Saída e tratamento de erros

Estrutura proposta para o exemplo:

```text
certificados/
  Belo_Horizonte/
    2026-10-22/
      0001_Ana_Cláudia_de_Souza.pdf
      0002_João_Pedro_Santos.pdf
      0003_Maria_da_Conceição.pdf
```

A numeração corresponde à ordem dos nomes aceitos e evita colisões entre
homônimos e entre nomes que resultem no mesmo texto sanitizado. Criar as pastas
com `Files.createDirectories` e verificar os erros dessa operação.

Separar a grafia no certificado da versão usada no nome de arquivo. Sanitizar
separadores, controles e caracteres incompatíveis com arquivos; limitar o tamanho
dos componentes e impedir que nomes ou local escapem da pasta de destino.
Evitar componentes reservados e pontos/espaços finais ao considerar Windows.

Proposta inicial: não sobrescrever arquivos existentes. Antes da geração,
calcular todos os destinos e rejeitar conflitos com orientação para escolher
outra pasta de saída. Garantir também a criação exclusiva na gravação, pois a
checagem prévia sozinha não impede colisões com outro processo.

Validar argumentos, nomes, fonte, modelo, encaixe dos textos e destinos antes de
gerar. Não criar a pasta de entrada automaticamente: um TXT ausente deve ser
informado como erro. A saída nunca poderá substituir o modelo ou o TXT.

Cada certificado terá recursos de PDF próprios, fechados com `try-with-resources`.
Gravar primeiro em arquivo temporário na pasta de saída e publicar o nome final
somente após fechar o PDF, sem substituir outro arquivo. Remover o temporário
em caso de falha.

Se ocorrer falha durante a geração, parar o lote, preservar os PDFs concluídos e
informar quais foram gerados e em qual aluno ocorreu a falha. A primeira versão
não promete transação de todo o lote nem retomada automática; após corrigir o
problema, usar uma pasta de saída nova.

Saída de sucesso sugerida: `3 certificados gerados. 1 linha vazia ignorada.`
Mensagens de falha devem explicar a ação necessária, sem stack trace por padrão.

| Código de saída proposto | Significado |
| --- | --- |
| `0` | Lote concluído ou ajuda exibida |
| `1` | Falha de leitura, escrita ou geração; pode haver saída parcial |
| `2` | Argumentos ou dados inválidos; nenhum certificado gerado |

Implementar um método `run(args)` que retorne o código; o ponto de entrada pode
encerrar o processo com esse valor. Na execução com `exec:java`, verificar como
o plugin propaga a saída; o contrato de códigos deve ser validado também com o
launcher Java quando o empacotamento estiver configurado.

## 9. Organização proposta

Manter classes pequenas, sem framework de injeção de dependências:

| Classe/pacote sugerido | Responsabilidade |
| --- | --- |
| `app.Main` | Conectar dependências, executar a CLI e devolver o código de saída |
| `cli.CliParser` / `cli.CliOptions` | Interpretar opções e representar a configuração recebida |
| `model.CourseInfo` | Representar local e `LocalDate` da turma |
| `model.Participant` | Representar nome e linha do TXT |
| `io.NamesReader` | Ler e validar a estrutura do TXT em UTF-8 |
| `validation.Validator` | Validar dados e pré-condições do lote |
| `io.OutputPathResolver` | Gerar caminhos sanitizados e identificar conflitos |
| `pdf.CertificateLayout` | Guardar as medidas e a fonte do modelo inicial |
| `pdf.PdfCertificateRenderer` | Gerar um certificado a partir do modelo e dos dados |
| `service.CertificateGenerator` | Coordenar validação, leitura e geração do lote |

Os nomes e pacotes são uma proposta de evolução das classes existentes.
Construtores apenas recebem configuração e dependências; não leem arquivos nem
geram PDFs. Remover caminhos estáticos mutáveis. Separar erros de entrada de
falhas de I/O para que a CLI apresente mensagens e códigos coerentes.

Fluxo: argumentos → validação → leitura do TXT → planejamento dos destinos e
textos → geração sequencial → resumo. Não há necessidade de paralelismo para
a primeira versão.

## 10. Etapas e critérios de aceitação

1. **Definir o modelo e fechar o escopo.** Confirmar campos variáveis, obter o
   PDF com áreas vazias, escolher fonte e medir o perfil. Aceite: um certificado
   de amostra aprovado visualmente com acentos e um nome longo.
2. **Separar leitura e validação.** Retirar o trabalho do construtor, usar `Path`
   e UTF-8 e implementar as regras do TXT e de data. Aceite: entradas inválidas
   produzem mensagens claras antes da geração.
3. **Gerar um certificado corretamente.** Usar um arquivo de saída e recursos
   próprios, aplicar o perfil e preservar o modelo. Aceite: PDF de uma página,
   legível, com nome, data e local corretos e modelo de origem intacto.
4. **Gerar o lote.** Planejar caminhos, tratar repetições e conflitos, publicar
   arquivos completos e reportar falhas. Aceite: três nomes válidos produzem três
   PDFs distintos; nova execução no mesmo destino não sobrescreve arquivos.
5. **Conectar a CLI.** Implementar opções, ajuda e retorno de erros. Aceite: o
   comando documentado funciona com caminhos e locais contendo espaços.
6. **Consolidar testes e instruções.** Configurar a execução real do JUnit e
   atualizar o README com o comportamento entregue. Aceite: `mvn test` executa
   os testes previstos com JDK compatível, e um lote é conferido visualmente.

Testes de unidade devem cobrir datas impossíveis, argumentos incompletos,
sanitização e nomes que colidam. Testes de leitura devem usar arquivos temporários
com acentos, BOM, linhas vazias, CRLF e repetições. Testes de integração devem
gerar PDFs em diretório temporário, reabri-los com iText e verificar páginas e
texto extraído, além de conferir ausência de sobrescrita e remoção de temporários
em falhas controladas. A extração de texto não substitui a revisão visual do layout.

## 11. Decisões para discussão

| Tema | Proposta inicial | Situação |
| --- | --- | --- |
| Modelo visual | PDF exportado do Canva com três áreas vazias | A confirmar com o usuário |
| Dados variáveis | Nome, data e local; demais informações fixas no modelo | Confirmar carga horária, título do curso e instrutor |
| Data impressa | Formato numérico `dd/MM/uuuu` | Confirmar no certificado atual |
| TXT | UTF-8, um aluno por linha | Proposta |
| Repetições | Preservar ocorrências e avisar | Proposta |
| Saída existente | Rejeitar conflitos, sem sobrescrever | Proposta |
| Falha no lote | Parar e preservar os certificados concluídos | Proposta |
| Java | Manter Java 24 declarado no POM | Ambiente local observado usa Java 21; alinhar antes de compilar |
| Distribuição | Executar via Maven inicialmente | JAR executável fica para etapa posterior |

A iText disponibiliza licenciamento AGPL e comercial, conforme sua
[documentação oficial de licenciamento](https://itextpdf.com/how-buy/AGPLv3-license).
Registrar a modalidade escolhida e adequar a licença do projeto antes de sua
distribuição. Nenhuma modalidade é escolhida por este documento.
