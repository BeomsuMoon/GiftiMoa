plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.giftimoa"
    compileSdk = 33

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }


    defaultConfig {
        applicationId = "com.example.giftimoa"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {
    implementation ("me.relex:circleindicator:2.1.6")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.code.gson:gson:2.8.8")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")

    //갤러리에서 이미지 가져올때 필요한 API
    implementation("com.github.bumptech.glide:glide:4.16.0")

    //갤러리 사진 가져오는 라이브러리
    implementation("com.github.bumptech.glide:glide:4.16.0")

    //Java 개체를 JSON 표현으로 변환하는 데 사용할 수 있는 Java 라이브러리
    implementation ("com.google.code.gson:gson:2.10.1")

    //뷰모델
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0")

    implementation ("com.rmtheis:tess-two:9.1.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

}