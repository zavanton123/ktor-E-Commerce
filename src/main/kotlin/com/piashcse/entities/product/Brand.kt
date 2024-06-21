package com.piashcse.entities.product

import com.piashcse.entities.base.BaseIntEntity
import com.piashcse.entities.base.BaseIntEntityClass
import com.piashcse.entities.base.BaseIntIdTable
import org.jetbrains.exposed.dao.id.EntityID

object BrandsTable : BaseIntIdTable("brand") {
    val brandName = text("brand_name")
    val brandLogo = text("brand_log").nullable()
}

class BrandEntity(id: EntityID<String>) : BaseIntEntity(id, BrandsTable) {
    companion object : BaseIntEntityClass<BrandEntity>(BrandsTable)

    var brandName by BrandsTable.brandName
    var brandLogo by BrandsTable.brandLogo

    fun brandResponse() = Brand(
        id = id.value,
        brandName = brandName,
        brandLogo = brandLogo
    )
}

data class Brand(
    val id: String,
    val brandName: String,
    val brandLogo: String?,
)
