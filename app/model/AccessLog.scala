package model

import java.time.LocalDateTime

case class AccessLog(
    linkId: Long,
    accessedAt: LocalDateTime = LocalDateTime.now(),
    id: Long = 0L)