package com.rockthejvm.workshop

import zio.*

/*  
  - value it produces 
  * error if it fails
  * requirements to run
  - separate the construction from the execution
 */

case class MyIO[A](action: () => A) {
  def map[B](f: A => B): MyIO[B] = MyIO(() => f(action()))
  def flatMap[B](f: A => MyIO[B]) = MyIO(() => f(action()).action())
}

case class MyZIO[-R, +E, +A](action: R => Either[E,A])

type UIO[A] = MyZIO[Any, Nothing, A]
type Task[A] = MyZIO[Any, Throwable, A]

case class Config(data: String)
case class TokenManager(db: String)
case class AuthService(manager: TokenManager)

case class WebServer(config: Config, auth: AuthService) {
  def run = Console.printLine("Server is running!")
}

object ZIODemo extends ZIOAppDefault {

  def program = for {
    tokenManager <- ZIO.service[TokenManager]
    authService <- ZIO.succeed(AuthService(tokenManager))
    config <- ZIO.service[Config] // ZIO[Config, Nothing, Config] <- Config is what I need
    server <- ZIO.succeed(WebServer(config, authService))
    _ <- server.run
  } yield ()

  override def run = program.provide(
    ZLayer.succeed(Config("host: 127.0.0.1")),
    ZLayer.succeed(TokenManager("{}"))
  )
}
