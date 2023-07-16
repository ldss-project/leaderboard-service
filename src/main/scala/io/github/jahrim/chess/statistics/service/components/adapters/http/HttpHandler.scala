package io.github.jahrim.chess.statistics.service.components.adapters.http

import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext

/** An handler for http requests. */
@FunctionalInterface
trait HttpHandler extends Handler[RoutingContext]
