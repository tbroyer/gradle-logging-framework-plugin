package net.ltgt.gradle.logging

internal data class Id(
    val group: String,
    val name: String
) {
    override fun toString(): String = "$group:$name"
}

internal val slf4jBindings = setOf(
    // see http://www.slf4j.org/manual.html#swapping
    Id("org.slf4j", "slf4j-log4j12"),
    Id("org.slf4j", "slf4j-jdk14"),
    Id("org.slf4j", "slf4j-nop"),
    Id("org.slf4j", "slf4j-simple"),
    Id("org.slf4j", "slf4j-jcl"),
    Id("ch.qos.logback", "logback-classic"),
    // see https://logging.apache.org/log4j/2.x/log4j-slf4j-impl/index.html
    Id("org.apache.logging.log4j", "log4j-slf4j-impl"),
    Id("org.apache.logging.log4j", "log4j-slf4j18-impl")
)

internal val slf4jBridges = mapOf(
    // see http://www.slf4j.org/legacy.html
    Id("org.slf4j", "jcl-over-slf4j") to Id("commons-logging", "commons-logging-api"),
    Id("org.slf4j", "log4j-over-slf4j") to Id("log4j", "log4j"),
    // see https://logging.apache.org/log4j/2.x/log4j-to-slf4j/index.html
    Id("org.apache.logging.log4j", "log4j-to-slf4j") to Id("org.apache.logging.log4j", "log4j-core")
)

internal val slf4jJulLoop = setOf(
    Id("org.slf4j", "slf4j-jdk14"),
    Id("org.slf4j", "jul-to-slf4j")
)

internal val log4jImplementations = setOf(
    // see https://logging.apache.org/log4j/2.x/log4j-core/index.html
    Id("org.apache.logging.log4j", "log4j-core"),
    // see https://logging.apache.org/log4j/2.x/log4j-to-slf4j/index.html
    Id("org.apache.logging.log4j", "log4j-to-slf4j")
)

internal val log4jBridges = mapOf(
    // see https://logging.apache.org/log4j/2.x/log4j-1.2-api/index.html
    Id("org.apache.logging.log4j", "log4j-1.2-api") to Id("log4j", "log4j")
)

internal val floggerBackends = setOf(
    Id("com.google.flogger", "flogger-system-backend"),
    Id("com.google.flogger", "flogger-log4j-backend"),
    Id("com.google.flogger", "flogger-log4j2-backend"),
    Id("com.google.flogger", "flogger-slf4j-backend")
)

internal val julBridges = setOf(
    // see http://www.slf4j.org/legacy.html#jul-to-slf4j
    Id("org.slf4j", "jul-to-slf4j"),
    // see https://logging.apache.org/log4j/2.x/log4j-jul/index.html
    Id("org.apache.logging.log4j", "log4j-jul")
)

internal val jclProviders = setOf(
    // see https://logging.apache.org/log4j/2.x/log4j-jcl/index.html
    Id("org.apache.logging.log4j", "log4j-jcl")
)

internal val SLF4J_BINDING_CAPABILITY = Id("net.ltgt.logging-framework", "slf4j-binding-capability")
internal val SLF4J_JUL_LOOP_CAPABILITY = Id("net.ltgt.logging-framework", "slf4j-jul-loop-capability")
internal val LOG4J_IMPLEMENTATION_CAPABILITY = Id("net.ltgt.logging-framework", "log4j-implementation-capability")
internal val FLOGGER_BACKEND_CAPABILITY = Id("net.ltgt.logging-framework", "flogger-backend-capability")
internal val JUL_BRIDGE_CAPABILITY = Id("net.ltgt.logging-framework", "jul-bridge-capability")
internal val JCL_PROVIDER_CAPABILITY = Id("net.ltgt.logging-framework", "jcl-provider-capability")
