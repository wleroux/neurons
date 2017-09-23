kernel void vector_multiply(global const float* a, global const float* b, global float* result, int const size) {
  const int gid = get_global_id(0);
  for (int j = 0; j < size; j ++) {
    result[gid] += a[gid] * b[j];
  }
}