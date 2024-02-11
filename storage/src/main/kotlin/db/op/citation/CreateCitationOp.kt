package info.idoubtthat.db.op.citation

import info.idoubtthat.db.op.WriteOp
import info.idoubtthat.db.schema.tables.references.CITATIONS
import info.idoubtthat.models.CitationDAO
import org.jooq.Configuration
import java.time.LocalDateTime

class CreateCitationOp(dao: CitationDAO) : WriteOp<CitationDAO>(dao) {
    override fun run(configuration: Configuration) {
        configuration.dsl()
            .insertInto(CITATIONS)
            .columns(
                CITATIONS.CITATION_ID,
                CITATIONS.USER_ID,
                CITATIONS.URL,
                CITATIONS.COMMENTARY,
                CITATIONS.VALID_FROM,
                CITATIONS.VALID_TO)
            .values(
                dao.id.toString(),
                dao.userId.toString(),
                dao.url,
                dao.commentary,
                LocalDateTime.now(),
                null)
            .execute()
    }
}
