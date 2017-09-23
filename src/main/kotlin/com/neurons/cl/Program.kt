package com.neurons.cl

import org.lwjgl.opencl.CL10.*
import org.lwjgl.system.MemoryStack.*

class Program(val context: Context, val programId: Long): AutoCloseable {
  fun kernel(kernelName: String): Kernel {
    stackPush().use { stack ->
      val pErrcode = stack.mallocInt(1)
      val kernelId = clCreateKernel(programId, kernelName, pErrcode)
      clCheckError(pErrcode)

      return Kernel(this, kernelId)
    }
  }

  override fun close() {
    clCheckError(clReleaseProgram(programId))
  }
}