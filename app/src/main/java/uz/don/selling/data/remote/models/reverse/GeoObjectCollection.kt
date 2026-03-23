package uz.don.selling.data.remote.models.reverse

data class GeoObjectCollection(
    val featureMember: List<FeatureMember>,
    val metaDataProperty: MetaDataPropertyX
)