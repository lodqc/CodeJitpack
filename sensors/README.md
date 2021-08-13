# CodeJitpack
# 集成需要项目的gradle
buildscript {
repositories {
mavenCentral()
jcenter()
}
dependencies {
// 添加 gradle 3.2.0+ 依赖
classpath 'com.android.tools.build:gradle:3.5.3'
// 添加神策分析 android-gradle-plugin2 依赖
classpath 'com.sensorsdata.analytics.android:android-gradle-plugin2:3.3.7'
}
}
# 集成需要项目的app的gradle
apply plugin: 'com.android.application'
// 应用 com.sensorsdata.analytics.android 插件
apply plugin: 'com.sensorsdata.analytics.android'

SensorsHelper建议在首页初始化，需要弹出权限弹窗

