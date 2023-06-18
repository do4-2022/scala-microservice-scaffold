import zio._
import zio.http._
import zio.Console._
import Auth.jwtEncode
import upickle.default._
import Auth.jwtDecode

case class User(username: String, password: String) derives ReadWriter

object User {
  private val users = scala.collection.mutable.ListBuffer.empty[User]

  val routes: Http[Any, Nothing, Request, Response] =
    Http.collectZIO[Request] {
      case req @ Method.POST -> Root / "register" => {
        (for {
          // Parse the body of the request
          user: User <- req.body.asString.map(_.split("&")).map { arr =>
            val map = arr
              .map(_.split("="))
              .map { arr =>
                arr(0) -> arr(1)
              }
              .toMap

            val username = map("username")
            val password = map("password")

            User(username, password)
          }

          _ <- printLine(s"Registering user: \$user")

          // Add user to the list of users
          _ <- ZIO.succeed(users.addOne(user))
        } yield Response.json(write(user)))
          .catchAll(_ =>
            ZIO.succeed(
              Response.status(Status.InternalServerError)
            )
          )
      }

      case req @ Method.POST -> Root / "login" =>
        (for {
          // Parse the body of the request
          user: User <- req.body.asString.map(_.split("&")).map { arr =>
            val map = arr
              .map(_.split("="))
              .map { arr =>
                arr(0) -> arr(1)
              }
              .toMap

            val username = map("username")
            val password = map("password")

            User(username, password)
          }

          _ <- printLine(s"User trying to login: \$user")

          // Fetch user from the list of users
          storedUser: User <- ZIO.succeed(
            if (users.exists(_.username == user.username))
              users.find(_.username == user.username).get
            else
              User("", "")
          )

          // Check if the password matches
          response <- ZIO.succeed(
            if (user.password == storedUser.password)
              Response.json(
                "{\"token\": \"" + jwtEncode(user.username) + "\"}"
              )
            else
              Response
                .json("{\"message\": \"Invalid username or password\"}")
                .withStatus(Status.Unauthorized)
          )
        } yield response)
          .catchAll(_ =>
            ZIO.succeed(
              Response.status(Status.InternalServerError)
            )
          )

      case req @ Method.GET -> Root / "user" / name =>
        (for {
          _ <- printLine(s"User trying to access: \$name")

          // Parse the body of the request
          token <- req.headers
            .get("Authorization")
            .map(_.split(" "))
            .map(_.last)
            .map(ZIO.succeed(_))
            .getOrElse(ZIO.fail("No token found"))

          // Decode the token
          tokenContent <- ZIO.fromOption(jwtDecode(token))

          _ <- printLine(s"Token content: \${tokenContent.content}")

          // Fetch username from the token
          username <- ZIO.fromOption(
            read[Map[String, String]](tokenContent.content).get("username")
          )

          // Fetch user from the list of users
          storedUser: User <- ZIO.succeed(
            if (users.exists(_.username == username))
              users.find(_.username == username).get
            else
              User("", "")
          )

          // Check if the username matches
          response <- ZIO.succeed(
            if (storedUser.username == name)
              Response.json(write(storedUser))
            else
              Response
                .json(
                  "{\"message\": \"Unauthorized\"}"
                )
                .withStatus(Status.Unauthorized)
          )
        } yield response)
          .catchAll(_ =>
            ZIO.succeed(
              Response.status(Status.InternalServerError)
            )
          )
    }
}
