package com.rockthejvm.workshop.services

import zio.*
import com.rockthejvm.reviewboard.domain.data.Company
import com.rockthejvm.workshop.repositories.CompanyRepository

class CompanyService(repo: CompanyRepository) {
  def create(name: String, url: String): Task[Company] = 
    repo.create(
      Company(-1, Company.makeSlug(name), name, url)
    )

  def getAll: Task[List[Company]] =
    repo.getAll

  def getById(id: String): Task[Option[Company]] = 
    for {
      numId <- ZIO.attempt(id.toLong) // surface exceptions if id is not a long
      company <- repo.getById(numId)
    } yield company

  def activate(id: String): Task[Boolean] = 
    for {
      numId <- ZIO.attempt(id.toLong) // surface exceptions if id is not a long
      result <- repo.activate(numId)
    } yield result
}


object CompanyService {
  def layer = ZLayer.fromFunction(new CompanyService(_))
}