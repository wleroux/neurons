package com.neurons.cl

import org.lwjgl.opencl.CL10.*
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.*
import java.nio.FloatBuffer

class Context(val device: Device, val contextId: Long) : AutoCloseable {
  fun commandQueue(): CommandQueue {
    stackPush().use { stack ->
      val pErrcode = stack.mallocInt(1)
      val commandQueueId = clCreateCommandQueue(contextId, device.deviceId, NULL, pErrcode)
      clCheckError(pErrcode)
      return CommandQueue(this, commandQueueId)
    }
  }

  fun program(programSource: String): Program {
    stackPush().use { stack ->
      val pErrcode = stack.mallocInt(1)
      val programId = clCreateProgramWithSource(contextId, programSource, pErrcode)
      clCheckError(pErrcode)

      // Build the program
      clCheckError(clBuildProgram(programId, device.deviceId, "", null, NULL))

      return Program(this, programId)
    }
  }

  fun buffer(flags: Int, size: Long): Buffer {
    stackPush().use { stack ->
      val pErrcode = stack.mallocInt(1)
      val bufferId = clCreateBuffer(contextId, flags.toLong(), size, pErrcode)
      clCheckError(pErrcode)

      return Buffer(bufferId)
    }
  }

  fun buffer(flags: Int, values: FloatArray): Buffer {
    val pErrcode = intArrayOf(0)
    val bufferId = clCreateBuffer(contextId, flags.toLong(), values, pErrcode)
    clCheckError(pErrcode[0])

    return Buffer(bufferId)
  }

  override fun close() {
    clCheckError(clReleaseContext(contextId))
  }
}