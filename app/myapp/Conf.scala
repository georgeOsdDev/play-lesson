package myapp;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import play.Logger;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

object Conf {

  //----------------------------------------------------------------------------

  private final val app = ConfigFactory.load().getConfig("app")

  //----------------------------------------------------------------------------

  final val dbHosts = app.getStringList("db.hosts")
  final val dbPort  = app.getInt("db.port")
  final val dbName  = app.getString("db.name")
  final val dbUser  = if (app.hasPath("db.user")) Option(app.getString("db.user")) else None
  final val dbPass  = if (app.hasPath("db.pass")) Option(app.getString("db.pass")) else None

  //----------------------------------------------------------------------------

  final val rootUserName  = app.getString("root.name")
  final val rootUserPass  = app.getString("root.pass")
  final val rootUserEmail = app.getString("root.email")
}
