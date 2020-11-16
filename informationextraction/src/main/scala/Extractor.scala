import miner.LibraryMiner
import config.Context
import java.io.PrintWriter
import java.io.File

import org.apache.spark.sql.Encoder
import scala.reflect.ClassTag

object Extractor extends Context {
  implicit def kryoEncoder[A](implicit ct: ClassTag[A]): Encoder[A] =
    org.apache.spark.sql.Encoders.kryo[A](ct)

  def writeExternalFile(data: Array[String], tags: Array[String], path: String): Unit = {
    val pw: PrintWriter = new PrintWriter(new File(path))

    pw.write(data.zip(tags).filter(_._1.nonEmpty).map {
      case (elem1, elem2) => s"$elem1,$elem2" 
    }.mkString("\n"))

    pw.close()
  }

  def main(args: Array[String]): Unit = {
    val DATA_PATH: String = "../tags_libraries"
    val DATA_OUT: String = s"../tags_libraries_extracted"

    println("Reading data ...")
    val dataLibraries = sparkSession.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv(s"$DATA_PATH/gt_three_tags.csv")
      .toDF()
    println("Done!")

    val processedLibraries: Array[(String, String)] =
      dataLibraries
        .map(row => {
          val groupID: String = row.getAs[String]("groupID")
          val artifactID: String = row.getAs[String]("artifactID")
          val version: String = row.getAs[String]("latestVersion")
          val tags: String = row.getAs[String]("tags")

          println(s"Analysing library $groupID -> $artifactID -> $version")
          val miner: LibraryMiner =
            new LibraryMiner(groupID, artifactID, version)

          val allInfo: String = miner.allInformation()
          val allPublic: String = miner.publicAPI()
          // val onlyClassPublic: String = miner.onlyClassesPublicAPI()
          // val onlyMethodPublic: String = miner.onlyMethodsPublic()

          (allPublic, tags)
        }).collect()
    
    writeExternalFile(processedLibraries.map(_._1), processedLibraries.map(_._2), s"$DATA_OUT/gt_three_tags.txt")
    writeExternalFile(processedLibraries.map(_._1), processedLibraries.map(_._2), "../onlyClassesPublic.txt")
    writeExternalFile(processedLibraries.map(_._1), processedLibraries.map(_._2), "../onlyMethodsPublic.txt")

    sparkSession.stop()
    sparkSession.close()
  }
}
