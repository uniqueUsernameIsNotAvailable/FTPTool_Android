import com.android.build.api.dsl.Packaging

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.tyoma.testingzone"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.tyoma.testingzone"
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
        isCoreLibraryDesugaringEnabled = true
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
            excludes += "/META-INF/LICENSE"
            excludes += "/META-INF/DEPENDENCIES"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(files("src/main/java/com/tyoma/testingzone/libs/core/client/ftp4j-1.7.2.jar"))
    implementation(files("src/main/java/com/tyoma/testingzone/libs/core/server/bcprov-jdk15to18-165.jar"))
    implementation(files("src/main/java/com/tyoma/testingzone/libs/core/server/ftplet-api-1.1.1.jar"))
    implementation(files("src/main/java/com/tyoma/testingzone/libs/core/server/ftpserver-core-1.1.1.jar"))
    implementation(files("src/main/java/com/tyoma/testingzone/libs/core/server/log4j-1.2.17.jar"))
    implementation(files("src/main/java/com/tyoma/testingzone/libs/core/server/mina-core-2.0.16.jar"))
    implementation(files("src/main/java/com/tyoma/testingzone/libs/core/server/slf4j-api-1.7.21.jar"))
    implementation(files("src/main/java/com/tyoma/testingzone/libs/core/server/slf4j-log4j12-1.7.21.jar"))
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")

    coreLibraryDesugaring(libs.desugar.jdk.libs)

}