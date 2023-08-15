package org.don.bottomappbar.domain.repository

import kotlinx.coroutines.flow.Flow
import org.don.bottomappbar.domain.model.UserData

interface UserDataRepository {


    /**
     * Stream of [UserData]
     */
    val userData: Flow<UserData>



}