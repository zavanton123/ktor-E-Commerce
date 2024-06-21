package com.piashcse.dbhelper

import com.piashcse.entities.ShippingTable
import com.piashcse.entities.orders.CartItemTable
import com.piashcse.entities.orders.OrderItemTable
import com.piashcse.entities.orders.OrdersTable
import com.piashcse.entities.product.BrandTable
import com.piashcse.entities.product.ProductImageTable
import com.piashcse.entities.product.ProductTable
import com.piashcse.entities.product.WishListTable
import com.piashcse.entities.product.category.ProductCategoryTable
import com.piashcse.entities.product.category.ProductSubCategoryTable
import com.piashcse.entities.shop.ShopCategoryTable
import com.piashcse.entities.shop.ShopTable
import com.piashcse.entities.user.UserProfileTable
import com.piashcse.entities.user.UserTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DatabaseFactory {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun init() {
        initDB()

        transaction {
            addLogger(StdOutSqlLogger)

            create(
                UserTable,
                UserProfileTable,
                ShopTable,
                ShopCategoryTable,
                ProductTable,
                ProductImageTable,
                ProductCategoryTable,
                ProductSubCategoryTable,
                BrandTable,
                CartItemTable,
                OrdersTable,
                OrderItemTable,
                WishListTable,
                ShippingTable
            )
        }
    }

    private fun initDB() {
        // database connection is handled from hikari properties
        val config = HikariConfig("/hikari.properties")
        val dataSource = HikariDataSource(config)

        //// or
        // val dataSource = hikari()

        runFlyway(dataSource)

        Database.connect(dataSource)
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = System.getenv("JDBC_DRIVER")
        config.jdbcUrl = System.getenv("HEROKU_POSTGRESQL_NAVY_URL")
        config.maximumPoolSize = 3
        config.isAutoCommit = true
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
    }

    private fun runFlyway(datasource: DataSource) {
        val flyway = Flyway.configure().dataSource(datasource).load()
        try {
            flyway.info()
            flyway.migrate()
        } catch (e: Exception) {
            log.error("Exception running flyway migration", e)
            throw e
        }
        log.info("Flyway migration has finished")
    }
}

suspend fun <T> query(block: () -> T): T = withContext(Dispatchers.IO) {
    transaction {
        block()
    }
}
