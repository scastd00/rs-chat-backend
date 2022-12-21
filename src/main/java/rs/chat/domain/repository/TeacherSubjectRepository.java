package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.entity.TeaSubj;
import rs.chat.domain.entity.TeaSubjPK;

import java.util.List;

@SuppressWarnings("java:S100")
public interface TeacherSubjectRepository extends JpaRepository<TeaSubj, TeaSubjPK> {
	List<TeaSubj> findAllByTeaSubjPK_TeacherId(Long id);

	void deleteAllByTeaSubjPK_SubjectId(Long subjectId);

	boolean existsByTeaSubjPK_TeacherIdAndTeaSubjPK_SubjectId(Long teacherId, Long subjectId);
}
