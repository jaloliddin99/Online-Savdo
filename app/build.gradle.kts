plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
}

android {
    compileSdk = 37

    defaultConfig {
        namespace = "uz.promo.selling"
        applicationId = "uz.promo.selling"
        minSdk = 24
        targetSdk = 36
        versionCode = 12
        versionName = "1.2.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {

        debug {
            buildConfigField("String", "BASE_URL", "\"https://selling.uz/api/v1/\"")
        }


        release {
            buildConfigField("String", "BASE_URL", "\"https://selling.uz/api/v1/\"")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding  = true
        dataBinding  = true
        buildConfig  = true
        compose      = true
    }



    bundle {
        language {
            enableSplit = false
        }
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {

    implementation("androidx.core:core-ktx:1.19.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.11.0")
    implementation("androidx.activity:activity-ktx:1.13.0")


    implementation(platform("androidx.compose:compose-bom:2026.06.01"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material-icons-extended")
    // Real backdrop blur ("liquid glass") for the floating bottom bar.
    implementation("dev.chrisbanes.haze:haze:1.7.2")
//    implementation("androidx.compose.material:material-icons-core")
//    implementation("androidx.compose.material3:material3-window-size-class")

    implementation("androidx.activity:activity-compose:1.13.0")
//    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
//    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.11.0")


    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.1")

    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    testImplementation("junit:junit:4.13.2")


    implementation("androidx.work:work-runtime-ktx:2.11.2")
    implementation("androidx.paging:paging-runtime-ktx:3.5.0")
    implementation("androidx.paging:paging-compose:3.5.0")

    implementation("androidx.room:room-runtime:2.8.4")
    implementation("androidx.room:room-ktx:2.8.4")
    ksp("androidx.room:room-compiler:2.8.4")

    implementation("androidx.navigation:navigation-compose:2.9.8")
    implementation("androidx.compose.runtime:runtime-tracing:1.11.4")

    implementation("com.google.dagger:hilt-android:2.60")
    ksp("com.google.dagger:hilt-android-compiler:2.60")
    ksp("androidx.hilt:hilt-compiler:1.4.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.4.0")

    implementation("com.chibatching.kotpref:kotpref:2.13.2")
    implementation("com.google.code.gson:gson:2.14.0")

    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")

    debugImplementation("com.github.chuckerteam.chucker:library:4.3.1")
    releaseImplementation("com.github.chuckerteam.chucker:library-no-op:4.3.1")
    implementation("com.squareup.okhttp3:logging-interceptor:5.4.0")

    implementation("com.karumi:dexter:6.2.3")
    implementation("io.coil-kt:coil-compose:2.7.0")

    implementation("id.zelory:compressor:3.0.1")
    implementation("com.airbnb.android:lottie-compose:6.7.1")
    implementation("com.github.ozcanalasalvar:otpview:2.0.1")
    implementation("com.googlecode.libphonenumber:libphonenumber:9.0.33")
    implementation("com.google.android.gms:play-services-location:21.4.0")
    implementation("com.google.maps.android:maps-compose:8.3.0")
    // Marker clustering for the search map.
    implementation("com.google.maps.android:maps-compose-utils:8.3.0")
    implementation("com.google.android.gms:play-services-maps:20.0.0")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.0")

    implementation(platform("com.google.firebase:firebase-bom:34.15.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging-ktx:24.1.2")

    implementation("androidx.credentials:credentials:1.6.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.6.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.2.0")

    implementation("androidx.hilt:hilt-work:1.4.0")

    // CameraX — custom in-app camera for the AI posting flow.
    implementation("androidx.camera:camera-camera2:1.6.1")
    implementation("androidx.camera:camera-lifecycle:1.6.1")
    implementation("androidx.camera:camera-view:1.6.1")

}