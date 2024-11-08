package com.rockthejvm.workshop.http.controllers

import zio.*
import com.rockthejvm.reviewboard.http.endpoints.CompanyEndpoints
import com.rockthejvm.reviewboard.domain.data.Company
import sttp.tapir.server.ServerEndpoint
import zio.http.Server
import com.rockthejvm.workshop.services.CompanyService

class CompanyController(service: CompanyService) extends BaseController with CompanyEndpoints {
  val create: ServerEndpoint[Any, Task] = createEndpoint
    .serverLogic { req =>
      // CreateCompanyRequest => ZIO[_,Throwable,Company]
      service.create(req.name, req.url).either
    }

  val getAll: ServerEndpoint[Any, Task] = getAllEndpoint
    .serverLogic { req =>
      service.getAll.either
    }

  val getById: ServerEndpoint[Any, Task] = getByIdEndpoint
    .serverLogic { id =>
      service.getById(id).either
    }

  override def routes: List[ServerEndpoint[Any, Task]] = 
    List(create, getAll, getById)
}

object CompanyController {
  def layer: ZLayer[CompanyService, Nothing, CompanyController] = 
    ZLayer.fromFunction(service => new CompanyController(service))
}