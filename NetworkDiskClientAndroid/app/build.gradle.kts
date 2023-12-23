plugins {
  id("com.android.application")
  id("kotlin-android")
  id("kotlin-parcelize")
  id("kotlin-kapt")
  id("dagger.hilt.android.plugin")
}

android {
  compileSdk = libs.versions.compileSdk.get().toInt()
  buildFeatures {
    dataBinding = true
    aidl = true
  }
  defaultConfig {
    applicationId = "com.hld.networkdisk.client"
    minSdk = libs.versions.minSdk.get().toInt()
    targetSdk = libs.versions.targetSdk.get().toInt()
    versionCode = 1
    versionName = "0.1.6"
    vectorDrawables.useSupportLibrary = true

    // Consult the README on instructions for setting up Unsplash API key
    buildConfigField("String", "UNSPLASH_ACCESS_KEY", "\"" + getUnsplashAccess() + "\"")
//      externalNativeBuild {
//          cmake {
//              cppFlags += ""
//          }
//      }
      javaCompileOptions {
      annotationProcessorOptions {
        arguments["dagger.hilt.disableModulesHaveInstallInCheck"] = "true"
      }
    }
  }
  buildTypes {
    release {
      isMinifyEnabled = true
      proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
    }
    create("benchmark") {
      initWith(getByName("release"))
      signingConfig = signingConfigs.getByName("debug")
      isDebuggable = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules-benchmark.pro"
      )
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  kotlinOptions {
    // work-runtime-ktx 2.1.0 and above now requires Java 8
    jvmTarget = "1.8"

    // Enable Coroutines and Flow APIs
    freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
    freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlinx.coroutines.FlowPreview"
  }
  buildFeatures {
    compose = true
    dataBinding = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
  }
  packagingOptions {
    // Multiple dependency bring these files in. Exclude them to enable
    // our test APK to build (has no effect on our AARs)
    resources.excludes += "/META-INF/AL2.0"
    resources.excludes += "/META-INF/LGPL2.1"
  }

  testOptions {
    managedDevices {
      devices {
        maybeCreate<com.android.build.api.dsl.ManagedVirtualDevice>("pixel2api27").apply {
          device = "Pixel 2"
          apiLevel = 27
          systemImageSource = "aosp"
        }
      }
    }
  }
//    externalNativeBuild {
//        cmake {
//            path = file("src/main/cpp/CMakeLists.txt")
//            version = "3.22.1"
//        }
//    }
}

androidComponents {
  onVariants(selector().withBuildType("release")) {
    // Only exclude *.version files in release mode as debug mode requires
    // these files for layout inspector to work.
    it.packaging.resources.excludes.add("META-INF/*.version")
  }
}

dependencies {
  kapt(libs.androidx.room.compiler)
  kapt(libs.hilt.android.compiler)
  implementation(libs.koin.core)
  implementation(libs.koin.compat)
  implementation(libs.koin.android)
  implementation(libs.reactivex.rxjava3.rxjava)
  implementation(libs.reactivex.rxjava3.rxandroid)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.constraintlayout)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.livedata.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.ktx)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.navigation.fragment)
  implementation(libs.androidx.navigation.ui)
  implementation(libs.androidx.paging.compose)
  implementation(libs.androidx.paging.runtime.ktx)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.work.runtime.ktx)
  implementation(libs.material)
  implementation(libs.gson)
  implementation(libs.okhttp3.logging.interceptor)
  implementation(libs.retrofit2.converter.gson)
  implementation(libs.retrofit2)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.hilt.android)
  implementation(libs.hilt.navigation.compose)
  implementation(libs.androidx.profileinstaller)
  implementation(libs.androidx.tracing.ktx)

  // Compose
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.accompanist.themeadapter.material)
  implementation(libs.accompanist.systemuicontroller)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.constraintlayout.compose)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.foundation.layout)
  implementation(libs.androidx.compose.material)
  implementation(libs.androidx.compose.ui.viewbinding)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.runtime.livedata)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.glide)
  implementation("com.github.gzu-liyujiang.AndroidPicker:FilePicker:4.1.13")
  debugImplementation(libs.androidx.compose.ui.tooling)

  // Testing dependencies
  debugImplementation(libs.androidx.monitor)
  kaptAndroidTest(libs.hilt.android.compiler)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.arch.core.testing)
  androidTestImplementation(libs.androidx.espresso.contrib)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.espresso.intents)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.uiautomator)
  androidTestImplementation(libs.androidx.work.testing)
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.guava)
  androidTestImplementation(libs.hilt.android.testing)
  androidTestImplementation(libs.accessibility.test.framework)
  androidTestImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.junit)
}

fun getUnsplashAccess(): String? {
  return project.findProperty("unsplash_access_key") as? String
}
