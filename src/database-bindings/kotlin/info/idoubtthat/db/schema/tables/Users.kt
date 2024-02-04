/*
 * This file is generated by jOOQ.
 */
package info.idoubtthat.db.schema.tables


import info.idoubtthat.db.schema.Citation
import info.idoubtthat.db.schema.indexes.USERS_USER_ID
import info.idoubtthat.db.schema.indexes.USERS_USER_ID_2
import info.idoubtthat.db.schema.tables.records.UsersRecord

import java.time.LocalDateTime

import kotlin.collections.Collection
import kotlin.collections.List

import org.jooq.Condition
import org.jooq.Field
import org.jooq.ForeignKey
import org.jooq.Index
import org.jooq.InverseForeignKey
import org.jooq.Name
import org.jooq.PlainSQL
import org.jooq.QueryPart
import org.jooq.Record
import org.jooq.SQL
import org.jooq.Schema
import org.jooq.Select
import org.jooq.Stringly
import org.jooq.Table
import org.jooq.TableField
import org.jooq.TableOptions
import org.jooq.impl.DSL
import org.jooq.impl.SQLDataType
import org.jooq.impl.TableImpl


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class Users(
    alias: Name,
    path: Table<out Record>?,
    childPath: ForeignKey<out Record, UsersRecord>?,
    parentPath: InverseForeignKey<out Record, UsersRecord>?,
    aliased: Table<UsersRecord>?,
    parameters: Array<Field<*>?>?,
    where: Condition?
): TableImpl<UsersRecord>(
    alias,
    Citation.CITATION,
    path,
    childPath,
    parentPath,
    aliased,
    parameters,
    DSL.comment(""),
    TableOptions.table(),
    where,
) {
    companion object {

        /**
         * The reference instance of <code>citation.users</code>
         */
        val USERS: Users = Users()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<UsersRecord> = UsersRecord::class.java

    /**
     * The column <code>citation.users.user_id</code>.
     */
    val USER_ID: TableField<UsersRecord, String?> = createField(DSL.name("user_id"), SQLDataType.VARCHAR(36), this, "")

    /**
     * The column <code>citation.users.first_name</code>.
     */
    val FIRST_NAME: TableField<UsersRecord, String?> = createField(DSL.name("first_name"), SQLDataType.VARCHAR(50), this, "")

    /**
     * The column <code>citation.users.last_name</code>.
     */
    val LAST_NAME: TableField<UsersRecord, String?> = createField(DSL.name("last_name"), SQLDataType.VARCHAR(50), this, "")

    /**
     * The column <code>citation.users.valid_from</code>.
     */
    val VALID_FROM: TableField<UsersRecord, LocalDateTime?> = createField(DSL.name("valid_from"), SQLDataType.LOCALDATETIME(0), this, "")

    /**
     * The column <code>citation.users.valid_to</code>.
     */
    val VALID_TO: TableField<UsersRecord, LocalDateTime?> = createField(DSL.name("valid_to"), SQLDataType.LOCALDATETIME(0), this, "")

    private constructor(alias: Name, aliased: Table<UsersRecord>?): this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<UsersRecord>?, parameters: Array<Field<*>?>?): this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<UsersRecord>?, where: Condition?): this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>citation.users</code> table reference
     */
    constructor(alias: String): this(DSL.name(alias))

    /**
     * Create an aliased <code>citation.users</code> table reference
     */
    constructor(alias: Name): this(alias, null)

    /**
     * Create a <code>citation.users</code> table reference
     */
    constructor(): this(DSL.name("users"), null)
    override fun getSchema(): Schema? = if (aliased()) null else Citation.CITATION
    override fun getIndexes(): List<Index> = listOf(USERS_USER_ID, USERS_USER_ID_2)
    override fun `as`(alias: String): Users = Users(DSL.name(alias), this)
    override fun `as`(alias: Name): Users = Users(alias, this)
    override fun `as`(alias: Table<*>): Users = Users(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): Users = Users(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): Users = Users(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): Users = Users(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): Users = Users(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): Users = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): Users = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): Users = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): Users = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): Users = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): Users = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): Users = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): Users = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): Users = where(DSL.notExists(select))
}
