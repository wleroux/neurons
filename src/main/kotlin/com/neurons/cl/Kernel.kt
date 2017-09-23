package com.neurons.cl

import org.lwjgl.opencl.CL10.*

class Kernel(val program: Program, val kernelId: Long): AutoCloseable {
  fun setArg1p(index: Int, buffer: Buffer): Kernel {
    clCheckError(clSetKernelArg1p(kernelId, index, buffer.bufferId))
    return this
  }

  fun setArg1i(index: Int, value: Int) {
    clCheckError(clSetKernelArg1i(kernelId, index, value))
  }

  override fun close() {
    clCheckError(clReleaseKernel(kernelId))
  }
}