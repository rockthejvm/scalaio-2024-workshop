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
      company <- repo.getById(id.toLong)
    } yield company
}


object CompanyService {
  def layer = ZLayer.fromFunction(new CompanyService(_))
}