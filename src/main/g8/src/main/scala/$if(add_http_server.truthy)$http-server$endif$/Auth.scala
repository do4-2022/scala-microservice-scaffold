import zio._
import zio.http._
import zio.Console._
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import java.time.Clock

object Auth {
  // Secret key to encode the JWT token
  private val JWT_SECRET =
    sys.env.getOrElse("JWT_SECRET", "secretKey")

  // Clock to set the expiration time of the JWT token
  implicit val clock: Clock = Clock.systemUTC

  // Method to encode the JWT token
  def jwtEncode(username: String): String = {
    val json = s"""{"username": "\${username}"}"""

    val claim = JwtClaim {
      json
    }.issuedNow.expiresIn(300)

    Jwt.encode(claim, JWT_SECRET, JwtAlgorithm.HS512)
  }

  // Method to decode the JWT token
  def jwtDecode(token: String): Option[JwtClaim] = {
    Jwt.decode(token, JWT_SECRET, Seq(JwtAlgorithm.HS512)).toOption
  }
}
