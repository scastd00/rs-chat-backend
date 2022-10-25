package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.entity.TeaSubj;
import rs.chat.domain.entity.TeaSubjPK;

@SuppressWarnings("java:S100")
public interface TeacherSubjectRepository extends JpaRepository<TeaSubj, TeaSubjPK> {
	void deleteAllByTeaSubjPK_SubjectId(Long subjectId);
}
