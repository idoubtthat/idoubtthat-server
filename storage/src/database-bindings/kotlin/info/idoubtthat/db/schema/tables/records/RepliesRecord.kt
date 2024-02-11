/*
 * This file is generated by jOOQ.
 */
package info.idoubtthat.db.schema.tables.records


import info.idoubtthat.db.schema.tables.Replies

import java.time.LocalDateTime

import org.jooq.impl.TableRecordImpl


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class RepliesRecord() : TableRecordImpl<RepliesRecord>(Replies.REPLIES) {

    open var replyId: String?
        set(value): Unit = set(0, value)
        get(): String? = get(0) as String?

    open var citationId: String?
        set(value): Unit = set(1, value)
        get(): String? = get(1) as String?

    open var userId: String?
        set(value): Unit = set(2, value)
        get(): String? = get(2) as String?

    open var replyCommentary: String?
        set(value): Unit = set(3, value)
        get(): String? = get(3) as String?

    open var validFrom: LocalDateTime?
        set(value): Unit = set(4, value)
        get(): LocalDateTime? = get(4) as LocalDateTime?

    open var validTo: LocalDateTime?
        set(value): Unit = set(5, value)
        get(): LocalDateTime? = get(5) as LocalDateTime?

    /**
     * Create a detached, initialised RepliesRecord
     */
    constructor(replyId: String? = null, citationId: String? = null, userId: String? = null, replyCommentary: String? = null, validFrom: LocalDateTime? = null, validTo: LocalDateTime? = null): this() {
        this.replyId = replyId
        this.citationId = citationId
        this.userId = userId
        this.replyCommentary = replyCommentary
        this.validFrom = validFrom
        this.validTo = validTo
        resetChangedOnNotNull()
    }
}