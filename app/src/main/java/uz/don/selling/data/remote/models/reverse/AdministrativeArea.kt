package uz.don.selling.data.remote.models.reverse

data class AdministrativeArea(
    val AdministrativeAreaName: String,
    val Locality: Locality,
    val SubAdministrativeArea: SubAdministrativeArea
)