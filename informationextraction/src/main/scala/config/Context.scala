package config

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

trait Context {
  lazy val sparkConf: SparkConf = new SparkConf()
    .setAppName("Mine Libraries")
    .setMaster("local[*]")
    .set("spark.cores.max", "8")
    .set("spark.executor.memory", "80g")
    .set("spark.driver.memory", "80g")
    .set("spark.memory.offHeap.enabled", "true")
    .set("spark.memory.offHeap.size", "80g")
    .set("spark.driver.maxResultSize", "80g")
    .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")

  lazy val sparkSession: SparkSession = SparkSession
    .builder()
    .config(sparkConf)
    .getOrCreate()
  sparkSession.sparkContext.setLogLevel("ERROR")
}
