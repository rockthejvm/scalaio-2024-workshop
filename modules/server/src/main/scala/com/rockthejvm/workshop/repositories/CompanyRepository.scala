package com.rockthejvm.workshop.repositories

import zio.*
import io.getquill.jdbczio.Quill
import io.getquill.*
import com.rockthejvm.reviewboard.domain.data.Company

class CompanyRepository(quill: Quill.Postgres[SnakeCase.type]) {
  import quill.* // <-- entry point to quill API

  // table definition
  inline given schema: SchemaMeta[Company]  = schemaMeta[Company]("companies")
  inline given insMeta: InsertMeta[Company] = insertMeta[Company](_.id)
  inline given upMeta: UpdateMeta[Company]  = updateMeta[Company](_.id)

  // methods to call the DB
  def create(company: Company): Task[Company] = 
    run(
      query[Company]
        .insertValue(lift(company)) // insert into companies(.....) values (...) returning (....)
        .returning(r => r)
    )
  
  def getAll: Task[List[Company]] = 
    run(
      query[Company] // select * from companies
    )

  def getById(id: Long): Task[Option[Company]] = 
    run(
      query[Company].filter(_.id == lift(id))
    ).map(_.headOption)
}

object CompanyRepository {
  def layer = ZLayer.fromFunction(quill => new CompanyRepository(quill))
}
