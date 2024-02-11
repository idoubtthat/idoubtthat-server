package info.idoubtthat.db.op

import org.jooq.DSLContext

interface ReadOp<T> {
    fun run(context: DSLContext): T
}