package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.entity.StuSubj;
import rs.chat.domain.entity.StuSubjId;

@SuppressWarnings("java:S100")
public interface StudentSubjectRepository extends JpaRepository<StuSubj, StuSubjId> {
	void deleteAllById_SubjectId(Long subjectId);
}
