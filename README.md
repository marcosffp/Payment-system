# Payment System

O Payment System é um projeto para gestão de pagamentos via Pix, integrando-se à API EfPlay. Oferece criação de cobranças, QR Codes e transferências, além de autenticação com JWT e gerenciamento de usuários. Desenvolvido com Spring Boot, Spring Security e MySQL, garante segurança, eficiência e escalabilidade.

---

## Funcionalidades

### Autenticação e Usuários

* Registro de usuários com verificação por email (envio de código de ativação)
* Autenticação JWT para proteção dos endpoints
* Endpoints protegidos por autorização, com filtro de segurança JWT customizado
* Criptografia segura de senhas usando BCrypt

### Operações Pix

* Criação de chaves Pix (EVP) via API EfPlay
* Geração e gestão de cobranças Pix
* Envio de pagamentos Pix

### Configuração CORS

* Permite requisições de qualquer origem (configuração liberada para desenvolvimento)
* Métodos HTTP permitidos: GET, POST, PUT, DELETE, OPTIONS

---

## Como Rodar o Projeto

### Pré-requisitos

* Java JDK 17+
* Maven
* MySQL
* Docker (opcional)

### Configuração

1. Clone o repositório:

```bash
git clone https://github.com/seu-usuario/payment-system.git
cd payment-system
```

2. Configure o banco de dados:

* O sistema usa um banco MySQL chamado `payments` (criado automaticamente, se não existir)
* Credenciais padrão (podem ser alteradas no arquivo `application.properties`):

  * Usuário: `root`
  * Senha: `root`

3. Configure as variáveis de ambiente:

* `MAIL_USER`: Email para envio de confirmações
* `MAIL_PASSWORD`: Senha do email
* `JWT_SECRET`: Chave secreta para geração e validação dos tokens JWT

4. Build e execução:

```bash
mvn clean install
mvn spring-boot:run
```

---

## Configuração de Variáveis de Ambiente via `.env`

