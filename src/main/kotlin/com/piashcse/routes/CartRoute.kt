package com.piashcse.routes

import com.papsign.ktor.openapigen.route.path.auth.delete
import com.papsign.ktor.openapigen.route.path.auth.get
import com.papsign.ktor.openapigen.route.path.auth.post
import com.papsign.ktor.openapigen.route.path.auth.principal
import com.papsign.ktor.openapigen.route.path.auth.put
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.piashcse.controllers.CartController
import com.piashcse.models.PagingData
import com.piashcse.models.cart.AddCart
import com.piashcse.models.cart.DeleteProduct
import com.piashcse.models.cart.UpdateCart
import com.piashcse.models.user.body.JwtTokenBody
import com.piashcse.plugins.RoleManagement
import com.piashcse.utils.ApiResponse
import com.piashcse.utils.Response
import com.piashcse.utils.authenticateWithJwt
import io.ktor.http.HttpStatusCode

fun NormalOpenAPIRoute.cartRoute(cartController: CartController) {
    route("cart") {
        authenticateWithJwt(RoleManagement.CUSTOMER.role) {
            get<PagingData, Response, JwtTokenBody> { pagingData ->
                pagingData.validation()

                respond(
                    ApiResponse.success(
                        data = cartController.getCartItems(
                            userId = principal().userId,
                            pagingData = pagingData,
                        ),
                        statsCode = HttpStatusCode.OK,
                    )
                )
            }

            post<AddCart, Response, Unit, JwtTokenBody> { addCart, _ ->
                addCart.validation()

                respond(
                    ApiResponse.success(
                        data = cartController.addToCart(
                            userId = principal().userId,
                            addCart = addCart,
                        ),
                        statsCode = HttpStatusCode.Created,
                    )
                )
            }

            route("/{productId}")
                .delete<DeleteProduct, Response, JwtTokenBody> { deleteProduct ->
                    deleteProduct.validation()

                    respond(
                        ApiResponse.success(
                            data = cartController.removeCartItem(
                                userId = principal().userId,
                                deleteProduct = deleteProduct,
                            ),
                            statsCode = HttpStatusCode.OK,
                        )
                    )
                }

            route("/{productId}")
                .put<UpdateCart, Response, Unit, JwtTokenBody> { updateCart, _ ->
                    updateCart.validation()

                    respond(
                        ApiResponse.success(
                            data = cartController.updateCartQuantity(
                                userId = principal().userId,
                                updateCart = updateCart,
                            ),
                            statsCode = HttpStatusCode.OK,
                        )
                    )
                }

            route("/all")
                .delete<Unit, Response, JwtTokenBody> { _ ->
                    respond(
                        ApiResponse.success(
                            data = cartController.deleteAllFromCart(
                                userId = principal().userId,
                            ),
                            statsCode = HttpStatusCode.NoContent,
                        )
                    )
                }
        }
    }
}
