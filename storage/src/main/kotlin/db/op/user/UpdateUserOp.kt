package db.op.user

import db.schema.tables.references.USERS
import db.op.WriteOp
import db.models.UserDAO
import org.jooq.Configuration
import java.time.LocalDateTime

class UpdateUserOp(dao: UserDAO) : WriteOp<UserDAO>(dao) {
    override fun run(configuration: Configuration) {
        val now = LocalDateTime.now()
        configuration.dsl().update(USERS)
            .set(USERS.VALID_TO, now)
            .where(USERS.VALID_TO.isNull)
            .and(USERS.USER_ID.eq(dao.id.toString()))
            .execute()
        configuration.dsl().insertInto(USERS)
            .columns(
                USERS.USER_ID,
                USERS.FIRST_NAME,
                USERS.LAST_NAME,
                USERS.VALID_FROM,
                USERS.VALID_TO)
            .values(
                dao.id.toString(),
                dao.firstName,
                dao.lastName,
                now,
                null)
            .execute()
    }
}
