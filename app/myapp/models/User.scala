package myapp.models

import java.util.{ List => JList }
import java.util.{ Map => JMap }
import scala.collection.JavaConverters._
import org.bson.types.ObjectId
import com.mongodb.WriteConcern
import org.mongodb.morphia.annotations.{ Entity, Id, Index, Indexes, PostLoad, PrePersist, Reference }
import play.api.libs.Crypto

import myapp.Conf
import myapp.db.DBHelper

@Entity
class Role(val appId: String, val role: String) {
  def this() = this(null, null)
}

@Entity(value = "users", noClassnameStored = true)
@Indexes(Array(new Index(value = "email", unique=true), new Index(value = "name")))
class User(
  val email: String,
  val name: String,
  var password: String,
  @Reference val roles: JList[Role],
  val sys_admin: Boolean,
  val created_at: Integer) {

  @Id var id: ObjectId = _
  var last_login: Integer = _
  var updated_at: Integer = _
  var deleted_at: Integer = _

  def this() = this(null, null, null, null, false, null)

  def this(email: String, name: String, password: String) = {
    this(email, name, password, null, false, null)
  }

  def this(email: String, name: String, password: String, sysAdmin: Boolean) = {
    this(email, name, password, null, sysAdmin, null)
  }

  @PrePersist def prePersist {
    password = Crypto.encryptAES(password, User.passwordSalt)
    updated_at = DBHelper.nowSecs
  }

  @PostLoad def postLoad {
    password = Crypto.decryptAES(password, User.passwordSalt)
  }
}

object User extends BaseModel {

  final val EMAIL = "email"
  final val NAME = "name"
  final val PASSWORD = "password"
  final val SYS_ADMIN = "sys_admin"

  protected final val passwordSalt = "mearb85wa#ggNIEz" // 16byte

  def create(
    email: String,
    name: String,
    password: String,
    roles: Option[List[Role]],
    sysAdmin: Boolean): Option[ObjectId] = {

    val user = roles match {
      case Some(r) =>
        new User(email, name, password, r.asJava, sysAdmin, DBHelper.nowSecs)
      case None => new User(email, name, password, List.empty.asJava, sysAdmin, DBHelper.nowSecs)
    }
    val result = ds.save[User](user)
    DBHelper.getResultObjectId(result)
  }

  def findById(id: String): Option[User] = {
    DBHelper.string2ObjectId(id) match {
      case Some(objectId) =>
        val user = ds.find(classOf[User])
          .field(ID).equal(objectId)
          .field(DELETED_AT).doesNotExist().get
        Option(user)
      case None =>
        None
    }
  }

  def authenticate(email: String, password: String): Option[User] = {
    val user = ds.find(classOf[User])
      .field(EMAIL).equal(email)
      .field(PASSWORD).equal(Crypto.encryptAES(password, passwordSalt))
      .field(DELETED_AT).doesNotExist().get
    Option(user)
  }

  def getSuperUsers: List[User] = {
    val users = ds.find(classOf[User])
      .field(SYS_ADMIN).equal(true).asList
    users.asScala.toList
  }

  def initializeRoot(): Unit = {
    val rootUser = ds.find(classOf[User])
                 .field(EMAIL).equal(Conf.rootUserEmail).get

    Option(rootUser) match {
      case Some(_) =>
      case None =>
        create(Conf.rootUserEmail, Conf.rootUserName, Conf.rootUserPass, None, true)
    }
  }
}
