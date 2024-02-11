package info.idoubtthat.db.op.citation

import info.idoubtthat.db.op.WriteOp
import info.idoubtthat.db.schema.tables.references.CITATIONS
import info.idoubtthat.db.schema.tables.references.USERS
import info.idoubtthat.models.CitationDAO
import org.jooq.Configuration
import java.time.LocalDateTime

class UpdateCitationOp(dao: CitationDAO) : WriteOp<CitationDAO>(dao) {
    override fun run(configuration: Configuration) {
        val now = LocalDateTime.now()
        configuration.dsl().update(CITATIONS)
            .set(CITATIONS.VALID_TO, now)
            .where(CITATIONS.CITATION_ID.eq(dao.id.toString())
                .and(CITATIONS.VALID_TO.isNull))
        configuration.dsl().insertInto(CITATIONS)
            .columns(
                CITATIONS.CITATION_ID,
                CITATIONS.USER_ID,
                CITATIONS.URL,
                USERS.VALID_FROM,
                USERS.VALID_TO)
            .values(
                dao.id.toString(),
                dao.userId.toString(),
                dao.url,
                LocalDateTime.now(),
                null)
            .execute()
    }
}
