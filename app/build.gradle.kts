import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
}

// Llaves públicas de Supabase: viven en local.properties, que no se sube a GitHub (RN-20).
val propiedadesLocales = Properties().apply {
    val archivo = rootProject.file("local.properties")
    if (archivo.exists()) archivo.inputStream().use { load(it) }
}

android {
    namespace = "com.univalle.clasificados"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.univalle.clasificados"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Usuario de prueba de HU-03 (lo crea Oscar en Firebase). Viene de local.properties, que no
        // se sube a GitHub, y solo lo recibe la prueba SesionPersistenteTest; no va dentro de la app.
        // Solo se envían si existen: Android rechaza argumentos vacíos al lanzar las pruebas.
        propiedadesLocales.getProperty("PRUEBA_CORREO")?.takeIf { it.isNotBlank() }?.let {
            testInstrumentationRunnerArguments["correoPrueba"] = it
        }
        propiedadesLocales.getProperty("PRUEBA_CONTRASENA")?.takeIf { it.isNotBlank() }?.let {
            testInstrumentationRunnerArguments["contrasenaPrueba"] = it
        }

        buildConfigField("String", "SUPABASE_URL", "\"${propiedadesLocales.getProperty("SUPABASE_URL", "")}\"")
        buildConfigField("String", "SUPABASE_KEY", "\"${propiedadesLocales.getProperty("SUPABASE_KEY", "")}\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)

    // Firebase: Authentication y Firestore
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.kotlinx.coroutines.play.services)

    // Supabase: Storage para las fotos
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.storage)
    implementation(libs.ktor.client.okhttp)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    // Espresso 3.7 funciona con Android 16+; la versión que trae Compose (3.5) falla en esos emuladores.
    androidTestImplementation(libs.androidx.test.espresso)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
