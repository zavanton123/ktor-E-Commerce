package com.piashcse.plugins

import com.papsign.ktor.openapigen.APITag
import com.papsign.ktor.openapigen.openAPIGen
import com.papsign.ktor.openapigen.route.apiRouting
import com.papsign.ktor.openapigen.route.tag
import com.piashcse.controllers.BrandController
import com.piashcse.controllers.CartController
import com.piashcse.controllers.OrderController
import com.piashcse.controllers.ProductCategoryController
import com.piashcse.controllers.ProductController
import com.piashcse.controllers.ProductSubCategoryController
import com.piashcse.controllers.ProfileController
import com.piashcse.controllers.ShippingController
import com.piashcse.controllers.ShopController
import com.piashcse.controllers.UserController
import com.piashcse.controllers.WishListController
import com.piashcse.routes.brandRoute
import com.piashcse.routes.cartRoute
import com.piashcse.routes.orderRoute
import com.piashcse.routes.productCategoryRoute
import com.piashcse.routes.productRoute
import com.piashcse.routes.productSubCategoryRoute
import com.piashcse.routes.profileRouting
import com.piashcse.routes.shippingRoute
import com.piashcse.routes.shopRoute
import com.piashcse.routes.userRoute
import com.piashcse.routes.wishListRoute
import io.ktor.server.application.Application
import io.ktor.server.application.application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get

fun Application.configureRouting() {
    install(Routing) {
        // open api json loading
        get("/openapi.json") {
            call.respond(application.openAPIGen.api.serialize())
        }

        get("/") {
            call.respondRedirect("/swagger-ui/index.html?url=/openapi.json", true)
        }

        // Api routing
        apiRouting {
            tag(Tags.USER) {
                userRoute(UserController())
            }
            tag(Tags.PROFILE) {
                profileRouting(ProfileController())
            }
            tag(Tags.SHOP) {
                shopRoute(ShopController())
            }
            tag(Tags.PRODUCT) {
                productRoute(ProductController())
            }
            tag(Tags.PRODUCT_CATEGORY) {
                productCategoryRoute(ProductCategoryController())
            }
            tag(Tags.PRODUCT_SUB_CATEGORY) {
                productSubCategoryRoute(ProductSubCategoryController())
            }
            tag(Tags.BRAND) {
                brandRoute(BrandController())
            }
            tag(Tags.WISHLIST) {
                wishListRoute(WishListController())
            }
            tag(Tags.CART) {
                cartRoute(CartController())
            }
            tag(Tags.ORDER) {
                orderRoute(OrderController())
            }
            tag(Tags.SHIPPING) {
                shippingRoute(ShippingController())
            }
        }
    }
}

enum class Tags(override val description: String) : APITag {
    USER(""),
    PROFILE(""),
    SHOP(""),
    PRODUCT(""),
    PRODUCT_CATEGORY(""),
    PRODUCT_SUB_CATEGORY(""),
    BRAND(""),
    CART(""),
    ORDER(""),
    WISHLIST(""),
    SHIPPING("")
}
