# AURUM

API para gestão financeira familiar, desenvolvida com Java e Spring Boot.

## Tecnologias

- Java 21
- Spring Boot
- Maven Wrapper
- PostgreSQL 17
- Docker Compose
- Flyway para versionamento do esquema do banco de dados

## Pré-requisitos

Antes de executar o projeto, instale e configure:

1. **JDK 21** — confirme com `java -version`.
2. **Docker Desktop** — no Windows, abra o Docker Desktop e aguarde o engine ficar ativo.
3. **Git** — para clonar o repositório.
4. **Terminal** — PowerShell, Windows Terminal ou equivalente.

O Maven Wrapper (`mvnw` / `mvnw.cmd`) é incluído no projeto, então não é necessário instalar o Maven separadamente.

## Configuração local

A configuração de desenvolvimento usa PostgreSQL local via Docker Compose. Confira se o arquivo `compose.yaml` (ou `docker-compose.yml`, conforme o nome presente no projeto) contém as configurações esperadas pela aplicação:

- Banco: `aurum`
- Usuário: `aurum`
- Senha de desenvolvimento: `aurum_dev_password`
- Porta: `5432`

Essas credenciais são apenas para desenvolvimento local. Não reutilize essa senha em ambientes compartilhados ou de produção e não versione credenciais reais.

A configuração da aplicação em `src/main/resources/application.yml` deve apontar para:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/aurum
    username: aurum
    password: aurum_dev_password
  jpa:
    hibernate:
      ddl-auto: none
  flyway:
    enabled: true
```

Mantenha a configuração alinhada ao arquivo Compose existente. Se os nomes, usuário ou senha forem diferentes no seu ambiente, ajuste os valores correspondentes nos dois arquivos.

## Como executar

Os comandos abaixo consideram o terminal **PowerShell no Windows**, executados a partir da raiz do repositório.

### 1. Iniciar o banco de dados

```powershell
docker compose up -d
```

Verifique se o contêiner está em execução:

```powershell
docker compose ps
```

Se o banco ainda estiver inicializando, aguarde alguns segundos e consulte os logs:

```powershell
docker compose logs -f postgres
```

Pressione `Ctrl+C` para sair do acompanhamento dos logs; isso não encerra o contêiner.

### 2. Iniciar a API

No PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Em Linux/macOS, use:

```bash
./mvnw spring-boot:run
```

Na inicialização, o Flyway deve aplicar as migrações pendentes automaticamente. Mantenha o terminal aberto enquanto estiver usando a API.

### 3. Verificar a aplicação

Com a API em execução, acesse:

- Health check: http://localhost:8080/api/v1/health

Resposta esperada:

```json
{
  "status": "UP"
}
```

Também é possível testar no PowerShell:

```powershell
Invoke-RestMethod http://localhost:8080/api/v1/health
```

### 4. Encerrar os serviços

Para parar a API, pressione `Ctrl+C` no terminal em que ela está executando.

Para parar os contêineres do Compose:

```powershell
docker compose down
```

Esse comando preserva os dados do volume do PostgreSQL. **Não use `docker compose down -v` a menos que queira apagar o volume e os dados locais do banco.**

## Banco de dados e migrações

As migrações SQL do Flyway ficam em:

```text
src/main/resources/db/migration/
```

Os arquivos seguem a convenção de nomes do Flyway, por exemplo `V1__create_categories_table.sql`. Crie uma nova migração para cada alteração futura no esquema; evite editar migrações que já foram aplicadas em um banco compartilhado.

## Comandos úteis

| Ação | Comando |
| --- | --- |
| Iniciar PostgreSQL | `docker compose up -d` |
| Ver status dos contêineres | `docker compose ps` |
| Acompanhar logs do PostgreSQL | `docker compose logs -f postgres` |
| Parar contêineres sem apagar os dados | `docker compose down` |
| Executar testes e validações Maven | `./mvnw.cmd verify` (PowerShell) |
| Iniciar a API | `./mvnw.cmd spring-boot:run` (PowerShell) |

## Solução de problemas

- **Docker não está disponível:** abra o Docker Desktop e aguarde o engine iniciar.
- **Porta 5432 em uso:** verifique se outro PostgreSQL está rodando e ajuste a porta publicada no Compose e a URL JDBC, se necessário.
- **Erro de conexão com o banco:** confira se o contêiner está saudável e se banco, usuário, senha e porta coincidem entre Compose e `application.yml`.
- **Erro do Flyway:** verifique os logs da aplicação e os arquivos SQL em `src/main/resources/db/migration/`.
- **Erro de sintaxe no YAML:** use espaços para indentação; não use tabulações.

## Status do projeto

O projeto está em desenvolvimento. O endpoint de health check e a infraestrutura inicial são a base para a implementação gradual dos recursos de gestão financeira.
