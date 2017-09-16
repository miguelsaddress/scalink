package util

case class PasswordExtensions(clearPassword: String) {
  import com.github.t3hnar.bcrypt._

  private[this] val ROUNDS: Int = 12

  def hash(): String = clearPassword.bcrypt(ROUNDS)
  def matches(aHash: String): Boolean = clearPassword.isBcrypted(aHash)
}
