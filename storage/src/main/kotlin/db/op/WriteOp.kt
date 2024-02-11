package info.idoubtthat.db.op

import info.idoubtthat.models.DAO
import org.jooq.TransactionalRunnable

abstract class WriteOp<T: DAO>(val dao: T): TransactionalRunnable
