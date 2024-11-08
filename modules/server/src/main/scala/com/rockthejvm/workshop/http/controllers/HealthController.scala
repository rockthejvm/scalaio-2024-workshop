package com.rockthejvm.workshop.http.controllers

import zio.*
import com.rockthejvm.reviewboard.http.endpoints.HealthEndpoint
import sttp.tapir.server.ServerEndpoint

class HealthController extends BaseController with HealthEndpoint {
  val health: ServerEndpoint[Any, Task] = 
    healthEndpoint.serverLogicSuccess(_ => ZIO.succeed("All good!"))

  override def routes: List[ServerEndpoint[Any, Task]] = 
    List(health)
}

object HealthController {
  def layer = ZLayer.succeed(new HealthController) // "smart constructor"
}