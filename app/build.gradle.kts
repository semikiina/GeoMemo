plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.services)
    alias(libs.plugins.google.secrets)
}

android {
    namespace = "com.example.notes"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.notes"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Jetpack Compose Dependencies
    implementation("androidx.compose.ui:ui:1.5.1") // UI-Komponenten für Jetpack Compose
    implementation("androidx.compose.material3:material3:1.3.1") // Material Design 3
    implementation("androidx.compose.foundation:foundation:1.5.1") // Für grundlegende Compose-Komponenten
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.1") // Für Vorschau von Composables
    implementation("androidx.compose.material:material-icons-core:1.5.1") // Material Icons
    implementation("androidx.compose.material:material-icons-extended:1.5.1") // Erweiterte Material Icons

    // Coil - Zum Laden von Bildern (z.B. für das Profilbild)
    implementation("io.coil-kt:coil-compose:2.3.0")
    implementation("io.coil-kt:coil-svg:2.3.0")


    // Navigation und Firebase
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.navigation.compose)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.navigation.runtime.ktx)


    // Test Dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Maps
    implementation(libs.google.maps.compose)
    implementation(libs.google.gms.play.services.location)
    implementation(libs.google.play.services.maps)
    implementation (libs.play.services.basement)
    implementation (libs.places)

}
