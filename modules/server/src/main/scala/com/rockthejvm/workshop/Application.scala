package com.rockthejvm.workshop

import zio.*
import zio.http.Server
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.server.ziohttp.ZioHttpServerOptions
import sttp.tapir.server.interceptor.cors.CORSInterceptor
import com.rockthejvm.workshop.http.controllers.*
import com.rockthejvm.workshop.services.*
import com.rockthejvm.workshop.repositories.*
import com.stripe.model.Event.Data
import io.getquill.context.ZioJdbc.DataSourceLayer

object Application extends ZIOAppDefault {
  

  def startServer = for {
    companies <- ZIO.service[CompanyController]
    health <- ZIO.service[HealthController]
    _ <- Server.serve(
      ZioHttpInterpreter(
        ZioHttpServerOptions.default.appendInterceptor(
          CORSInterceptor.default
        )
      ).toHttp(
        companies.routes ++ health.routes
      )
    )
  } yield ()

  def program = for {
    _ <- Console.printLine("Rock the JVM!")
    _ <- startServer
  } yield ()

  override def run = 
    program.provide(
      Server.default,
      // controllers
      HealthController.layer,
      CompanyController.layer, // <-- requires CompanyService!
      // services
      CompanyService.layer, // <-- requires CompanyRepository
      PaymentService.layer,
      // repositories
      CompanyRepository.layer,
      // infra
      DataLayer.quill,
      DataLayer.dataSource
    )
}
