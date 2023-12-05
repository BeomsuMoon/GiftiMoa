pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        maven(url = "https://devrepo.kakao.com/nexus/repository/kakaomap-releases/")
        mavenCentral()
        jcenter()
        maven(url = "https://jitpack.io")
    }



}


rootProject.name = "GiftiMoa"
include(":app")
 