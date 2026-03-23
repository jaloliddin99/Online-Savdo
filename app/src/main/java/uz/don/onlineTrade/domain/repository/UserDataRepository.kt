package uz.don.onlineTrade.domain.repository

import kotlinx.coroutines.flow.Flow
import uz.don.onlineTrade.domain.model.DarkThemeConfig
import uz.don.onlineTrade.domain.model.ThemeBrand
import uz.don.onlineTrade.domain.model.UserData

interface UserDataRepository {


    /**
     * Stream of [UserData]
     */
    val userData: Flow<UserData>


    /**
     * Sets the desired theme brand.
     */
    suspend fun setThemeBrand(themeBrand: ThemeBrand)

    /**
     * Sets the desired dark theme config.
     */
    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig)

    /**
     * Sets the preferred dynamic color config.
     */
    suspend fun setDynamicColorPreference(useDynamicColor: Boolean)



}