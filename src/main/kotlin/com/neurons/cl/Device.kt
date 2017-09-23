package com.neurons.cl

import org.lwjgl.opencl.CL.*
import org.lwjgl.opencl.CL10.*
import org.lwjgl.opencl.CLCapabilities
import org.lwjgl.opencl.CLContextCallback
import org.lwjgl.system.MemoryStack.*
import org.lwjgl.system.MemoryUtil

class Device(val platform: Platform, val deviceId: Long) {
  val capacities: CLCapabilities = createDeviceCapabilities(deviceId, platform.capacities)
  val name: String get() {
    stackPush().use { stack ->
      val size = stack.mallocPointer(1)
      val buffer = stack.malloc(100)

      clCheckError(clGetDeviceInfo(deviceId, CL_DEVICE_NAME, buffer, size))
      val builder = StringBuilder()
      for (c in 0 until size[0]) {
        builder.append(buffer[c.toInt()].toChar())
      }
      return builder.toString()
    }
  }
  fun context(): Context {
    stackPush().use { stack ->
      val pErrcode = stack.callocInt(1)
      val contextCallback = CLContextCallback.create({ errinfo, _, _, _ ->
        System.err.println("[LWJGL] cl_context_callback")
        System.err.println("\tInfo: " + MemoryUtil.memUTF8(errinfo))
      })

      val ctxProps = stack.mallocPointer(3)
      ctxProps.put(0, CL_CONTEXT_PLATFORM.toLong())
              .put(1, platform.platformId)
              .put(2, 0)

      val contextId = clCreateContext(ctxProps, deviceId, contextCallback, MemoryUtil.NULL, pErrcode)
      clCheckError(pErrcode)

      return Context(this, contextId)
    }
  }
}