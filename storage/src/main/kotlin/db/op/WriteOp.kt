package db.op

import db.models.DAO
import org.jooq.TransactionalRunnable

abstract class WriteOp<T: DAO>(val dao: T): TransactionalRunnable
