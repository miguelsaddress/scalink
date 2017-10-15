import com.google.inject.{ AbstractModule, Provides, Singleton }
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import com.mamoreno.playseed.auth.{ AuthorizationHandler }
import auth.AuthorizationHandlerImpl

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.

 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
class Module extends AbstractModule {

  override def configure() = {
    bind(classOf[AuthorizationHandler]).to(classOf[AuthorizationHandlerImpl])
  }

  @Provides @Singleton
  def provideDatabaseConfig: DatabaseConfig[JdbcProfile] = {
    val env = Option(System.getProperty("env")).getOrElse("dev")
    env match {
      case "dev" => DatabaseConfig.forConfig[JdbcProfile]("dev")
      case "test" => DatabaseConfig.forConfig[JdbcProfile]("test")
      case x => DatabaseConfig.forConfig[JdbcProfile]("prod")
    }
  }

}
