package com.rockthejvm.workshop.repositories

import io.getquill.jdbczio.Quill
import io.getquill.SnakeCase

object DataLayer {
  val dataSource = Quill.DataSource.fromPrefix("db") // creates a DataSource from application.conf
  val quill = Quill.Postgres.fromNamingStrategy(SnakeCase)
}
