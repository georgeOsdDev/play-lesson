package myapp.db;

import java.util.{ ArrayList => JArrayList, Map => JMap, Date }

import scala.annotation.tailrec
import scala.util.Random
import scala.util.control.NonFatal

import org.bson.types.ObjectId
import com.mongodb.{
  BasicDBObject,
  DBCollection,
  DBObject,
  MongoClient,
  MongoCredential,
  MongoException,
  ServerAddress,
  WriteConcern
}
import com.mongodb.gridfs.GridFS
import org.mongodb.morphia.{Datastore, Key, Morphia}

import myapp.Conf;

object DB {
  private val (mongoDB, mongoClient) = {
    val serverAddresses = new JArrayList[ServerAddress]
    val it = Conf.dbHosts.iterator()
    while (it.hasNext()) {
      val host = it.next()
      serverAddresses.add(new ServerAddress(host, Conf.dbPort))
    }

    val client = (Conf.dbUser, Conf.dbPass) match {
      case (Some(u), Some(p)) =>
        val credentials = new JArrayList[MongoCredential]
        for (i <- 1 to serverAddresses.size) {
          credentials.add(MongoCredential.createMongoCRCredential(u, Conf.dbName, p.toCharArray))
        }
        new MongoClient(serverAddresses, credentials)

      case _ =>
        new MongoClient(serverAddresses)
    }

    (client.getDB(Conf.dbName), client)
  }

  val gridfs = new GridFS(mongoDB)
  // tokensColl is used from Tokens.java
  val tokensColl = mongoDB.getCollection("tokens")
  // Other collections will be used by Morphia except "tokens" collection
  val ds = new Morphia().createDatastore(mongoClient, Conf.dbName)

  def getColl(collName: String) = {
    mongoDB.getCollection(collName)
  }
}

object DBHelper {

  def nowSecs() = (System.currentTimeMillis() / 1000).toInt

  def objIdFromSecs(secs: Int) = {
    val date = new Date(secs.toLong * 1000)
    new ObjectId(date)
  }

  /**
   * Numbers added in MongoDB console are double by default. Mistakenly added
   * numbers in MongoDB console (without casting using NumberInt) will cause
   * exception if we simply use mongoDBDoc.get("number").asInstanceOf[Int] to
   * get the number out from the server side.
   *
   * null is converted to 0.
   */
  def toInt(number: Any): Int = {
    try {
      number.asInstanceOf[Int]
    } catch {
      case NonFatal(e) =>
        number.asInstanceOf[Double].toInt
    }
  }

  def toBoolean(o: Object, defaultValue: Boolean): Boolean = {
    if (o == null) defaultValue
    else {
      try {
        o.asInstanceOf[Boolean]
      } catch {
        case NonFatal(e) =>
          defaultValue
      }
    }
  }

  private val r = new Random(31)
  @tailrec
  private def randomString(n: Int, list: List[Char]): List[Char] = {
    if (n == 1) util.Random.nextPrintableChar :: list
    else randomString(n - 1, util.Random.nextPrintableChar :: list)
  }

  def createRandomString = randomString(10, List.empty).mkString("")

  def string2ObjectId(id: String):Option[ObjectId] =  {
    try {
      Some(new ObjectId(id))
    } catch {
      case e: IllegalArgumentException =>
        None
    }
  }

  def isJavaNull(v: Any): Boolean = {
    null == v
  }

  def nullOrVal[T](opt: Option[T]) = {
    opt match {
      case Some(v) => v
      case None    => null
    }
  }

  def nullOrVal(opt: Option[String]) = {
    opt match {
      case Some(v) => v
      case None    => null
    }
  }

  def null2Option(v: Any) = {
    if (null == v) None
    else Some(v)
  }

  def getResultObjectId[T](result: Key[T]): Option[ObjectId] = {
    if (result != null)
      Some(result.getId.asInstanceOf[ObjectId])
    else
      None
  }

}

