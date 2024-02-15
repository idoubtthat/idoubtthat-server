package db.op.citation

import db.schema.tables.references.CITATIONS
import db.op.WriteOp
import db.models.CitationDAO
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
