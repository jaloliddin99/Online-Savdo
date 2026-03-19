plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
}

android {
    compileSdk = 36

    defaultConfig {
        namespace = "org.don.onlineTrade"
        applicationId = "org.don.onlineTrade"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {

        debug {
            buildConfigField("String", "BASE_URL", "\"http://95.169.201.165:8080/api/v1/\"")
        }


        release {
            buildConfigField("String", "BASE_URL", "\"http://95.169.201.165:8080/api/v1/\"")
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

    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
    implementation("androidx.activity:activity-ktx:1.12.4")


    implementation(platform("androidx.compose:compose-bom:2026.02.01"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material-icons-extended")
//    implementation("androidx.compose.material:material-icons-core")
//    implementation("androidx.compose.material3:material3-window-size-class")

    implementation("androidx.activity:activity-compose:1.12.4")
//    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
//    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.10.0")


    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.1")

    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    testImplementation("junit:junit:4.13.2")


    val nav_version = "2.9.7"

    implementation("androidx.navigation:navigation-compose:$nav_version")
    implementation("androidx.compose.runtime:runtime-tracing:1.10.4")

    implementation("com.google.dagger:hilt-android:2.59.2")
    ksp("com.google.dagger:hilt-android-compiler:2.59.2")
    ksp("androidx.hilt:hilt-compiler:1.3.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")

    implementation("com.chibatching.kotpref:kotpref:2.13.2")
    implementation("com.google.code.gson:gson:2.13.2")

    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")

    debugImplementation("com.github.chuckerteam.chucker:library:4.3.0")
    releaseImplementation("com.github.chuckerteam.chucker:library-no-op:4.3.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.3.2")

    implementation("com.karumi:dexter:6.2.3")
    implementation("io.coil-kt:coil-compose:2.7.0")

    implementation("id.zelory:compressor:3.0.1")
    implementation("com.airbnb.android:lottie-compose:6.7.1")
    implementation("com.github.ozcanalasalvar:otpview:2.0.1")
    implementation("com.googlecode.libphonenumber:libphonenumber:9.0.24")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.maps.android:maps-compose:8.2.0")
    implementation("com.google.android.gms:play-services-maps:20.0.0")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.0")

    implementation(platform("com.google.firebase:firebase-bom:34.10.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging-ktx:24.1.2")

    implementation("androidx.work:work-runtime-ktx:2.10.1")
    implementation("androidx.hilt:hilt-work:1.3.0")



}