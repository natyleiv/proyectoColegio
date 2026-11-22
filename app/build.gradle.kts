plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kover)
}

android {
    namespace = "cl.duoc.bitacora"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "cl.duoc.bitacora"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }

    // Pruebas unitarias: si el código llama a alguna función de Android (que no existe en la JVM),
    // devuelve un valor por defecto en vez de fallar. Necesario para probar ViewModels.
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Navegación entre pantallas (clase 03)
    implementation(libs.androidx.navigation.compose)
    // Íconos de Material (clase 03: flecha de volver, etc.)
    implementation(libs.androidx.compose.material.icons.extended)
    // Persistencia local con Room (clase 05)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    // Coil para mostrar imágenes desde una URI (clase 06)
    implementation("io.coil-kt.coil3:coil-compose:3.3.0")
    // Retrofit para conectarse a los microservicios (EP4)
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")

    // Pruebas unitarias: MockK para simular la API y los DAO,
    // y coroutines-test para ejecutar funciones suspend dentro de las pruebas (runTest)
    testImplementation("io.mockk:mockk:1.14.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
}
// Kover: mide qué parte de la lógica ejecutan las pruebas unitarias.
// Se incluyen las clases con lógica de negocio (validaciones, fechas, conversión de datos,
// manejo de respuestas y repositorios). Las pantallas, el tema y la navegación son
// presentación visual y no cuentan para la meta del 80 % (guía del profesor, sección 4).
kover {
    reports {
        // Igual que el ejemplo del profesor: koverVerify falla si la cobertura baja del 80 %
        verify {
            rule("Cobertura mínima del proyecto") {
                minBound(80)
            }
        }
        filters {
            includes {
                classes(
                    "cl.duoc.bitacora.util.ValidacionesKt",
                    "cl.duoc.bitacora.util.FechasKt",
                    "cl.duoc.bitacora.data.remote.MapeoRemoto",
                    "cl.duoc.bitacora.data.remote.RespuestasKt",
                    "cl.duoc.bitacora.data.remote.ErrorServidor",
                    "cl.duoc.bitacora.data.UsuarioRepository*",
                    "cl.duoc.bitacora.data.BitacoraRepository*",
                    "cl.duoc.bitacora.data.RegistroRepository*",
                    "cl.duoc.bitacora.ui.login.LoginViewModel*"
                )
            }
        }
    }
}
// MockK usa Byte Buddy para crear los objetos falsos. Android Studio ejecuta las pruebas
// con Java 25, más nuevo que lo que Byte Buddy soporta oficialmente: este modo lo habilita.
tasks.withType<Test>().configureEach {
    jvmArgs("-Dnet.bytebuddy.experimental=true")
}
