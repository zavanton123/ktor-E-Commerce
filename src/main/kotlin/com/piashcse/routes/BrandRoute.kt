package com.piashcse.routes

import com.papsign.ktor.openapigen.route.path.auth.delete
import com.papsign.ktor.openapigen.route.path.auth.get
import com.papsign.ktor.openapigen.route.path.auth.post
import com.papsign.ktor.openapigen.route.path.auth.put
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.piashcse.controllers.BrandController
import com.piashcse.models.PagingData
import com.piashcse.models.bands.AddBrand
import com.piashcse.models.bands.DeleteBrand
import com.piashcse.models.bands.UpdateBrand
import com.piashcse.models.user.body.JwtTokenBody
import com.piashcse.plugins.RoleManagement
import com.piashcse.utils.ApiResponse
import com.piashcse.utils.Response
import com.piashcse.utils.authenticateWithJwt
import io.ktor.http.HttpStatusCode

fun NormalOpenAPIRoute.brandRoute(brandController: BrandController) {

    route("brand") {
        authenticateWithJwt(RoleManagement.CUSTOMER.role, RoleManagement.SELLER.role, RoleManagement.ADMIN.role) {
            get<PagingData, Response, JwtTokenBody> { pagingData: PagingData ->
                pagingData.validation()

                respond(ApiResponse.success(brandController.getBrand(pagingData), HttpStatusCode.OK))
            }
        }

        authenticateWithJwt(RoleManagement.ADMIN.role) {
            post<AddBrand, Response, Unit, JwtTokenBody> { addBrand: AddBrand, _ ->
                addBrand.validation()

                respond(ApiResponse.success(brandController.createBrand(addBrand), HttpStatusCode.Created))
            }

            put<UpdateBrand, Response, Unit, JwtTokenBody> { updateBrand, _ ->
                updateBrand.validation()

                respond(ApiResponse.success(brandController.updateBrand(updateBrand), HttpStatusCode.OK))
            }

            delete<DeleteBrand, Response, JwtTokenBody> { deleteBrand ->
                deleteBrand.validation()

                respond(ApiResponse.success(brandController.deleteBrand(deleteBrand), HttpStatusCode.NoContent))
            }
        }
    }
}
