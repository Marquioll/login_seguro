# Login Seguro

Sistema desenvolvido para uma atividade acadêmica usando Java,
Spring Boot, Thymeleaf, Spring Security e MongoDB Atlas.

## Funcionalidades

- Cadastro com validação dos dados.
- Senhas armazenadas como hash BCrypt.
- Login por e-mail e senha.
- Logout com invalidação da sessão.
- Autorização pelos perfis USUARIO, MODERADOR e ADMIN.
- Administração: listagem, edição, alteração de perfil e exclusão de usuários.
- Sessões armazenadas no MongoDB Atlas.
- Templates reutilizáveis e tema visual configurável.

## Tecnologias

- Java 21.
- Spring Boot 4.0.8.
- Spring Web.
- Spring Security.
- Spring Data MongoDB.
- Spring Session MongoDB: org.mongodb:mongodb-spring-session:4.0.0.
- Thymeleaf.
- Jakarta Validation.
- Maven Wrapper.

## Configuração do MongoDB Atlas

1. Crie uma conta no MongoDB Atlas e um cluster.
2. Crie um usuário de banco com a permissão readWrite no banco login_seguro.
3. Adicione o IP atual do computador à IP Access List.
4. No cluster, abra Connect → Drivers and Client Libraries.
5. Selecione Java e copie a URI mongodb+srv.
6. Substitua os marcadores de usuário e senha pelas credenciais do banco.

Se as credenciais contiverem caracteres reservados de URI, codifique-os
adequadamente. Mantenha TLS habilitado e a validação de certificados ativa.

O usuário de conexão do Atlas é diferente das contas cadastradas no sistema.

## Arquivo de configuração

O arquivo src/main/resources/application.properties contém:

```properties
spring.application.name=login-seguro

spring.mongodb.uri=${MONGODB_URI}
spring.mongodb.database=login_seguro

spring.data.mongodb.auto-index-creation=true

app.tema=padrao
```

A credencial real é fornecida pela variável de ambiente MONGODB_URI.
Não publique a URI com senha no repositório.

## Execução local no Windows

Pré-requisitos: JDK 21 e Git instalados.

1. Clone este repositório pela URL disponível no botão Code do GitHub.
2. Abra a pasta principal do projeto, que contém pom.xml e mvnw.cmd.
3. Abra um terminal PowerShell nessa pasta.
4. Defina a conexão usando a URI do seu cluster:

```powershell
$env:MONGODB_URI = 'mongodb+srv://USUARIO:SENHA@SEU_CLUSTER/?appName=LoginSeguro'
```

Substitua USUARIO, SENHA e SEU_CLUSTER pelos dados do seu Atlas.
Mantenha as aspas simples.

5. Execute no mesmo terminal:

```powershell
.\mvnw.cmd spring-boot:run
```

6. Acesse:

http://localhost:8080/login

O Maven Wrapper baixa as dependências na primeira execução.
Não é necessário instalar o Maven separadamente.

Para parar a aplicação, pressione Ctrl + C.
Ao abrir outro terminal ou reiniciar o computador, defina MONGODB_URI novamente.

## Primeiro administrador

1. Cadastre uma conta pela página /cadastro.
2. No Atlas, abra login_seguro → usuarios_login_seguro.
3. Localize o documento pelo e-mail cadastrado.
4. Altere somente o campo role de USUARIO para ADMIN, mantendo o tipo String.
5. Se a conta estiver conectada, faça logout.
6. Entre novamente e acesse /admin/usuarios.

Após essa configuração inicial, o administrador pode alterar os perfis
dos demais usuários pela interface.

## Perfis e rotas

| Rota | Permissão |
|---|---|
| /cadastro | Pública |
| /login | Pública |
| /inicio | Usuário autenticado |
| /moderador/painel | MODERADOR ou ADMIN |
| /admin/usuarios e suas operações | Somente ADMIN |

O cadastro público sempre cria contas com o perfil USUARIO.
Os perfis existentes são USUARIO, MODERADOR e ADMIN.

As permissões são carregadas no login. Após uma mudança de perfil,
faça logout e entre novamente na conta alterada.

As operações de cadastro, edição, exclusão e logout usam POST com
proteção CSRF habilitada.

## Validação e senhas

