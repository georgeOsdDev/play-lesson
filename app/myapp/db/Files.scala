package myapp.db

import java.io.File
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

object Files {
  private final val gridFs = DB.gridfs

  def put(serverFilename: String, file: File): Future[Boolean] = {
    Future {
      val in = new BufferedInputStream(new FileInputStream(file))
      try {
        gridFs.createFile(in, serverFilename).save()
        true
      } finally {
        in.close()
      }
    }
  }

  def put(serverFilename: String, data: Array[Byte]): Future[Boolean] = {
    Future {
      val in = new ByteArrayInputStream(data)
      gridFs.createFile(in, serverFilename).save()
      true
    }
  }

  def get(serverFilename: String): Future[Array[Byte]] = {
    Future {
      val f = gridFs.findOne(serverFilename)
      val out = new ByteArrayOutputStream()
      f.writeTo(out)
      out.toByteArray()
    }
  }

  def read(serverFilename: String) = {
    val f = gridFs.findOne(serverFilename)
    f.getInputStream()
  }

  def delete(serverFilename: String): Boolean = {
    gridFs.remove(serverFilename)
    true
  }
}