package miner

import java.net.URL
import java.util.zip.ZipFile
import java.io.{File, IOException}

import org.apache.commons.io.FileUtils

import scala.collection.JavaConverters._
import org.apache.bcel.classfile.{ClassParser, JavaClass, Method}

import utils.StringProcess
import org.apache.spark.sql.catalyst.plans.logical.Except

class LibraryMiner(groupID: String, artifactID: String, version: String) {
  val mavenAddress: String = "https://repo1.maven.org/maven2/"
  val path: String = "../jars/" + pathJar

  def pathJar: String = {
    groupID.replaceAll("\\.", "/") + "/" +
      artifactID + "/" +
      version + "/" +
      artifactID + "-" + version + ".jar"
  }

  def downloadJar(): Unit = {
    if (!new File(path).exists()) {
      download(path, mavenAddress + pathJar)
    }
  }

  def download(target: String, url: String): Unit = {
    println(s"Downloading $url ...")
    val file: File = new File(target)
    try {
      if (!file.exists())
        FileUtils.copyURLToFile(new URL(url), new File(target))

      val zipFile: ZipFile = new ZipFile(target)
      println("Done !")
      Some(zipFile)
    } catch {
      case _: IOException => None
    }
  }

  def allInformation(): String = {
    downloadJar()
    val file: File = new File(path)
    var information: Array[String] = Array()

    if (file.exists()) {
      try {
        val zipFile = new ZipFile(file)

        val entries = zipFile.entries.asScala
        entries
          .filter(x => !x.isDirectory && x.getName.endsWith(".class"))
          .foreach(entry => {
            val entryName: String = entry.getName

            if (!entryName.equals("module-info.class")) {
              val classParser: ClassParser =
                new ClassParser(zipFile.getInputStream(entry), entry.getName)

              val javaClass: JavaClass = classParser.parse()
              val className: String = javaClass.getClassName().split("\\.").last

              val methods: Array[Method] = javaClass.getMethods()
              val nameMethods: Array[String] = methods.map(_.getName())

              // The names need to be Camel Case Divided and symbols need to be removed
              val classNameTransformed: String = StringProcess
                .removeNonAscii(StringProcess.camelCaseSplit(className))

              val methodNamesTranformed: Array[String] = nameMethods.map(name =>
                StringProcess.removeNonAscii(StringProcess.camelCaseSplit(name))
              )

              val informationToSave: String =
                classNameTransformed + methodNamesTranformed.mkString(" ")

              information :+= informationToSave
                .split(" ")
                .filter(_.length() > 2)
                .filterNot(_.equals("init"))
                .filterNot(_.equals("clinit"))
                .mkString(" ")
            }
          })
        zipFile.close()
      } catch {
        case _: Exception =>
      }
    }
    information.mkString(" ")
  }

  def publicAPI(): String = {
    downloadJar()
    val file: File = new File(path)
    var information: Array[String] = Array()

    if (file.exists()) {
      try {
        val zipFile = new ZipFile(file)

        val entries = zipFile.entries.asScala
        entries
          .filter(x => !x.isDirectory && x.getName.endsWith(".class"))
          .foreach(entry => {
            val entryName: String = entry.getName

            if (!entryName.equals("module-info.class")) {
              val classParser: ClassParser =
                new ClassParser(zipFile.getInputStream(entry), entry.getName)

              val javaClass: JavaClass = classParser.parse()

              if (javaClass.isPublic()) {
                val className: String =
                  javaClass.getClassName().split("\\.").last

                val methods: Array[Method] = javaClass.getMethods()
                val nameMethods: Array[String] =
                  methods.filter(_.isPublic()).map(_.getName())

                // The names need to be Camel Case Divided and symbols need to be removed
                val classNameTransformed: String = StringProcess
                  .removeNonAscii(StringProcess.camelCaseSplit(className))

                val methodNamesTranformed: Array[String] =
                  nameMethods.map(name =>
                    StringProcess
                      .removeNonAscii(StringProcess.camelCaseSplit(name))
                  )

                val informationToSave: String =
                  classNameTransformed + methodNamesTranformed.mkString(" ")

                information :+= informationToSave
                  .split(" ")
                  .filter(_.length() > 2)
                  .filterNot(_.equals("init"))
                  .filterNot(_.equals("clinit"))
                  .mkString(" ")
              }
            }
          })
        zipFile.close()
      } catch {
        case _: Exception =>
      }
    }
    information.mkString(" ")
  }

  def onlyClassesPublicAPI(): String = {
    downloadJar()
    val file: File = new File(path)
    var information: Array[String] = Array()

    if (file.exists()) {
      try {
        val zipFile = new ZipFile(file)

        val entries = zipFile.entries.asScala
        entries
          .filter(x => !x.isDirectory && x.getName.endsWith(".class"))
          .foreach(entry => {
            val entryName: String = entry.getName

            if (!entryName.equals("module-info.class")) {
              val classParser: ClassParser =
                new ClassParser(zipFile.getInputStream(entry), entry.getName)

              val javaClass: JavaClass = classParser.parse()

              if (javaClass.isPublic()) {
                val className: String =
                  javaClass.getClassName().split("\\.").last

                // The names need to be Camel Case Divided and symbols need to be removed
                val classNameTransformed: String = StringProcess
                  .removeNonAscii(StringProcess.camelCaseSplit(className))

                information :+= classNameTransformed
                  .split(" ")
                  .filter(_.length() > 2)
                  .filterNot(_.equals("init"))
                  .filterNot(_.equals("clinit"))
                  .mkString(" ")
              }
            }
          })
        zipFile.close()
      } catch {
        case _: Exception =>
      }
    }
    information.mkString(" ")
  }

  def onlyMethodsPublic(): String = {
    downloadJar()
    val file: File = new File(path)
    var information: Array[String] = Array()

    if (file.exists()) {
      try {
        val zipFile = new ZipFile(file)

        val entries = zipFile.entries.asScala
        entries
          .filter(x => !x.isDirectory && x.getName.endsWith(".class"))
          .foreach(entry => {
            val entryName: String = entry.getName

            if (!entryName.equals("module-info.class")) {
              val classParser: ClassParser =
                new ClassParser(zipFile.getInputStream(entry), entry.getName)

              val javaClass: JavaClass = classParser.parse()

              if (javaClass.isPublic()) {
                val methods: Array[Method] = javaClass.getMethods()
                val nameMethods: Array[String] =
                  methods.filter(_.isPublic()).map(_.getName())

                // The names need to be Camel Case Divided and symbols need to be removed
                val methodNamesTranformed: Array[String] =
                  nameMethods.map(name =>
                    StringProcess
                      .removeNonAscii(StringProcess.camelCaseSplit(name))
                  )

                val informationToSave: String =
                  methodNamesTranformed.mkString(" ")

                information :+= informationToSave
                  .split(" ")
                  .filter(_.length() > 2)
                  .filterNot(_.equals("init"))
                  .filterNot(_.equals("clinit"))
                  .mkString(" ")
              }
            }
          })
        zipFile.close()
      } catch {
        case _: Exception =>
      }
    }
    information.mkString(" ")
  }

  override def toString: String = s"$groupID -> $artifactID -> $version"
}
