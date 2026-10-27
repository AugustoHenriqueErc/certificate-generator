# Gerador de certificados

Ferramenta CLI em Java para gerar certificados em PDF dos cursos de primeiros
socorros lecionados pelo Vailton. A proposta é informar a data e o local da turma,
ler um TXT com os nomes dos participantes e produzir um PDF por aluno.

O projeto está no início da implementação. A CLI e o preenchimento do modelo PDF
ainda não estão implementados. O código contém valores fixos no `Main` e precisa
de correções na criação dos arquivos PDF.

Consulte a [proposta de implementação](docs/implementacao.md) para conhecer o
escopo, o contrato sugerido da CLI, a arquitetura, as regras de geração e as
decisões que ainda precisamos discutir.

## Tecnologias atuais

- Java 24, conforme o `pom.xml`.
- Maven, com `exec-maven-plugin` 3.2.0.
- iText 9.2.0 para manipulação dos PDFs.
- Guava 33.3.1-jre e JUnit Jupiter API 5.13.3.

## Executar o código atual

Com o JDK 24 configurado no Maven:

```bash
mvn compile
mvn exec:java
```

Esses comandos executam o protótipo, que utiliza local, data e nome de arquivo
fixos. Eles ainda não representam o fluxo de geração planejado.

O ambiente inspecionado durante a documentação possui Java 21 e Maven 3.8.7.
Para compilar com o POM atual, é necessário configurar o JDK 24. Uma eventual
mudança da versão Java será uma decisão separada de implementação.
