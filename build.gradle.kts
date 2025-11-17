import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort

plugins {
    id("java")
    id("com.github.spotbugs") version "6.0.25"
    checkstyle
    application
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(11)
    }
}
repositories {
    mavenCentral()
}

application {
    mainClass = "ui.Main"
}

tasks.compileJava {
    options.release = 11
}

tasks.test {
    useJUnitPlatform()
}

group = "nu.csse.sqe"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.puppycrawl.tools:checkstyle:10.18.2")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    spotbugs("com.github.spotbugs:spotbugs:4.8.6")
    spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.13.0")
    testImplementation("org.easymock:easymock:5.4.0")
    // cucumber
    testImplementation(platform("io.cucumber:cucumber-bom:7.20.1"))
    testImplementation("io.cucumber:cucumber-java")
    testImplementation("io.cucumber:cucumber-junit-platform-engine")
    testImplementation("io.cucumber:cucumber-picocontainer:7.20.1")
}
spotbugs {
    ignoreFailures = false
    showStackTraces = true
    showProgress = true
    effort = Effort.DEFAULT
    reportLevel = Confidence.DEFAULT
//    visitors = listOf("FindSqlInjection", "SwitchFallthrough")
//    omitVisitors = listOf("FindNonShortCircuit")
//    reportsDir = file("$buildDir/spotbugs")
//    onlyAnalyze = listOf("com.foobar.MyClass", "com.foobar.mypkg.*")
    maxHeapSize = "1g"
    extraArgs = listOf("-nested:false")
    excludeFilter.set(file("config/spotbugs/excludeFilter.xml"))
//    jvmArgs = listOf("-Duser.language=ja")
}
checkstyle {
    toolVersion = "10.18.2"
    isIgnoreFailures = false
}

tasks.spotbugsMain {
    reports.create("html") {
        required = true
        outputLocation = file("build/reports/spotbugs/spotbugs_main.html")
        setStylesheet("fancy-hist.xsl")
    }
}
tasks.spotbugsTest {
    reports.create("html") {
        required = true
        outputLocation = file("build/reports/spotbugs/spotbugs_test.html")
        setStylesheet("fancy-hist.xsl")
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(11)
    }
}
tasks.withType<Checkstyle>().configureEach {
    reports {
        xml.required = false
        html.required = true
        html.stylesheet = resources.text.fromFile("config/xsl/checkstyle-noframes-severity-sorted.xsl")
    }
}