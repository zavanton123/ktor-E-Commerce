package com.piashcse.controllers

import com.piashcse.dbhelper.query
import com.piashcse.entities.Shipping
import com.piashcse.entities.ShippingEntity
import com.piashcse.entities.ShippingTable
import com.piashcse.entities.orders.OrdersTable
import com.piashcse.entities.user.UserTable
import com.piashcse.models.shipping.AddShipping
import com.piashcse.models.shipping.UpdateShipping
import com.piashcse.utils.CommonException
import com.piashcse.utils.extensions.alreadyExistException
import com.piashcse.utils.extensions.isNotExistException
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and

class ShippingController {

    suspend fun addShipping(
        userId: String,
        addShipping: AddShipping,
    ): Shipping = query {
        val shippingEntity = ShippingEntity
            .find { (UserTable.id eq userId) and (OrdersTable.id eq addShipping.orderId) }
            .toList()
            .singleOrNull()

        shippingEntity?.let {
            addShipping.orderId.alreadyExistException()
        }

        ShippingEntity.new {
            this.userId = EntityID(userId, ShippingTable)
            this.orderId = EntityID(addShipping.orderId, ShippingTable)
            shippingAddress = addShipping.shipAddress
            shippingCity = addShipping.shipCity
            shippingPhone = addShipping.shipPhone
            shippingName = addShipping.shipName
            shippingEmail = addShipping.shipEmail
            shippingCountry = addShipping.shipCountry
        }.response()
    }

    suspend fun getShipping(
        userId: String,
        orderId: String,
    ): Shipping = query {
        val shippingEntity = ShippingEntity
            .find { UserTable.id eq userId and (OrdersTable.id eq orderId) }
            .toList()
            .singleOrNull()

        shippingEntity?.let { entity ->
            entity.response()
        } ?: throw CommonException("$orderId is not Exist")
    }

    suspend fun updateShipping(
        userId: String,
        updateShipping: UpdateShipping,
    ): Shipping = query {
        val shippingEntity = ShippingEntity
            .find { (UserTable.id eq userId) and (OrdersTable.id eq updateShipping.orderId) }
            .toList()
            .singleOrNull()

        if (shippingEntity == null) {
            throw CommonException("${updateShipping.orderId} is not Exist")
        }

        shippingEntity.let {
            it.shippingAddress = updateShipping.shipAddress ?: it.shippingAddress
            it.shippingCity = updateShipping.shipCity ?: it.shippingCity
            it.shippingPhone = updateShipping.shipPhone ?: it.shippingPhone
            it.shippingName = updateShipping.shipName ?: it.shippingName
            it.shippingEmail = updateShipping.shipEmail ?: it.shippingEmail
            it.shippingCountry = updateShipping.shipCountry ?: it.shippingCountry
        }

        shippingEntity.response()
    }

    suspend fun deleteShipping(userId: String, orderId: String) = query {
        val isExist = ShippingEntity
            .find { UserTable.id eq userId and (OrdersTable.id eq orderId) }
            .toList()
            .singleOrNull()

        isExist?.delete() ?: orderId.isNotExistException()
    }
}
