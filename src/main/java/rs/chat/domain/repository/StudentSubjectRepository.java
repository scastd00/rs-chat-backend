package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.entity.StuSubj;
import rs.chat.domain.entity.StuSubjPK;

@SuppressWarnings("java:S100")
public interface StudentSubjectRepository extends JpaRepository<StuSubj, StuSubjPK> {
	void deleteAllByStuSubjPK_SubjectId(Long subjectId);
}
