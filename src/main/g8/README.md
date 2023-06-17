# $name$

$name$ is a microservice written in scala.

## Run locally

$if(add_sql_orm.truthy||add_message_queue.truthy)$

You can run this project locally using [Docker Compose](https://docs.docker.com/compose/) and [SBT](https://www.scala-sbt.org/).

1. Clone this project.
2. Run `docker-compose.yaml`:

```bash
  cd /path/to/$name$
  docker-compose up -d
```

3. Run $name$ with SBT:

```bash
  sbt run
```

**Done!**
$else$

You can run this project locally using [SBT](https://www.scala-sbt.org/).

1. Clone this project.
2. Run $name$ with SBT:

```bash
  sbt run
```

**Done!**
$endif$

## Deploy in production

This project provide [Terraform](https://www.terraform.io/) configurations to deploy $name$ with Kubernetes.

1. Clone this project.
2. Create a local cluster (depends on the Kubernetes implementation you are using).
3. Copy `terraform/env-example.tfvars` to `terraform/env.tfvars` and change variables as you need
4. Deploy $name$ and its dependencies with Terraform:

```bash
  cd /path/to/$name$/terraform
  terraform init
  terraform apply -var-file="env.tfvars"
```

**Done!**
$if(add_http_server.truthy||add_sql_orm.truthy||add_message_queue.truthy)$

## Environnement variables

$if(add_http_server.truthy)$

**HTTP_SERVER_PORT**

The http server port. Default is _8080_.

**JWT_SECRET**

The jwt secret to encode authentication tokens. Default is _secretKey_.
$endif$$if(add_sql_orm.truthy)$

**POSTGRES_USER**

The username used to login with PostgreSQL database. Default is _postgres_.

**POSTGRES_PASSWORD**

The password used to login with PostgreSQL database. Default is _postgres_.

**POSTGRES_DATABASE**

The PostgreSQL database to connect to. Default is _postgres_.

**POSTGRES_HOST**

The PostgreSQL hostname. Default is _localhost_.

**POSTGRES_PORT**

The PostgreSQL hostname. Default is _5432_.
$endif$$if(add_message_queue.truthy)$

**RABBITMQ_HOST**

The RabbitMQ hostname. No default value.

**RABBITMQ_PORT**

The RabbitMQ AMQP Port. Default is _5672_.

**RABBITMQ_USER**

The RabbitMQ default username. Default is _rabbitmq_.

**RABBITMQ_PASSWORD**

The RabbitMQ default password. Default is _rabbitmq_.
$endif$$endif$

$if(add_http_server.truthy)$

## HTTP API

`GET /health`

Check if the service is up and running.

Response:

```json
{
  "status": "ok"
}
```

`POST /register`

Register a new user.

Request body:

```json
{
  "username": "username",
  "password": "password"
}
```

Response:

```json
{
  "username": "username",
  "password": "password"
}
```

`POST /login`

Login with a user.

Request body:

```json
{
  "username": "username",
  "password": "password"
}
```

Response:

```json
{
  "token": "token"
}
```

`GET /user/:username`

Get a user. This route is protected by JWT authentication.

Request header:

```json
{
  "Authorization": "Bearer <auth_token>"
}
```

Response:

```json
{
  "username": "username",
  "password": "password"
}
```

$endif$
