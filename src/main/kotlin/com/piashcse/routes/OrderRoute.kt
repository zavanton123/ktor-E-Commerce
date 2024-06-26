package com.piashcse.routes

import com.papsign.ktor.openapigen.route.path.auth.get
import com.papsign.ktor.openapigen.route.path.auth.post
import com.papsign.ktor.openapigen.route.path.auth.principal
import com.papsign.ktor.openapigen.route.path.auth.put
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.piashcse.controllers.OrderController
import com.piashcse.models.PagingData
import com.piashcse.models.order.AddOrder
import com.piashcse.models.order.OrderId
import com.piashcse.models.orderitem.OrderItem
import com.piashcse.models.user.body.JwtTokenBody
import com.piashcse.plugins.RoleManagement
import com.piashcse.utils.ApiResponse
import com.piashcse.utils.Response
import com.piashcse.utils.authenticateWithJwt
import com.piashcse.utils.extensions.OrderStatus
import io.ktor.http.HttpStatusCode

fun NormalOpenAPIRoute.orderRoute(orderController: OrderController) {
    route("order") {
        authenticateWithJwt(RoleManagement.CUSTOMER.role) {
            post<Unit, Response, AddOrder, JwtTokenBody>(
                exampleRequest = AddOrder(
                    quantity = 1,
                    subTotal = 100f,
                    total = 100f,
                    shippingCharge = 2f,
                    orderStatus = "pending",
                    orderItems = mutableListOf(OrderItem("productId", 1)),
                ),
            ) { _, addOrder: AddOrder ->
                addOrder.validation()

                respond(
                    ApiResponse.success(
                        data = orderController.createOrder(
                            userId = principal().userId,
                            addOrder = addOrder,
                        ),
                        statsCode = HttpStatusCode.Created,
                    )
                )
            }

            get<PagingData, Response, JwtTokenBody> { pagingData ->
                pagingData.validation()

                respond(
                    ApiResponse.success(
                        data = orderController.getOrders(
                            userId = principal().userId,
                            pagingData = pagingData,
                        ),
                        statsCode = HttpStatusCode.OK,
                    )
                )
            }

            route("/payment")
                .put<OrderId, Response, Unit, JwtTokenBody> { orderId, _ ->
                    orderId.validation()

                    respond(
                        ApiResponse.success(
                            data = orderController.updateOrder(
                                userId = principal().userId,
                                orderId = orderId,
                                orderStatus = OrderStatus.PAID,
                            ),
                            statsCode = HttpStatusCode.OK,
                        )
                    )
                }

            route("/cancel")
                .put<OrderId, Response, Unit, JwtTokenBody> { orderId, _ ->
                    orderId.validation()

                    respond(
                        ApiResponse.success(
                            data = orderController.updateOrder(
                                userId = principal().userId,
                                orderId = orderId,
                                orderStatus = OrderStatus.CANCELED
                            ),
                            statsCode = HttpStatusCode.NoContent,
                        )
                    )
                }

            route("/receive")
                .put<OrderId, Response, Unit, JwtTokenBody> { orderId, _ ->
                    orderId.validation()

                    respond(
                        ApiResponse.success(
                            data = orderController.updateOrder(
                                userId = principal().userId,
                                orderId = orderId,
                                orderStatus = OrderStatus.RECEIVED,
                            ),
                            statsCode = HttpStatusCode.OK,
                        )
                    )
                }
        }

        authenticateWithJwt(RoleManagement.SELLER.role) {
            route("/confirm")
                .put<OrderId, Response, Unit, JwtTokenBody> { orderId, _ ->
                    orderId.validation()

                    respond(
                        ApiResponse.success(
                            data = orderController.updateOrder(
                                userId = principal().userId,
                                orderId = orderId,
                                orderStatus = OrderStatus.CONFIRMED,
                            ),
                            statsCode = HttpStatusCode.OK,
                        )
                    )
                }

            route("/deliver")
                .put<OrderId, Response, Unit, JwtTokenBody> { orderId, _ ->
                    orderId.validation()

                    respond(
                        ApiResponse.success(
                            data = orderController.updateOrder(
                                userId = principal().userId,
                                orderId = orderId,
                                orderStatus = OrderStatus.DELIVERED,
                            ),
                            statsCode = HttpStatusCode.OK,
                        )
                    )
                }
        }
    }
}
