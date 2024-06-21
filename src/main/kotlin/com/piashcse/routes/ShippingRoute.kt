package com.piashcse.routes

import com.papsign.ktor.openapigen.route.path.auth.delete
import com.papsign.ktor.openapigen.route.path.auth.get
import com.papsign.ktor.openapigen.route.path.auth.post
import com.papsign.ktor.openapigen.route.path.auth.principal
import com.papsign.ktor.openapigen.route.path.auth.put
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.piashcse.controllers.ShippingController
import com.piashcse.models.shipping.AddShipping
import com.piashcse.models.shipping.OrderId
import com.piashcse.models.shipping.UpdateShipping
import com.piashcse.models.user.body.JwtTokenBody
import com.piashcse.plugins.RoleManagement
import com.piashcse.utils.ApiResponse
import com.piashcse.utils.Response
import com.piashcse.utils.authenticateWithJwt
import io.ktor.http.HttpStatusCode

fun NormalOpenAPIRoute.shippingRoute(shippingController: ShippingController) {
    route("shipping") {
        authenticateWithJwt(RoleManagement.CUSTOMER.role) {
            post<Unit, Response, AddShipping, JwtTokenBody> { _, addShipping ->
                addShipping.validation()

                respond(
                    ApiResponse.success(
                        data = shippingController.addShipping(
                            userId = principal().userId,
                            addShipping = addShipping,
                        ),
                        statsCode = HttpStatusCode.Created,
                    )
                )
            }

            get<OrderId, Response, JwtTokenBody> { orderId ->
                respond(
                    ApiResponse.success(
                        data = shippingController.getShipping(
                            userId = principal().userId,
                            orderId = orderId.orderId,
                        ),
                        statsCode = HttpStatusCode.OK,
                    )
                )
            }

            route("/{orderId}")
                .put<UpdateShipping, Response, Unit, JwtTokenBody> { updateShipping, _ ->
                    updateShipping.validation()

                    val shipping = shippingController.updateShipping(
                        userId = principal().userId,
                        updateShipping = updateShipping,
                    )

                    respond(
                        ApiResponse.success(
                            data = shipping,
                            statsCode = HttpStatusCode.OK,
                        )
                    )
                }

            delete<OrderId, Response, JwtTokenBody> { orderId ->
                orderId.validation()

                respond(
                    ApiResponse.success(
                        data = shippingController.deleteShipping(
                            userId = principal().userId,
                            orderId = orderId.orderId,
                        ),
                        statsCode = HttpStatusCode.NoContent,
                    )
                )
            }
        }
    }
}
