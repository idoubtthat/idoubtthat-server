/*
 * This file is generated by jOOQ.
 */
package info.idoubtthat.db.schema.tables.records


import info.idoubtthat.db.schema.tables.Users

import java.time.LocalDateTime

import org.jooq.impl.TableRecordImpl


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class UsersRecord() : TableRecordImpl<UsersRecord>(Users.USERS) {

    open var userId: String?
        set(value): Unit = set(0, value)
        get(): String? = get(0) as String?

    open var firstName: String?
        set(value): Unit = set(1, value)
        get(): String? = get(1) as String?

    open var lastName: String?
        set(value): Unit = set(2, value)
        get(): String? = get(2) as String?

    open var validFrom: LocalDateTime?
        set(value): Unit = set(3, value)
        get(): LocalDateTime? = get(3) as LocalDateTime?

    open var validTo: LocalDateTime?
        set(value): Unit = set(4, value)
        get(): LocalDateTime? = get(4) as LocalDateTime?

    /**
     * Create a detached, initialised UsersRecord
     */
    constructor(userId: String? = null, firstName: String? = null, lastName: String? = null, validFrom: LocalDateTime? = null, validTo: LocalDateTime? = null): this() {
        this.userId = userId
        this.firstName = firstName
        this.lastName = lastName
        this.validFrom = validFrom
        this.validTo = validTo
        resetChangedOnNotNull()
    }
}