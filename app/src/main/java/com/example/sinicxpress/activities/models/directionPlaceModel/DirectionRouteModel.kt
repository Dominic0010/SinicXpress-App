package com.example.sinicxpress.activities.models.directionPlaceModel

import com.example.sinicxpress.activities.models.directionPlaceModel.DirectionLegModel
import com.example.sinicxpress.activities.models.directionPlaceModel.DirectionPolylineModel
import com.squareup.moshi.Json

data class DirectionRouteModel(
    @field:Json(name = "legs")

    var legs: List<DirectionLegModel>? = null,

    @field:Json(name = "overview_polyline")

    var polylineModel: DirectionPolylineModel? = null,

    @field:Json(name = "summary")

    var summary: String? = null
) {

}