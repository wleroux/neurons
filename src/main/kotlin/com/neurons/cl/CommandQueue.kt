package com.neurons.cl

import org.lwjgl.PointerBuffer
import org.lwjgl.opencl.CL10.*
import org.lwjgl.system.MemoryStack.*

class CommandQueue(val context: Context, private val commandQueueId: Long): AutoCloseable {
  fun enqueueWriteBuffer(buffer: Buffer, offset: Int, values: IntArray) {
    clCheckError(clEnqueueWriteBuffer(commandQueueId, buffer.bufferId, false, offset.toLong(), values, null as PointerBuffer?, null as PointerBuffer?))
  }
  fun enqueueWriteBuffer(buffer: Buffer, offset: Int, values: FloatArray) {
    clCheckError(clEnqueueWriteBuffer(commandQueueId, buffer.bufferId, false, offset.toLong(), values, null as PointerBuffer?, null as PointerBuffer?))
  }

  fun enqueueReadBuffer(buffer: Buffer, offset: Int, values: IntArray) {
    clCheckError(clEnqueueReadBuffer(commandQueueId, buffer.bufferId, false, offset.toLong(), values, null as PointerBuffer?, null as PointerBuffer?))
  }
  fun enqueueReadBuffer(buffer: Buffer, offset: Int, values: FloatArray) {
    clCheckError(clEnqueueReadBuffer(commandQueueId, buffer.bufferId, false, offset.toLong(), values, null as PointerBuffer?, null as PointerBuffer?))
  }

  fun enqueueNDRangeKernel(kernel: Kernel, workDim: Int, size: Int) {
    stackPush().use { stack ->
      val buffer = stack.mallocPointer(1)
      buffer.put(0, size.toLong())

      clEnqueueNDRangeKernel(commandQueueId, kernel.kernelId, workDim, null as PointerBuffer?, buffer, null as PointerBuffer?, null as PointerBuffer?, null as PointerBuffer?)
    }
  }

  fun flush() {
    clFlush(commandQueueId)
  }

  fun finish() {
    clFinish(commandQueueId)
  }

  override fun close() {
    clCheckError(clReleaseCommandQueue(commandQueueId))
  }
}