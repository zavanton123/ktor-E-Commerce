package com.piashcse.controllers

import com.piashcse.dbhelper.query
import com.piashcse.entities.orders.Cart
import com.piashcse.entities.orders.CartItemEntity
import com.piashcse.entities.orders.CartItemTable
import com.piashcse.entities.product.Product
import com.piashcse.entities.product.ProductEntity
import com.piashcse.entities.product.ProductTable
import com.piashcse.models.PagingData
import com.piashcse.models.cart.AddCart
import com.piashcse.models.cart.DeleteProduct
import com.piashcse.models.cart.UpdateCart
import com.piashcse.utils.extensions.alreadyExistException
import com.piashcse.utils.extensions.isNotExistException
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and

class CartController {

    suspend fun addToCart(
        userId: String,
        addCart: AddCart,
    ) = query {
        val isProductExist = CartItemEntity
            .find { (CartItemTable.userId eq userId) and (CartItemTable.productId eq addCart.productId) }
            .toList()
            .singleOrNull()

        isProductExist?.let {
            addCart.productId.alreadyExistException()
        } ?: CartItemEntity.new {
            this.userId = EntityID(userId, CartItemTable)
            productId = EntityID(addCart.productId, CartItemTable)
            quantity = addCart.quantity
        }.cartResponse()
    }

    suspend fun getCartItems(
        userId: String,
        pagingData: PagingData,
    ): List<Cart> = query {
        CartItemEntity
            .find { CartItemTable.userId eq userId }
            .limit(pagingData.limit, pagingData.offset)
            .map { cartItemEntity ->
                cartItemEntity.cartResponse(
                    product = ProductEntity
                        .find { ProductTable.id eq cartItemEntity.productId }
                        .first()
                        .response()
                )
            }
    }

    suspend fun updateCartQuantity(
        userId: String,
        updateCart: UpdateCart,
    ): Cart? = query {
        val cartItemEntity = CartItemEntity
            .find { (CartItemTable.userId eq userId) and (CartItemTable.productId eq updateCart.productId) }
            .toList()
            .singleOrNull()

        cartItemEntity?.let { entity ->
            entity.quantity = entity.quantity + updateCart.quantity

            entity.cartResponse(
                product = ProductEntity
                    .find { ProductTable.id eq entity.productId }
                    .first()
                    .response()
            )
        }
    }

    suspend fun removeCartItem(
        userId: String,
        deleteProduct: DeleteProduct,
    ): Product? = query {
        val cartItemEntity = CartItemEntity
            .find { (CartItemTable.userId eq userId) and (CartItemTable.productId eq deleteProduct.productId) }
            .toList()
            .singleOrNull()

        cartItemEntity?.let { entity ->
            entity.delete()

            ProductEntity.find { ProductTable.id eq entity.productId }
                .first()
                .response()
        }
    }

    suspend fun deleteAllFromCart(
        userId: String,
    ) = query {
        val cartItemEntities = CartItemEntity
            .find { CartItemTable.userId eq userId }
            .toList()

        if (cartItemEntities.isEmpty()) {
            "".isNotExistException()
        } else {
            cartItemEntities.forEach { cartItemEntity ->
                cartItemEntity.delete()
            }
            true
        }
    }
}