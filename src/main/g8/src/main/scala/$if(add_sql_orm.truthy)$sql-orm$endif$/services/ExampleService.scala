package sql_orm.services

import sql_orm.domain.ExampleEntity

import io.getquill.jdbczio.Quill
import io.getquill._
import zio._
import java.sql.SQLException

class ExampleService(quill: Quill.Postgres[SnakeCase]) {
  import quill._

  def getById(id: String): ZIO[Any, SQLException, Option[ExampleEntity]] = {
    run(quote {
      query[ExampleEntity].filter(_.id == lift(id))
    }).map(_.headOption)
  }

  def getMany(
    where: Quoted[ExampleEntity => Boolean],
    sortBy: Quoted[ExampleEntity => Boolean],
    offset: Int,
    limit: Int
  ): ZIO[Any, SQLException, List[ExampleEntity]] = {
    run(quote {
      query[ExampleEntity]
        .filter(where(_))
        .sortBy(sortBy(_))
        .drop(lift(offset))
        .take(lift(limit))
    })
  }

  def insertOne(
    id: String,
    value: Int
  ): ZIO[Any, SQLException, ExampleEntity] = {
    run(quote {
      query[ExampleEntity].insertValue(lift(ExampleEntity(id, value))).returning(_.id)
    }).map(id => ExampleEntity(id, value))
  }

  def updateById(
    id: String,
    value: Int
  ): ZIO[Any, SQLException, Option[ExampleEntity]] = {
    run(quote {
      query[ExampleEntity]
        .filter(_.id == lift(id))
        .update(_.value -> lift(value))
    }).map(e => if (e == 1) Some(ExampleEntity(id, value)) else None)
  }

  def updateMany(
    where: Quoted[ExampleEntity => Boolean],
    value: Int
  ): ZIO[Any, SQLException, List[ExampleEntity]] = {
    run(quote {
      query[ExampleEntity]
        .filter(where(_))
        .update(_.value -> lift(value))
        .returningMany(r => (r.id, r.value))
    }).map(_.map((i, v) => ExampleEntity(i, v)))
  }

  def deleteById(id: String): ZIO[Any, SQLException, Option[ExampleEntity]] = {
    run(quote {
      query[ExampleEntity]
        .filter(_.id == lift(id))
        .delete
    }).map(e => if (e == 1) Some(ExampleEntity(id, 0)) else None)
  }

  def deleteMany(where: Quoted[ExampleEntity => Boolean]): ZIO[Any, SQLException, List[ExampleEntity]] = {
    run(quote {
      query[ExampleEntity]
        .filter(where(_))
        .delete
        .returningMany(r => (r.id, r.value))
    }).map(_.map((i, v) => ExampleEntity(i, v)))
  }
}

object ExampleService {
  val live = ZLayer.fromFunction(new ExampleService(_))
}