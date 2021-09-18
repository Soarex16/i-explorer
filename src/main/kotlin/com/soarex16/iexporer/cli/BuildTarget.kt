package com.soarex16.iexporer.cli

import com.soarex16.iexporer.emitter.IInterfaceEmitter
import com.soarex16.iexporer.emitter.java.SpoonJavaEmitter
import com.soarex16.iexporer.emitter.python.SimplePythonEmitter

enum class BuildTarget(platformName: String) {
    JAVA("java"),
    PYTHON("python")
}

fun getEmitter(targetPlatform: BuildTarget): IInterfaceEmitter = when (targetPlatform) {
    BuildTarget.PYTHON -> SimplePythonEmitter()
    // TODO: починить эмиттер в java
    BuildTarget.JAVA -> throw RuntimeException("${SpoonJavaEmitter::javaClass.name} currently not working")//SpoonJavaEmitter()
}

fun getTargetFileExtension(targetPlatform: BuildTarget): String = when (targetPlatform) {
    BuildTarget.PYTHON -> ".py"
    BuildTarget.JAVA -> ".java"
}