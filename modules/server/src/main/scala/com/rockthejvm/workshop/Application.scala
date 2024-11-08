package com.rockthejvm.workshop

import zio.*
import zio.http.Server
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.server.ziohttp.ZioHttpServerOptions
import sttp.tapir.server.interceptor.cors.CORSInterceptor
import com.rockthejvm.workshop.http.controllers.CompanyController

object Application extends ZIOAppDefault {
  

  def startServer = for {
    companies <- ZIO.succeed(new CompanyController)
    _ <- Server.serve(
      ZioHttpInterpreter(
        ZioHttpServerOptions.default.appendInterceptor(
          CORSInterceptor.default
        )
      ).toHttp(
        List(companies.create)
      )
    )
  } yield ()

  def program = for {
    _ <- Console.printLine("Rock the JVM!")
    _ <- startServer
  } yield ()

  override def run = 
    program.provide(Server.default)
}
