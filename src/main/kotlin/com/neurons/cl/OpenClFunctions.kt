package com.neurons.cl

import org.lwjgl.opencl.CL10.*
import org.lwjgl.system.MemoryStack.*
import java.nio.IntBuffer

fun platforms(): List<Platform> {
  stackPush().use { stack ->
    val platformSizeBuffer = stack.mallocInt(1)
    clCheckError(clGetPlatformIDs(null, platformSizeBuffer))

    val platformIds = stack.mallocPointer(platformSizeBuffer[0])
    clCheckError(clGetPlatformIDs(platformIds, null as IntBuffer?))
    return (0 until platformIds.capacity()).map {platformIndex->
      Platform(platformIds[platformIndex])
    }
  }
}

fun clCheckError(pErrcode: IntBuffer) {
  clCheckError(pErrcode.get(pErrcode.position()))
}

fun clCheckError(errcode: Int) {
  if (errcode != CL_SUCCESS) {
    throw RuntimeException(String.format("OpenCL error [%d]", errcode))
  }
}
