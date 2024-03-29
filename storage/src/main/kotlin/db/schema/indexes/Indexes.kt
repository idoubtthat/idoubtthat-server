/*
 * This file is generated by jOOQ.
 */
package db.schema.indexes


import db.schema.tables.Citations
import db.schema.tables.FlywaySchemaHistory
import db.schema.tables.Replies
import db.schema.tables.Users

import org.jooq.Index
import org.jooq.impl.DSL
import org.jooq.impl.Internal



// -------------------------------------------------------------------------
// INDEX definitions
// -------------------------------------------------------------------------

val CITATIONS_CITATION_ID: Index = Internal.createIndex(DSL.name("citation_id"), Citations.CITATIONS, arrayOf(Citations.CITATIONS.CITATION_ID, Citations.CITATIONS.VALID_TO), false)
val FLYWAY_SCHEMA_HISTORY_FLYWAY_SCHEMA_HISTORY_S_IDX: Index = Internal.createIndex(DSL.name("flyway_schema_history_s_idx"), FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY, arrayOf(FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY.SUCCESS), false)
val REPLIES_REPLY_ID: Index = Internal.createIndex(DSL.name("reply_id"), Replies.REPLIES, arrayOf(Replies.REPLIES.REPLY_ID, Replies.REPLIES.VALID_TO), false)
val USERS_USER_ID: Index = Internal.createIndex(DSL.name("user_id"), Users.USERS, arrayOf(Users.USERS.USER_ID, Users.USERS.VALID_TO), false)