O projeto utiliza a biblioteca [Dotenv](https://github.com/cdimascio/java-dotenv) para carregar variáveis de ambiente a partir de um arquivo `.env` na raiz do projeto, facilitando a configuração local e em ambientes que não usam variáveis de ambiente diretamente.

No método `main` da aplicação, as variáveis são carregadas e configuradas no sistema Java:

```java
public static void main(String[] args) {
    // Carregar variáveis do .env
    Dotenv dotenv = Dotenv.load();
    System.setProperty("DB_PORT", dotenv.get("DB_PORT"));
    System.setProperty("DB_USERNAME", dotenv.get("DB_USER"));
    System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
    System.setProperty("MAIL_USER", dotenv.get("MAIL_USER"));
    System.setProperty("MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD"));
    System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET")); 
    SpringApplication.run(PaymentSystemApplication.class, args);
}
```

### Variáveis esperadas no arquivo `.env`

Você deve criar um arquivo `.env` na raiz do projeto contendo, por exemplo:

```
DB_PORT=3306
DB_USER=root
DB_PASSWORD=root
MAIL_USER=seu-email@gmail.com
MAIL_PASSWORD=sua-senha-email
JWT_SECRET=sua-chave-secreta-jwt
```

---

### Como funciona essa configuração

* No início da aplicação, o arquivo `.env` é lido e suas variáveis são injetadas como propriedades do sistema (`System.setProperty`).
* O Spring Boot lê essas propriedades para configurar banco de dados, email, JWT, etc.
* Essa abordagem facilita desenvolvimento local e evita hardcoding de segredos no código-fonte.

---

### Atenção

* Nunca comite seu arquivo `.env` com senhas ou segredos reais em repositórios públicos.
* Em produção, é recomendado configurar variáveis de ambiente diretamente no servidor ou usar serviços de gerenciamento de segredos.

---

## Arquitetura e Componentes Principais

### Beans e Injeção de Dependência

* Objetos gerenciados pelo Spring (Beans) são automaticamente criados e injetados onde necessários
* Exemplo: `@Bean` para `PasswordEncoder` (BCrypt), `SecurityFilterChain` para configurar Spring Security, `CorsConfig` para liberar CORS

### Segurança (Spring Security)

* Configuração sem sessão (stateless) para APIs REST com JWT
* Endpoints públicos: registro, verificação e login
* Filtro customizado `SecurityFilter` intercepta requisições para validar token JWT e autenticar usuário
* Senhas armazenadas com hash BCrypt

### Serviço de Usuário (`UserService`)

* Registra usuários com senha criptografada e código de verificação gerado via `RandomString`
* Envia email de confirmação com link para ativação da conta
* Valida código e ativa usuário

### Serviço de Token (`TokenService`)

* Gera tokens JWT assinados com segredo configurado (`jwt.secret`)
* Valida tokens e recupera dados do usuário (email)

### Serviço de Email (`MailService`)

* Envia email de verificação usando SMTP configurado com variáveis de ambiente

### Serviço Pix (`PixService`)

* Comunicação com API EfPlay para criar chaves EVP, gerar cobranças e enviar pagamentos Pix
* Controlado via endpoints REST no `PixController`

---

## Endpoints da API

### Autenticação

| Método | Endpoint              | Descrição                      | Autenticação |
| ------ | --------------------- | ------------------------------ | ------------ |
| POST   | /api/v1/auth/login    | Login do usuário (retorna JWT) | Não          |
| POST   | /api/v1/user/register | Registro de novo usuário       | Não          |
| GET    | /api/v1/user/verify   | Verificação do email           | Não          |

### Usuários

| Método | Endpoint           | Descrição                     | Autenticação |
| ------ | ------------------ | ----------------------------- | ------------ |
| GET    | /api/v1/user/teste | Endpoint protegido para teste | Sim          |

### Operações Pix

| Método | Endpoint    | Descrição               | Autenticação |
| ------ | ----------- | ----------------------- | ------------ |
| GET    | /pix/criar  | Cria nova chave Pix EVP | Sim          |
| POST   | /pix/cobrar | Gera cobrança Pix       | Sim          |
| POST   | /pix/enviar | Envia pagamento Pix     | Sim          |

---

## Exemplos de Uso

1. Registrar um novo usuário:

```bash
curl -X POST http://localhost:8080/api/v1/user/register \
-H "Content-Type: application/json" \
-d '{
    "email": "usuario@exemplo.com",
    "password": "senha123",
    "name": "Nome do Usuário"
}'
```

2. Fazer login e obter token JWT:

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
-H "Content-Type: application/json" \
-d '{
    "email": "usuario@exemplo.com",
    "password": "senha123"
}'
```

3. Criar uma cobrança Pix (usando token JWT do login):

```bash
curl -X POST http://localhost:8080/pix/cobrar \
-H "Content-Type: application/json" \
-H "Authorization: Bearer <SEU_TOKEN_JWT>" \
-d '{
    "valor": "100.00",
    "infoPagador": "Pagamento de serviço"
}'
```

---

## Configurações importantes

### Banco de Dados

* Criação automática do banco (`createDatabaseIfNotExist=true`)
* SSL desabilitado para desenvolvimento (`useSSL=false`)
* Timezone UTC (`serverTimezone=UTC`)
* Permite recuperação de chave pública (`allowPublicKeyRetrieval=true`)

### CORS (Cross-Origin Resource Sharing)

* Configuração permite chamadas de qualquer origem (`allowedOrigins("*")`)
* Métodos permitidos: GET, POST, PUT, DELETE, OPTIONS
* Recomendado ajustar para produção para restringir origens confiáveis

### Segurança JWT

* Tokens gerados com algoritmo HMAC256 e segredo configurado
* Validação e autenticação via filtro customizado `SecurityFilter`
* Senhas armazenadas com hash BCrypt para segurança

### Email

* SMTP configurado para Gmail via variáveis de ambiente
* Porta 587, autenticação e STARTTLS habilitados

---

## Tecnologias Utilizadas

* Java 17
* Spring Boot
* Spring Security
* Spring Data JPA
* MySQL
* JSON (org.json)
* JavaMail (SMTP Gmail)
* JWT (auth0 Java JWT)

---

## Licença

Este projeto está licenciado sob a licença MIT - veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

