package com.piashcse.models.cart

import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.annotations.parameters.QueryParam
import org.valiktor.functions.isBetween
import org.valiktor.functions.isNotEmpty
import org.valiktor.functions.isNotNull
import org.valiktor.functions.isNotZero
import org.valiktor.validate

data class UpdateCart(
    @PathParam("productId") val productId: String,
    @QueryParam("quantity") val quantity: Int,
) {
    fun validation() {
        validate(this) {
            validate(UpdateCart::productId).isNotNull().isNotEmpty()
            validate(UpdateCart::quantity).isNotZero().isBetween(-1, 1)
        }
    }
}
