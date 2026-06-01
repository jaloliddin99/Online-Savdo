package uz.promo.selling.data.remote.models.reverse

data class Country(
    val AddressLine: String,
    val AdministrativeArea: AdministrativeArea,
    val CountryName: String,
    val CountryNameCode: String
)