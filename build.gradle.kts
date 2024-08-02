import org.apache.commons.lang3.SystemUtils

        plugins {
            idea
            java
            id("gg.essential.loom") version "0.10.0.+"
            id("dev.architectury.architectury-pack200") version "0.1.3"
            id("com.github.johnrengelman.shadow") version "8.1.1"
        }

//Constants:

val baseGroup: String by project
val mcVersion: String by project
val version: String by project
val mixinGroup = "$baseGroup.mixin"
val modid: String by project

// Toolchains:
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

// Minecraft configuration:
loom {
    log4jConfigs.from(file("log4j2.xml"))
    launchConfigs {
        "client" {
            // If you don't want mixins, remove these lines
            property("mixin.debug", "true")
            arg("--tweakClass", "cc.polyfrost.oneconfig.loader.stage0.LaunchWrapperTweaker", "org.spongepowered.asm.launch.MixinTweaker")
        }
    }
    runConfigs {
        "client" {
            if (SystemUtils.IS_OS_MAC_OSX) {
                // This argument causes a crash on macOS
                vmArgs.remove("-XstartOnFirstThread")
            }
        }
        remove(getByName("server"))
    }
    forge {
        pack200Provider.set(dev.architectury.pack200.java.Pack200Adapter())
        // If you don't want mixins, remove this lines
        mixinConfig("mixins.$modid.json")
    }
    // If you don't want mixins, remove these lines
    mixin {
        defaultRefmapName.set("mixins.$modid.refmap.json")
    }
}

sourceSets.main {
    output.setResourcesDir(sourceSets.main.flatMap { it.java.classesDirectory })
}

// Dependencies:

repositories {
    mavenCentral()

    maven("https://repo.spongepowered.org/maven/")
    // If you don't want to log in with your real minecraft account, remove this line
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
    maven("https://repo.polyfrost.cc/releases")
}

val shadowImpl: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

dependencies {
    compileOnly("com.yworks:yguard:4.1.0")
    compileOnly("cc.polyfrost:oneconfig-1.8.9-forge:0.2.2-alpha+")
    shadowImpl("cc.polyfrost:oneconfig-wrapper-launchwrapper:1.0.0-beta+")


    shadowImpl("org.json:json:20240303")
    shadowImpl("com.google.code.gson:gson:2.10.1")

    compileOnly("org.spongepowered:mixin:0.7.11-SNAPSHOT")

    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    implementation("me.djtheredstoner:DevAuth-forge-legacy:1.1.0")


    minecraft("com.mojang:minecraft:1.8.9")
    mappings("de.oceanlabs.mcp:mcp_stable:22-1.8.9")
    forge("net.minecraftforge:forge:1.8.9-11.15.1.2318-1.8.9")

    shadowImpl("org.spongepowered:mixin:0.7.11-SNAPSHOT") {
        isTransitive = false
    }

    shadowImpl("net.dv8tion:JDA:5.0.0-beta.9") {
        exclude(module = "opus-java")
    }
    annotationProcessor("org.spongepowered:mixin:0.8.5-SNAPSHOT")
    runtimeOnly("me.djtheredstoner:DevAuth-forge-legacy:1.1.2")
    shadowImpl("net.java.dev.jna:jna:5.8.0")
//    shadowImpl("org.java-websocket:Java-WebSocket:1.5.2")
//    shadowImpl("org.bouncycastle:bcprov-jdk15on:1.70")
}

// Tasks:

tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
}

tasks.withType(org.gradle.jvm.tasks.Jar::class) {
    archiveBaseName.set(modid)
//    manifest.attributes.run {
//        this["FMLCorePluginContainsFMLMod"] = "true"
//        this["ForceLoadAsMod"] = "true"
//
////         If you don't want mixins, remove these lines
////        this["TweakClass"] = "org.spongepowered.asm.launch.MixinTweaker"
////        this["MixinConfigs"] = "mixins.$modid.json"
//    }
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("mcversion", mcVersion)
    inputs.property("modid", modid)
    inputs.property("basePackage", baseGroup)

    filesMatching(listOf("mcmod.info", "mixins.$modid.json")) {
        expand(inputs.properties)
    }

    rename("(.+_at.cfg)", "META-INF/$1")
}


val remapJar by tasks.named<net.fabricmc.loom.task.RemapJarTask>("remapJar") {
    archiveClassifier.set("")
    from(tasks.shadowJar)
    input.set(tasks.shadowJar.get().archiveFile)
}

tasks.jar {
    archiveClassifier.set("without-deps")
    destinationDirectory.set(layout.buildDirectory.dir("intermediates"))

    manifest.attributes += mapOf(
        "ModSide" to "CLIENT",
        "FMLCorePlugin" to "com.github.calamari34.mantaflipbeta.mixin.MixinLoader",
        "TweakOrder" to 0,
        "ForceLoadAsMod" to true,
        "TweakClass" to "cc.polyfrost.oneconfig.loader.stage0.LaunchWrapperTweaker",
        "MixinConfigs" to "mixins.$modid.json"
    )
}

tasks.shadowJar {
    destinationDirectory.set(layout.buildDirectory.dir("intermediates"))
    archiveClassifier.set("non-obfuscated-with-deps")
    configurations = listOf(shadowImpl)
    doLast {
        configurations.forEach {
            println("Copying dependencies into mod: ${it.files}")
        }
    }

    // If you want to include other dependencies and shadow them, you can relocate them in here
    fun relocate(name: String) = relocate(name, "$baseGroup.deps.$name")
}

tasks.register("yguard") {
    group = "yGuard"
    description = "Obfuscates and shrinks the java archive."

    doLast {
        ant.withGroovyBuilder {
            "taskdef"(
                "name" to "yguard",
                "classname" to "com.yworks.yguard.YGuardTask",
                "classpath" to sourceSets["main"].compileClasspath.asPath
            )

            "yguard" {
                "inout"(mapOf(
                    "in" to tasks.shadowJar.get().archiveFile.get().asFile,
                    "out" to file("${buildDir}/libs/${modid}-${version}-obfuscated.jar")
                ))

                "rename" {
                    "property"(mapOf("name" to "obfuscate", "value" to "true"))
                }

                "shrink" {
                    "property"(mapOf("name" to "shrink", "value" to "true"))
                }
            }
        }
    }
}

tasks.assemble.get().dependsOn(tasks.remapJar)