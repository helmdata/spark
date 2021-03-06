/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// scalastyle:off println
package org.apache.spark.examples

import scala.math.random

import org.apache.spark.sql.SparkSession

/** Computes an approximation to pi */
object SparkPi {
  def main(args: Array[String]) {
    // helm 获取编程入口
    val spark = SparkSession
      .builder
      .appName("Spark Pi")
      .getOrCreate()
    // 计算并行度
    val slices = if (args.length > 0) args(0).toInt else 2
    val n = math.min(100000L * slices, Int.MaxValue).toInt // avoid overflow
    /**
     * 并行化数据集得到RDD，并执行各种计算，得到结果集RDD
     * count 存在于driver端
     * 1、spark.sparkContext spark初始化
     */
    val count = spark.sparkContext.parallelize(1 until n, slices).map { i =>
      val x = random * 2 - 1
      val y = random * 2 - 1
      if (x*x + y*y <= 1) 1 else 0
    }
      /**
       * reduce action算子 触发任务执行
       * 2、sparkContext.runJob()
       */
      .reduce(_ + _)
    // 输出结果
    println(s"Pi is roughly ${4.0 * count / (n - 1)}")
    // 停止执行
    spark.stop()
  }
}
// scalastyle:on println
