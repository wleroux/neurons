package com.neurons.cl

import org.lwjgl.opencl.CL.*
import org.lwjgl.opencl.CL10.*
import org.lwjgl.opencl.CLCapabilities
import org.lwjgl.system.MemoryStack.*
import java.nio.IntBuffer

class Platform(val platformId: Long) {
  val capacities: CLCapabilities = createPlatformCapabilities(platformId)
  val devices : List<Device> get() {
    stackPush().use { stack ->
      val deviceSizeBuffer = stack.mallocInt(1)
      clCheckError(clGetDeviceIDs(platformId, CL_DEVICE_TYPE_ALL.toLong(), null, deviceSizeBuffer))

      val deviceIds = stack.mallocPointer(deviceSizeBuffer.get(0))
      clCheckError(clGetDeviceIDs(platformId, CL_DEVICE_TYPE_ALL.toLong(), deviceIds, null as IntBuffer?))
      return (0 until deviceIds.capacity()).map { deviceIndex->
        Device(this, deviceIds[deviceIndex])
      }
    }
  }

  val name : String get() {
    stackPush().use { stack ->
      val size = stack.mallocPointer(1)
      val buffer = stack.malloc(100)

      clCheckError(clGetPlatformInfo(platformId, CL_PLATFORM_NAME, buffer, size))
      val builder = StringBuilder()
      for (c in 0 until size[0]) {
        builder.append(buffer[c.toInt()].toChar())
      }
      return builder.toString()
    }
  }
}
