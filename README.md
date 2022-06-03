# <p align="center"> :space_invader: Star Wars API :space_invader:</p>

#### <p align="center"> Project Tooling </p>

<div align="center"> 
    <a href="https://app.snyk.io/org/eddiescj/projects" target="_blank">:wolf: Snyk </a>
    <a href="https://sonarcloud.io/summary/new_code?id=EddieSCJ_starwars-api-java2" target="_blank">:detective: SonarCloud </a>
    <a href="https://app.codecov.io/gh/EddieSCJ/starwars-api-java/" target="_blank">:open_umbrella: CodeCov </a>
</div>

#### <p align="center"> Requirements </p>

<div align="center"> 
    <a href="https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html" target="_blank">:coffee: Java 17 </a>
    <a href="https://docs.docker.com/get-docker/" target="_blank"> :whale: Docker </a>
    <a href="https://docs.docker.com/get-docker/" target="_blank"> <img width="13" src="https://raw.githubusercontent.com/davzoku/emoji.ico/master/devicon/mongodb-original.ico"> MongoDB </a>
</div>

#### <p align="center"> Description </p>

This api is a simple wrapper for the [Star Wars API](https://swapi.dev/) where you can get information about the characters,
planets, starships, vehicles, species, films, and more with a few extra features where you can handle this data however you
need.

The api also is documented with [OpenAPI Swagger](https://swagger.io/specification/), so if you have any doubt, just open in your browser the follow url: `http://domain-you-are-using/api/v0/swagger-ui/index.html`

Please, read the content below to know how to use this api and if is there any doubt, please, contact me.

#### <p align="center"> PR Advices </p>

You'll see that once you open a PR some checks will be made, they are all essential to be passed before you merge your branch, so, make sure they are all passing.

One exception is snyk, which sometimes have some problems that can't be solved now, in this case you can just bypass.

#### <p align="center"> Using Cloud Tools </p>

- Snyk
  - Just click in the link above and search for starwars-api-java, so you will be able to see the security problems.
- SonarCloud
  - Clicking the link above you will be redirected to the quality analysis of this project.
- CodeCov
  - You can click the link above and see the code coverage details by commit or any type of data or just see the summary
    in your PR.

#### <p align="center"> Making Requests </p>

Being authenticated:

```bash
curl -o token.json --location --request POST 'http://localhost:8080/api/v0/login' \
--header 'Content-Type: application/json' \
--data-raw '{
    "username":"another_application_who_consumes_this_api",
    "password": "12"
}'
```

Using Authorization to get planets

```bash
curl -o result.json --location --request GET 'http://localhost:8080/api/v0/planets' \
--header 'Authorization: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhbm90aGVyX2FwcGxpY2F0aW9uX3dob19jb25zdW1lc190aGlzX2FwaSJ9.jnSWQTkg6dQ18tAPl8RS2JrdEdmtxBvx40Tq7WqYFighnziLKzUi2BLJ4S__dOlQDuJl0Lw3NYFS5IbGgd-XnQ' \
--header 'Content-Type: application/json' | json_pp
```

#### <p align="center"> Running </p>

This application runs in development mode with [Localstack](https://localstack.cloud) and [Mongo](https://www.mongodb.com) in docker containers. You also have the option to run the application as a container.

To run the application as a container run the following commands:

```
chmod +x ./container-mode.sh

./container-mode.sh
```

To run the application using the Java environment of your computer, run the following commands:

```
chmod +x ./developer-mode.sh

./developer-mode.sh
```

As both of ways use the same port (8080), They cannot run simultaneously.
