package dal

import java.net.URL

object Implicits {
  implicit def string2url(str: String) = {
    if (!str.startsWith("http")) new URL("http://" + str) else new URL(str)
  }
}
