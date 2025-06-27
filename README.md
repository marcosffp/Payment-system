# Payment System

O Payment System é um projeto para gestão de pagamentos via Pix, integrando-se à API EfPlay. Oferece criação de cobranças, QR Codes e transferências, além de autenticação com JWT e gerenciamento de usuários. Desenvolvido com Spring Boot, Spring Security e MySQL, garante segurança, eficiência e escalabilidade.

## Funcionalidades

### Autenticação e Usuários
- Registro de usuários com verificação por email
- Autenticação JWT
- Endpoints protegidos por autorização

### Operações Pix
- Criação de chaves Pix (EVP)
- Geração de cobranças Pix
- Envio de transações Pix

## Como Rodar o Projeto

### Pré-requisitos
- Java JDK 17+
- Maven
- MySQL
- Docker (opcional)

### Configuração

1. Clone o repositório:
```bash
git clone https://github.com/seu-usuario/payment-system.git
cd payment-system
```

2. Configure o banco de dados:
- O sistema usará automaticamente o banco MySQL chamado `payments` (criado automaticamente se não existir)
- Credenciais padrão (podem ser alteradas no arquivo `application.properties`):
  - Usuário: `root`
  - Senha: `root`

3. Configure as variáveis de ambiente:
- `MAIL_USER`: Seu email para envio de confirmações
- `MAIL_PASSWORD`: Senha do email
- `JWT_SECRET`: Chave secreta para geração de tokens JWT

4. Build e execução:
```bash
mvn clean install
mvn spring-boot:run
```


## Endpoints da API

### Autenticação
- `POST /api/v1/auth/login` - Login de usuário (retorna token JWT)
- `POST /api/v1/user/register` - Registro de novo usuário
- `GET /api/v1/user/verify` - Verificação de email

### Usuários
- `GET /api/v1/user/teste` - Endpoint protegido para teste de autenticação

### Operações Pix
- `GET /pix/criar` - Cria uma nova chave Pix EVP
- `POST /pix/cobrar` - Cria uma cobrança Pix
- `POST /pix/enviar` - Envia um valor via Pix

## Exemplo de Uso

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

2. Fazer login:
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
-H "Content-Type: application/json" \
-d '{
    "email": "usuario@exemplo.com",
    "password": "senha123"
}'
```

3. Criar uma cobrança Pix (usando o token JWT obtido no login):
```bash
curl -X POST http://localhost:8080/pix/cobrar \
-H "Content-Type: application/json" \
-H "Authorization: Bearer <SEU_TOKEN_JWT>" \
-d '{
    "valor": "100.00",
    "infoPagador": "Pagamento de serviço"
}'
```

## Configurações do Banco de Dados
O sistema está configurado para:
- Criar automaticamente o banco de dados se não existir (`createDatabaseIfNotExist=true`)
- Usar SSL desabilitado para desenvolvimento local (`useSSL=false`)
- Configuração de timezone UTC (`serverTimezone=UTC`)
- Permitir recuperação de chave pública (`allowPublicKeyRetrieval=true`)

## Configurações de Email
O sistema usa SMTP do Gmail configurável através das variáveis de ambiente:
- Porta: 587
- Autenticação e STARTTLS habilitados

## Licença
Este projeto está licenciado sob a licença MIT - veja o arquivo [LICENSE](LICENSE) para mais detalhes.
