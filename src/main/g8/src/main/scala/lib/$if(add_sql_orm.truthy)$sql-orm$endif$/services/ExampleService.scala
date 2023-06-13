package lib.sql_orm.tables

import lib.sql_orm.domain.Example

import io.getquill.jdbczio.Quill
import io.getquill._
import zio.ZIO
import java.sql.SQLException

class ExampleService(quill: Quill.Postgres[SnakeCase]) {
  import quill._

  def getById(id: String): ZIO[Any, SQLException, Option[Example]] = {
    run(quote {
      query[Example].filter(_.id == lift(id))
    }).map(_.headOption)
  }

  def getMany(
    where: Quoted[Example => Boolean],
    sortBy: Quoted[Example => Boolean],
    offset: Int,
    limit: Int
  ): ZIO[Any, SQLException, List[Example]] = {
    run(quote {
      query[Example]
        .filter(where(_))
        .sortBy(sortBy(_))
        .drop(lift(offset))
        .take(lift(limit))
    })
  }

  def insertOne(
    id: String,
    value: Int
  ): ZIO[Any, SQLException, Example] = {
    run(quote {
      query[Example].insertValue(lift(Example(id, value))).returning(_.id)
    }).map(id => Example(id, value))
  }

  def updateById(
    id: String,
    value: Int
  ): ZIO[Any, SQLException, Option[Example]] = {
    run(quote {
      query[Example]
        .filter(_.id == lift(id))
        .update(_.value -> lift(value))
    }).map(e => if (e == 1) Some(Example(id, value)) else None)
  }

  def updateMany(
    where: Quoted[Example => Boolean],
    value: Int
  ): ZIO[Any, SQLException, List[Example]] = {
    run(quote {
      query[Example]
        .filter(where(_))
        .update(_.value -> lift(value))
        .returningMany(r => (r.id, r.value))
    }).map(_.map((i, v) => Example(i, v)))
  }

  def deleteById(id: String): ZIO[Any, SQLException, Option[Example]] = {
    run(quote {
      query[Example]
        .filter(_.id == lift(id))
        .delete
    }).map(e => if (e == 1) Some(Example(id, 0)) else None)
  }

  def deleteMany(where: Quoted[Example => Boolean]): ZIO[Any, SQLException, List[Example]] = {
    run(quote {
      query[Example]
        .filter(where(_))
        .delete
        .returningMany(r => (r.id, r.value))
    }).map(_.map((i, v) => Example(i, v)))
  }
}
