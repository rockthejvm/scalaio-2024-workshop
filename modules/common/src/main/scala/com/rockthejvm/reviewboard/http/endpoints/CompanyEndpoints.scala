package com.rockthejvm.reviewboard.http.endpoints

import sttp.tapir.*
import sttp.tapir.json.zio.*
import sttp.tapir.generic.auto.* // imports the type class derivation package

import sttp.tapir.EndpointIO.annotations.jsonbody
import com.rockthejvm.reviewboard.domain.data.*
import com.rockthejvm.reviewboard.http.requests.CreateCompanyRequest
import sttp.model.MediaType
import sttp.model.Header

trait CompanyEndpoints extends BaseEndpoint {
  // POST /api/companies {name, url} -> Company
  val createEndpoint =
    baseEndpoint
      .in("companies")
      .post
      .in(jsonBody[CreateCompanyRequest]) // arg = CreateCompanyRequest
      .out(jsonBody[Company])


  // GET /api/companies -> List[Company]
  val getAllEndpoint =
    baseEndpoint
    .in("companies")
    .get
    .out(jsonBody[List[Company]])

  // GET /api/companies/$id -> Option[Company]
  val getByIdEndpoint =
    baseEndpoint
      .in("companies" / path[String]("id"))
      .get
      .out(jsonBody[Option[Company]])


  // Stripe endpoints
  val sponsoredEndpoint =
    baseEndpoint
      .in("companies" / "sponsored")
      .post
      .in(jsonBody[CreateCompanyRequest]) 
      .out(stringBody)  // returns Stripe checkout link

  val webhookEndpoint = 
    baseEndpoint
      .in("companies" / "webhook")
      .in(header[String]("Stripe-Signature")) // signing the payload
      .post
      .in(stringBody)
}

// sbt "project server; run"
