package com.neurons

import com.neurons.cl.*
import org.lwjgl.opencl.CL10.*
import java.util.concurrent.TimeUnit

const val ITERATIONS = 1000
const val COUNT = 500

fun main(args: Array<String>) {
  val values = FloatArray(COUNT, { index -> index.toFloat()})

  // Plain Old Java
  run {
    val results = FloatArray(COUNT, { _ -> 0f})
    val duration = timed({
      for (i in 0..ITERATIONS) {
        sum(values, values, results)
      }
    })
    println("JAVA: ${duration}ms; ${results[0]} ${results[1]} ${results[2]}")
  }

  // OpenCL
  platforms().forEach { platform ->
    println(platform.name)
    platform.devices.forEach { device ->
      val context = device.context()
      val commandQueue = context.commandQueue()
      val program = context.program(read("com/neurons/vector_multiply.cl"))
      val kernel = program.kernel("vector_multiply")

      val results = FloatArray(COUNT, { _ -> 0f})
      val duration = timed({
        for (i in 0..ITERATIONS) {
          clSum(context, kernel, commandQueue, values, values, results)
        }
      })
      println("\t${device.name}: ${duration}ms; ${results[0]} ${results[1]} ${results[2]}")

      kernel.close()
      program.close()
      commandQueue.close()
      context.close()
    }
  }
}

fun sum(a: FloatArray, b: FloatArray, result: FloatArray) {
  for (i in a.indices) {
    for (j in b) {
      result[i] += a[i] * j
    }
  }
}

fun clSum(context: Context, kernel: Kernel, commandQueue: CommandQueue, a: FloatArray, b: FloatArray, results: FloatArray) {
  // Create memory buffers on the device for each vector
  val bufferA = context.buffer(CL_MEM_WRITE_ONLY, a.size.toLong() * java.lang.Float.BYTES)
  val bufferB = context.buffer(CL_MEM_WRITE_ONLY, b.size.toLong() * java.lang.Float.BYTES)
  val bufferResults = context.buffer(CL_MEM_READ_ONLY, results.size.toLong() * java.lang.Float.BYTES)

  kernel
          .setArg1p(0, bufferA)
          .setArg1p(1, bufferB)
          .setArg1p(2, bufferResults)
          .setArg1i(3, a.size)

  commandQueue.enqueueWriteBuffer(bufferA, 0, a)
  commandQueue.enqueueWriteBuffer(bufferB, 0, b)
  commandQueue.enqueueNDRangeKernel(kernel, 1, results.size)
  commandQueue.enqueueReadBuffer(bufferResults, 0, results)

  commandQueue.flush()
  commandQueue.finish()
  bufferA.close()
  bufferB.close()
  bufferResults.close()
}

fun read(name: String): String {
  return ClassLoader.getSystemClassLoader().getResourceAsStream(name).bufferedReader().use {
    it.readText()
  }
}

inline fun <T>timed(body: () -> T): Long {
  val start = System.nanoTime()
  body()
  val end = System.nanoTime()
  val durationInNanoseconds = end - start
  return TimeUnit.MILLISECONDS.convert(durationInNanoseconds, TimeUnit.NANOSECONDS)
}