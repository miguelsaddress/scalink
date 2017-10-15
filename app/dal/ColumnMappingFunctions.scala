package dal

import java.net.URL
import java.sql.Timestamp
import java.time.LocalDateTime


object ColumnMappingFunctions {

  def url2string(url: URL) = url.toString()
  def string2url(str: String) = new URL(str)

  def localDateTime2timestamp(ldt: LocalDateTime) = Timestamp.valueOf(ldt)
  def timestamp2localeDateTime(t: Timestamp) = t.toLocalDateTime   
}