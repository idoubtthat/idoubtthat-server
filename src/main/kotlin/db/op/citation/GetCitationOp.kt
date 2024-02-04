package info.idoubtthat.db.op.citation

import info.idoubtthat.db.op.ReadOp
import info.idoubtthat.db.schema.tables.references.CITATIONS
import info.idoubtthat.models.CitationDAO
import org.jooq.DSLContext
import org.jooq.impl.DSL
import java.util.*

class GetCitationOp(val dao: CitationDAO): ReadOp<CitationDAO?> {
    override fun run(context: DSLContext): CitationDAO? {
        val result = context.selectFrom(CITATIONS)
            .where(CITATIONS.VALID_TO.isNull)
            .and(if (dao.id != null) CITATIONS.CITATION_ID.eq(dao.id.toString()) else DSL.noCondition())
            .and(if (dao.userId != null) CITATIONS.USER_ID.eq(dao.userId.toString()) else DSL.noCondition())
            .and(if (dao.url != null) CITATIONS.URL.eq(dao.url) else DSL.noCondition())
            .and(if (dao.commentary != null) CITATIONS.COMMENTARY.eq(dao.commentary) else DSL.noCondition())
            .fetchOne()
        return if (result != null) {
            CitationDAO(
                UUID.fromString(result.citationId!!),
                UUID.fromString(result.userId!!),
                result.url!!,
                result.commentary!!
            )
        } else {
            null
        }
    }
}