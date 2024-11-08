package com.rockthejvm.workshop.http.controllers

import zio.*
import com.rockthejvm.reviewboard.http.endpoints.CompanyEndpoints
import com.rockthejvm.reviewboard.domain.data.Company
import sttp.tapir.server.ServerEndpoint
import zio.http.Server
import com.rockthejvm.workshop.services.CompanyService
import com.rockthejvm.workshop.services.PaymentService
import com.stripe.net.Webhook
import scala.jdk.OptionConverters.*
import com.stripe.model.checkout.Session

class CompanyController(service: CompanyService, paymentService: PaymentService) extends BaseController with CompanyEndpoints {
  val create: ServerEndpoint[Any, Task] = createEndpoint
    .serverLogic { req =>
      // CreateCompanyRequest => ZIO[_,Throwable,Company]
      service.create(req.name, req.url).either
    }

  val getAll: ServerEndpoint[Any, Task] = getAllEndpoint
    .serverLogic { _ =>
      service.getAll.logError("morti").either
    }

  val getById: ServerEndpoint[Any, Task] = getByIdEndpoint
    .serverLogic { id =>
      service.getById(id).either
    }

  // Stripe endpoints
  val createSponsored: ServerEndpoint[Any, Task] = 
    sponsoredEndpoint.serverLogic { req =>
      val program = for {
        _ <- ZIO.log("creating company")
        company <- service.create(req.name, req.url) // create a company with 'active' = false
        _ <- ZIO.log(s"created company: ${company}")
        session <- paymentService.createCheckoutSession(company.id).someOrFail(new RuntimeException("failed to make Stripe checkout session!")) // create a checkout session so that the user can pay for it
      } yield session.getUrl()

      program.either
    }

  val whSecret = "whsec_38d9b6ddc02f3a37fbb1a72c58696f53e7de98d6969a373c746626efba45ce52"
  val webhook: ServerEndpoint[Any, Task] = 
    webhookEndpoint.serverLogic { 
      case (signature, payload) =>
        val program: Task[Unit] = ZIO.attempt(Webhook.constructEvent(payload, signature, whSecret))
          .flatMap { event =>
            event.getType() match {
              case "checkout.session.completed" => 
                val maybeObj = event.getDataObjectDeserializer().getObject().toScala
                val refId = maybeObj
                  .map(_.asInstanceOf[Session])
                  .map(_.getClientReferenceId)

                  // if we get that id, we need to activate that company
                
                refId match {
                  case Some(id) => service.activate(id).unit
                  case None => ZIO.fail(new RuntimeException("No client reference id for you, friend")).logError("Webhook failure")
                }
              
              case _ => ZIO.unit
            }
          }

        program
          .logError("Stripe webhook")
          .either
    }

  override def routes: List[ServerEndpoint[Any, Task]] = 
    List(create, getAll, createSponsored, webhook, getById)
}

object CompanyController {
  def layer: ZLayer[CompanyService & PaymentService, Nothing, CompanyController] = 
    ZLayer.fromFunction((service, payment) => new CompanyController(service, payment))
}