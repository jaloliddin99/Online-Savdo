package uz.promo.selling.data.remote.models.reverse

data class GeoObject(
    val Point: Point,
    val boundedBy: BoundedBy,
    val description: String,
    val metaDataProperty: MetaDataProperty,
    val name: String,
    val uri: String
)