package com.neurons.cl

import org.lwjgl.opencl.CL10.*

class Buffer(val bufferId: Long): AutoCloseable {
  override fun close() {
    clReleaseMemObject(bufferId)
  }
}