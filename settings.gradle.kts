rootProject.name = "oms"

include(
        "oms-db",
        "oms-common")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            name = "JCenter Gradle Plugins"
            url = uri("https://dl.bintray.com/gradle/gradle-plugins")
        }
        jcenter()
    }
}
