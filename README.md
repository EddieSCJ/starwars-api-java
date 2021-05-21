# Star Wars Api - B2W Desafio Técnico

## O que o sistema cobre ?
1. Testes unitários de uma ponta a outra
2. Boas abstrações de OO
3. Helpers
4. Services
5. Repositories
6. Response Handler
7. Exceções Personalizadas (Não rastreadas e nem handlers personalizados, simplifiquei)
8. Consome uma API do star wars
9. DTOs
10. Documentação com swagger
11. Dockeirização Multi-stage
12. Conexão com mongo-db
13. Ficou faltando BDD porque deu medo de não dar tempo de mockar o server com wiremock 

### Testando
O docker compose está executando os testes antes de subir mas caso queira
1. Abra o Intellij
2. Execute o path de testes

### Rodando o sistema
1. Instale o docker
2. Tenha certeza de que a porta 8080 está livre
3. Abra o terminal mais próximo
4. Digite docker-compose up ou docker compose up
5. Abra o insomnia ou outro cliente
6. Acesse o localhost:8080
7. Faça suas requisições


### Mas eu não sei como fazer as requisições
1. Aha espertinho
2. Após a etapa anterior copie este link http://localhost:8080/swagger-ui.html
3. Cole este link no navegador mais próximo
4. Leia a documentação

### Mas eu não sei ler
1. Execute a etapa anterior
2. Clique no primeiro dropdown que ver
3. Clique na primeira cor que ver
4. Clique no botão superior esquerdo
5. Clique no botão azul abaixo
6. Veja a mágica acontecer
