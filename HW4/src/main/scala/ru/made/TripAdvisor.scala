package org.example.ru.made

import org.apache.spark.sql._
import org.apache.spark.sql.functions._


object TripAdvisor{
  def main(args: Array[String]): Unit = {

    import org.apache.spark.sql.expressions.Window

    val spark = SparkSession.builder()
      .master("local[*]")
      .appName("ru/made")
      .getOrCreate()

    val trip = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv("tripadvisor_hotel_reviews.csv")

    val df = trip
      .withColumn("Review", regexp_replace(lower(col("Review")), "[^a-z ]", ""))
      .select(col("Review"))
      .withColumn("Review", split(trim(col("Review")), "\\s+"))
      .withColumn("id", monotonically_increasing_id())
      .withColumn("word", explode(col("Review")))

    val partitionById = Window.partitionBy(col("id"), col("word"))
    val tf = df
      .withColumn("word_cnt", count(col("Review")).over(partitionById))
      .withColumn("len", size(col("Review")))
      .withColumn("tf", col("word_cnt") / col("len"))
      .select("id", "tf", "word")

    val docsNumber = trip.count()

    val idf = df
      .groupBy("word")
      .agg(countDistinct("id").as("docfreq"))
      .orderBy(desc("docfreq"))
      .limit(100)
      .withColumn("docsNumber", lit(docsNumber))
      .withColumn("idf", log(col("docsNumber") / col("docfreq")))
      .select("word", "idf")

    val tfIdf = tf
      .join(idf, Seq("word"), joinType = "Inner")
      .withColumn("tfidf", col("tf") * col("idf"))

    val tfIdfPivot = tfIdf.groupBy("id").pivot("word").max("tfidf").na.fill(0)

    tfIdfPivot.show()
  }
}