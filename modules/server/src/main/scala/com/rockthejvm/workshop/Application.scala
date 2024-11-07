package com.rockthejvm.workshop

import zio.*
import zio.http.Server
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.server.ziohttp.ZioHttpServerOptions
import sttp.tapir.server.interceptor.cors.CORSInterceptor

object Application extends ZIOAppDefault {
  

  def startServer = for {
    _ <- Server.serve(
      ZioHttpInterpreter(
        ZioHttpServerOptions.default.appendInterceptor(
          CORSInterceptor.default
        )
      ).toHttp(List())
    )
  } yield ()

  def program = for {
    _ <- Console.printLine("Rock the JVM!")
    _ <- startServer
  } yield ()

  override def run: ZIO[Any & (ZIOAppArgs & Scope), Any, Any] = 
    program.provide(Server.default)
}
