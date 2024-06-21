package com.piashcse.controllers

import com.piashcse.dbhelper.query
import com.piashcse.entities.orders.CartItemEntity
import com.piashcse.entities.orders.CartItemTable
import com.piashcse.entities.orders.OrderCreatedPayload
import com.piashcse.entities.orders.OrderEntity
import com.piashcse.entities.orders.OrderItemEntity
import com.piashcse.entities.orders.OrderItemTable
import com.piashcse.entities.orders.OrderPayload
import com.piashcse.entities.orders.OrdersTable
import com.piashcse.models.PagingData
import com.piashcse.models.order.AddOrder
import com.piashcse.models.order.OrderId
import com.piashcse.utils.extensions.OrderStatus
import com.piashcse.utils.extensions.isNotExistException
import com.piashcse.utils.extensions.orderStatusCode
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and

class OrderController {

    suspend fun createOrder(
        userId: String,
        addOrder: AddOrder,
    ): OrderCreatedPayload = query {
        // create order
        val order = OrderEntity.new {
            this.userId = EntityID(userId, OrdersTable)
            this.quantity = addOrder.quantity
            this.shippingCharge = addOrder.shippingCharge
            this.subTotal = addOrder.subTotal
            this.total = addOrder.total
        }

        // create order_items
        addOrder.orderItems.forEach { orderItem ->
            OrderItemEntity.new {
                orderId = EntityID(order.id.value, OrderItemTable)
                productId = EntityID(orderItem.productId, OrderItemTable)
                quantity = orderItem.quantity
            }
        }

        // delete the products from the cart when creating a new order
        addOrder.orderItems.forEach {
            val productExist = CartItemEntity
                .find { (CartItemTable.userId eq userId) and (CartItemTable.productId eq it.productId) }
                .toList()
                .singleOrNull()

            productExist?.delete()
        }

        // order id is returned
        order.orderCreatedResponse()
    }

    suspend fun getOrders(
        userId: String,
        pagingData: PagingData,
    ): List<OrderPayload> = query {
        OrderEntity.find { OrdersTable.userId eq userId }
            .limit(pagingData.limit, pagingData.offset)
            .map { orderEntity ->
                orderEntity.response()
            }
    }

    suspend fun updateOrder(
        userId: String,
        orderId: OrderId,
        orderStatus: OrderStatus,
    ) = query {
        val orderExist = OrderEntity
            .find { (OrdersTable.userId eq userId) and (OrdersTable.id eq orderId.orderId) }
            .toList()
            .singleOrNull()

        orderExist?.let {
            it.status = orderStatus.name.lowercase()
            it.statusCode = orderStatus.name.lowercase().orderStatusCode()
            it.response()
        } ?: "".isNotExistException()
    }
}
