package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.entity.TeaSubj;
import rs.chat.domain.entity.TeaSubjId;

import java.util.List;

@SuppressWarnings("java:S100")
public interface TeacherSubjectRepository extends JpaRepository<TeaSubj, TeaSubjId> {
	List<TeaSubj> findAllById_TeacherId(Long id);

	void deleteAllById_SubjectId(Long subjectId);

	boolean existsById_TeacherIdAndId_SubjectId(Long teacherId, Long subjectId);
}