- Nome e e-mail obrigatórios.
- Validação do formato do e-mail.
- E-mail normalizado em letras minúsculas e com índice único.
- Nome com no máximo 100 caracteres.
- E-mail com no máximo 254 caracteres.
- Senha com pelo menos 8 caracteres e no máximo 72 bytes em UTF-8.
- Confirmação da senha obrigatória e igual à senha.
- Hash BCrypt gerado antes de salvar o usuário.
- O hash da senha não é exibido na administração.

## Integração com o MongoDB Atlas

Banco utilizado: login_seguro.

Coleções utilizadas:

- usuarios_login_seguro: dados dos usuários, hash da senha e perfil.
- sessions: sessões HTTP gerenciadas pela integração Spring Session MongoDB.

A coleção de usuários tem um índice único no campo email.

SessionConfig habilita as sessões no MongoDB com expiração após
30 minutos sem atividade. As sessões persistem após reiniciar a
aplicação, enquanto estiverem válidas e o navegador mantiver o cookie.

A coleção usuarios_login_seguro foi utilizada para separar esta atividade
de dados que já existiam na coleção usuarios do cluster.

## Estrutura do projeto

```text
src/main/java/com/lucas/login_seguro/
├── LoginSeguroApplication.java
├── config/
│   ├── PasswordConfig.java
│   ├── SecurityConfig.java
│   ├── SessionConfig.java
│   └── TemaConfig.java
├── controller/
│   ├── AuthController.java
│   ├── PaginaController.java
│   └── UsuarioController.java
├── dto/
│   ├── CadastroUsuarioDto.java
│   └── EdicaoUsuarioDto.java
├── model/
│   ├── Role.java
│   └── Usuario.java
├── repository/
│   └── UsuarioRepository.java
└── service/
    ├── UsuarioDetailsService.java
    └── UsuarioService.java

src/main/resources/
├── application.properties
├── static/css/temas/padrao.css
└── templates/
    ├── fragments/
    │   ├── head.html
    │   ├── header.html
    │   └── footer.html
    ├── cadastro.html
    ├── login.html
    ├── inicio.html
    ├── moderador/painel.html
    └── admin/
        ├── usuarios.html
        └── editar-usuario.html
```

## Separação da interface e da lógica

- Controllers recebem requisições e selecionam páginas.
- DTOs recebem e validam dados dos formulários.
- Services implementam regras de cadastro e gerenciamento.
- Repository acessa o MongoDB.
- Config configura autenticação, autorização, sessões e tema.
- Templates contêm a interface HTML.
- CSS define a aparência.

## Personalização do tema

1. Crie um arquivo CSS em src/main/resources/static/css/temas.
2. Por exemplo, para um arquivo chamado personalizado.css, configure:

```properties
app.tema=personalizado
```

3. Reinicie a aplicação.

O fragment head.html carrega o CSS correspondente ao nome configurado.
Os fragments header.html e footer.html compartilham a estrutura das páginas.

A personalização visual não exige alterar as regras de autenticação
ou os serviços de usuários.

## Gitflow

- main: versão pronta para entrega.
- develop: integração das funcionalidades.
- feature/*: desenvolvimento das funcionalidades.
- release/*: preparação da versão de entrega.
- hotfix/*: correções urgentes de versões entregues, quando necessárias.

As funcionalidades são integradas em develop. A entrega é preparada em
uma branch release e depois integrada em main e develop, com uma tag de versão.

## Verificações manuais realizadas

- Cadastro, login e logout.
- Hash BCrypt e índice único de e-mail no Atlas.
- Bloqueio da administração para USUARIO.
- Acesso à administração como ADMIN.
- Acesso ao painel como MODERADOR e bloqueio de sua entrada na administração.
- Listagem, edição de nome, alteração de perfil e exclusão.
- Armazenamento da sessão no Atlas.
- Permanência do login após reiniciar a aplicação.
- Bloqueio de acesso à página inicial após logout.
- Aplicação dos fragments e troca de tema pela configuração.
- Rejeição de cadastro com senha e confirmação diferentes.
- Rejeição de cadastro com e-mail já existente.
- Rejeição de login com senha incorreta.
- Rejeição de edição com e-mail pertencente a outro usuário.