/*
 * This file is generated by jOOQ.
 */
package db.schema.keys


import db.schema.tables.Citations
import db.schema.tables.FlywaySchemaHistory
import db.schema.tables.Replies
import db.schema.tables.Users
import db.schema.tables.records.CitationsRecord
import db.schema.tables.records.FlywaySchemaHistoryRecord
import db.schema.tables.records.RepliesRecord
import db.schema.tables.records.UsersRecord

import org.jooq.UniqueKey
import org.jooq.impl.DSL
import org.jooq.impl.Internal



// -------------------------------------------------------------------------
// UNIQUE and PRIMARY KEY definitions
// -------------------------------------------------------------------------

val KEY_CITATIONS_PRIMARY: UniqueKey<CitationsRecord> = Internal.createUniqueKey(Citations.CITATIONS, DSL.name("KEY_citations_PRIMARY"), arrayOf(Citations.CITATIONS.CITATION_ID, Citations.CITATIONS.VALID_FROM), true)
val KEY_FLYWAY_SCHEMA_HISTORY_PRIMARY: UniqueKey<FlywaySchemaHistoryRecord> = Internal.createUniqueKey(FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY, DSL.name("KEY_flyway_schema_history_PRIMARY"), arrayOf(FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY.INSTALLED_RANK), true)
val KEY_REPLIES_PRIMARY: UniqueKey<RepliesRecord> = Internal.createUniqueKey(Replies.REPLIES, DSL.name("KEY_replies_PRIMARY"), arrayOf(Replies.REPLIES.REPLY_ID, Replies.REPLIES.VALID_TO), true)
val KEY_USERS_PRIMARY: UniqueKey<UsersRecord> = Internal.createUniqueKey(Users.USERS, DSL.name("KEY_users_PRIMARY"), arrayOf(Users.USERS.USER_ID, Users.USERS.VALID_FROM), true)
