package com.rockthejvm.workshop.http.controllers

import zio.*
import com.rockthejvm.reviewboard.http.endpoints.CompanyEndpoints
import com.rockthejvm.reviewboard.domain.data.Company
import sttp.tapir.server.ServerEndpoint

class CompanyController extends CompanyEndpoints {
  val create: ServerEndpoint[Any, Task] = createEndpoint
    .serverLogic { req =>
      // CreateCompanyRequest => ZIO[_,Throwable,Company]
      ZIO.attempt(Company.dummy).either
    }
}
