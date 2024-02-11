package info.idoubtthat.db.op.user

import info.idoubtthat.db.op.ReadOp
import info.idoubtthat.db.schema.tables.references.USERS
import info.idoubtthat.models.UserDAO
import org.jooq.DSLContext
import org.jooq.impl.DSL
import java.util.*

class GetUserOp(private val dao: UserDAO): ReadOp<UserDAO?> {
    override fun run(context: DSLContext): UserDAO? {
        val result = context.selectFrom(USERS)
            .where(USERS.VALID_TO.isNull)
            .and(if (dao.id != null) USERS.USER_ID.eq(dao.id.toString()) else DSL.noCondition())
            .and(if (dao.firstName != null) USERS.FIRST_NAME.eq(dao.firstName) else DSL.noCondition())
            .and(if (dao.lastName != null) USERS.USER_ID.eq(dao.lastName) else DSL.noCondition())
           .fetchOne()
        return if (result != null) {
            UserDAO(
                UUID.fromString(result.userId!!),
                result.firstName!!,
                result.lastName!!
            )
        } else {
            null
        }
    }
}