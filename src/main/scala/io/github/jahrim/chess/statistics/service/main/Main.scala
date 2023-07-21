package io.github.jahrim.chess.statistics.service.main

import io.github.jahrim.chess.statistics.service.components.ports.{StatisticsModel, StatisticsPort}
import io.github.jahrim.chess.statistics.service.components.adapters.http.StatisticsHttpAdapter
import io.github.jahrim.hexarc.persistence.mongodb.MongoDBPersistentCollection
import io.github.jahrim.hexarc.architecture.vertx.core.dsl.VertxDSL.*
import io.github.jahrim.hexarc.logging.Loggers
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServerOptions
import org.rogach.scallop.*

/** Main of the application. */
@main def main(args: String*): Unit =
  val arguments: Args = Args(args)

  DeploymentGroup.deploySingle(Vertx.vertx()) {
    new Service:
      name = "StatisticsService"

      new Port[StatisticsPort]:
        model = StatisticsModel(
          scoreCollection = MongoDBPersistentCollection(
            connection = arguments.mongoDBConnection(),
            database = arguments.mongoDBDatabase(),
            collection = arguments.mongoDBCollection()
          ).get
        )

        new Adapter(
          adapter = StatisticsHttpAdapter(
            new HttpServerOptions:
              setHost(arguments.httpHost())
              setPort(arguments.httpPort())
          )
        )
  }

/**
 * The parsed command line arguments accepted by this application.
 *
 * @param arguments the sequence of command line arguments to parse.
 *
 * @see [[https://github.com/scallop/scallop Scallop Documentation on Github]].
 */
class Args(private val arguments: Seq[String]) extends ScallopConf(arguments):
  val httpHost: ScallopOption[String] = opt[String](
    name = "http-host",
    descr = "The server host for the http adapter of this service.",
    default = Some("localhost"),
    required = true
  )
  val httpPort: ScallopOption[Int] = opt[Int](
    name = "http-port",
    descr = "The server port for the http adapter of this service.",
    default = Some(8080),
    required = true
  )
  val mongoDBConnection: ScallopOption[String] = opt[String](
    name = "mongodb-connection",
    descr = "The connection string to the mongodb instance used by this service.",
    required = true
  )
  val mongoDBDatabase: ScallopOption[String] = opt[String](
    name = "mongodb-database",
    descr = "The database within the mongodb instance used by this service.",
    default = Some("statistics"),
    required = true
  )
  val mongoDBCollection: ScallopOption[String] = opt[String](
    name = "mongodb-collection",
    descr = "The collection within the mongodb database used by this service.",
    default = Some("scores"),
    required = true
  )
  verify()
