# Gerador de certificados

Protótipo CLI em Java para gerar um PDF por participante, com nome, local e data.
O preenchimento do modelo visual em `certificates/Modelo Certificado.pdf` ainda
não está implementado: os PDFs atuais contêm apenas esses três textos.

## Executar

O projeto declara Java 24 no `pom.xml`. Com esse JDK configurado no Maven:

```bash
mvn test
mvn compile exec:java -Dexec.args='"Belo Horizonte" 04/11/2026 names.txt ./entrada ./saida'
```

A ordem dos argumentos é:

```text
<local> <data> <arquivo de nomes> [diretório de nomes] [diretório de saída]
```

Use aspas em argumentos que contenham espaços. `--help` mostra a sintaxe.
A data aceita `dd/MM/aaaa` ou `dd-MM-aaaa`, com validação de calendário e anos
bissextos. O local precisa ser não vazio.

O diretório de nomes padrão é `.out/names`. Um caminho absoluto para o TXT
também pode ser passado como terceiro argumento. A saída padrão é
`.out/certificates/<local>/<data>`, com espaços e separadores sanitizados.
O diretório de saída informado como quinto argumento é usado diretamente.

O TXT deve estar em UTF-8, com um participante por linha. Espaços nas extremidades,
um BOM inicial e linhas vazias são tratados. A pasta de entrada não é criada
automaticamente. Um arquivo sem participantes é rejeitado.

Os PDFs recebem nomes como `0001_Ana_Cláudia.pdf`; participantes repetidos são
preservados com números distintos. Arquivos existentes não são sobrescritos.
Uma falha durante a escrita pode deixar arquivos parciais; nesse caso, revise a
saída e escolha outra pasta para repetir a execução.

Códigos de saída: `0` para sucesso ou ajuda, `1` para falha de leitura/escrita,
`2` para argumentos ou dados inválidos.

## Uso em Java

O construtor valida a configuração sem ler ou gerar arquivos. Os diretórios
pertencem a cada instância e podem ser ajustados antes de chamar `generate()`:

```java
CertificateGenerator generator = new CertificateGenerator("Belo Horizonte", "04/11/2026", "names.txt");
generator.setNamesDirectory("./entrada");
generator.setCertificatesDirectory("./saida");
int total = generator.generate();
```

Os setters, `loadNames` e `generateCertificates` agora são métodos de instância.
Falhas de I/O são propagadas para o chamador.

## Desenvolvimento

JUnit Jupiter e Maven Surefire executam os testes de datas, leitura, argumentos,
isolamento dos diretórios e geração de PDFs, incluindo reabertura e extração do texto.

Para verificar no ambiente com Java 21, sem mudar a versão declarada no projeto:

```bash
mvn -Dmaven.compiler.source=21 -Dmaven.compiler.target=21 test
```

Consulte a [proposta de implementação](docs/implementacao.md) para as próximas
etapas, incluindo modelo visual, fontes e uma CLI com opções nomeadas.
