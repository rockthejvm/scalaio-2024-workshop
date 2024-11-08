package com.rockthejvm.workshop.services

import zio.*
import com.stripe.model.checkout.Session
import com.stripe.Stripe
import com.stripe.param.checkout.SessionCreateParams

class PaymentService {

  

  /* 
    Creates a checkout session (on Stripe) for a company we've just added
    but currently inactive ('active' = false in the db).

    We'll return a checkout session with a link, redirect the user,
    and then after payment, we will activate the company (at a later point).
   */
  def createCheckoutSession(id: Long): Task[Option[Session]] = 
    ZIO.attempt(
      SessionCreateParams.builder()
        .setSuccessUrl("https://rockthejvm.com")
        .addLineItem(
          SessionCreateParams.LineItem.builder()
            .setPrice("price_1QIsUXEbqSNrvVAB0FYJotd7")
            .setQuantity(1L)
            .build()
        )
        .setClientReferenceId(id.toString) // unique identifier will be included in the webhook payload at the validation step
        .setMode(SessionCreateParams.Mode.PAYMENT)
        .build()
    )
    .logError("Stripe checkout session failure")
    .map(params => Session.create(params))
    .map(Option(_))
}

object PaymentService {
  def layer = ZLayer {
    for {
      _ <- ZIO.attempt(
        Stripe.apiKey = "sk_test_51MHkdcEbqSNrvVABkg8GGfp9uFIZZwvGVbYmQHfVGaeriT9EJ2JnlkiMOnmpclbnq04a00ZAEnLVOuqsEati8qzL00e1moFFnb"
      )
      service <- ZIO.succeed(new PaymentService)
    } yield service
  }
}
