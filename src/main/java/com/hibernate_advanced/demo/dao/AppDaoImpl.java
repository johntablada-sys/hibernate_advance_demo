package com.hibernate_advanced.demo.dao;

import com.hibernate_advanced.demo.entity.Course;
import com.hibernate_advanced.demo.entity.Instructor;
import com.hibernate_advanced.demo.entity.InstructorDetail;
import com.hibernate_advanced.demo.entity.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class AppDaoImpl implements AppDao {

	private final EntityManager entityManager;

	public AppDaoImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	@Transactional
	public void save(Instructor instructor) {
		entityManager.persist(instructor);
	}

	@Override
	@Transactional
	public void save(Course course) {
		entityManager.persist(course);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean courseExistsByTitle(String title) {
		Long count = entityManager.createQuery(
				"SELECT COUNT(c) FROM Course c WHERE c.title = :title", Long.class)
				.setParameter("title", title)
				.getSingleResult();
		return count > 0;
	}

	@Override
	@Transactional(readOnly = true)
	public Course findCourseAndStudentsByTitle(String title) {
		TypedQuery<Course> query = entityManager.createQuery(
				"SELECT c FROM Course c JOIN FETCH c.students WHERE c.title = :title",
				Course.class);
		query.setParameter("title", title);
		return query.getResultList().stream().findFirst().orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public Student findStudentAndCoursesByEmail(String email) {
		TypedQuery<Student> query = entityManager.createQuery(
				"SELECT s FROM Student s JOIN FETCH s.courses WHERE s.email = :email",
				Student.class);
		query.setParameter("email", email);
		return query.getResultList().stream().findFirst().orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public Course findCourseAndReviews() {
		TypedQuery<Course> query = entityManager.createQuery(
				"SELECT c FROM Course c JOIN FETCH c.reviews ORDER BY c.id DESC",
				Course.class);
		List<Course> courses = query.setMaxResults(1).getResultList();
		return courses.isEmpty() ? null : courses.get(0);
	}

	@Override
	@Transactional
	public void update(Instructor instructor) {
		entityManager.merge(instructor);
	}

	@Override
	@Transactional
	public void update(Student student) {
		entityManager.merge(student);
	}

	@Override
	@Transactional
	public void deleteCourseById(int id) {
		TypedQuery<Course> query = entityManager.createQuery(
				"SELECT c FROM Course c LEFT JOIN FETCH c.students WHERE c.id = :id",
				Course.class);
		query.setParameter("id", id);
		Course course = query.getResultList().stream().findFirst().orElse(null);
		if (course == null) {
			return;
		}

		for (Student student : course.getStudents()) {
			student.getCourses().remove(course);
		}
		course.getStudents().clear();

		entityManager.remove(course);
	}

	@Override
	@Transactional
	public void deleteStudentById(int id) {
		TypedQuery<Student> query = entityManager.createQuery(
				"SELECT s FROM Student s LEFT JOIN FETCH s.courses WHERE s.id = :id",
				Student.class);
		query.setParameter("id", id);
		Student student = query.getResultList().stream().findFirst().orElse(null);
		if (student == null) {
			return;
		}

		for (Course course : student.getCourses()) {
			course.getStudents().remove(student);
		}

		entityManager.remove(student);
	}

	@Override
	@Transactional(readOnly = true)
	public Instructor findInstructorById(int id) {
		return entityManager.find(Instructor.class, id);
	}

	@Override
	@Transactional(readOnly = true)
	public Instructor findInstructorWithCourses(int id) {
		return entityManager.find(Instructor.class, id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Course> findCoursesByInstructorId(int id) {
		TypedQuery<Course> query = entityManager.createQuery(
				"FROM Course WHERE instructor.id = :data", Course.class);
		query.setParameter("data", id);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = true)
	public Instructor findInstructorByIdJoinFetch(int id) {
		TypedQuery<Instructor> query = entityManager.createQuery(
				"SELECT i FROM Instructor i " +
						"JOIN FETCH i.courses " +
						"JOIN FETCH i.instructorDetail " +
						"WHERE i.id = :data",
				Instructor.class);
		query.setParameter("data", id);
		return query.getResultList().stream().findFirst().orElse(null);
	}

	@Override
	@Transactional
	public void deleteInstructorById(int id) {
		Instructor instructor = entityManager.find(Instructor.class, id);
		if (instructor == null) {
			return;
		}

		for (Course course : instructor.getCourses()) {
			course.setInstructor(null);
		}

		entityManager.remove(instructor);
	}

	@Override
	@Transactional(readOnly = true)
	public InstructorDetail findInstructorDetailById(int id) {
		return entityManager.find(InstructorDetail.class, id);
	}

	@Override
	@Transactional
	public void populateExampleData() {
		mergeInstructorDetail(21, "PE", "Granting wishes");
		mergeInstructorDetail(22, "Psychology", "Generating meme");
		mergeInstructorDetail(23, "Literature", "Making troubles");

		mergeInstructor(21, "Wanda", "Cosma", "wanda.cosma@example.com");
		mergeInstructor(22, "Cosmo", "Cosma", "cosmo.cosma@example.com");
		mergeInstructor(23, "Timmy", "Turner", "timmy.turner@example.com");
	}

	@Override
	@Transactional
	public void deleteInstructorDetail(int id) {
		InstructorDetail instructorDetail = entityManager.find(InstructorDetail.class, id);
		if (instructorDetail != null) {
			Instructor instructor = instructorDetail.getInstructor();
			if (instructor != null) {
				instructor.setInstructorDetail(null);
			}
			entityManager.remove(instructorDetail);
		}
	}

	private void mergeInstructorDetail(int id, String subject, String hobby) {
		entityManager.createNativeQuery("""
				MERGE INTO instructor_detail target
				USING (SELECT :id id, :subject subject, :hobby hobby FROM dual) incoming
				ON (target.id = incoming.id)
				WHEN MATCHED THEN UPDATE SET
					target.subject = incoming.subject,
					target.hobby = incoming.hobby
				WHEN NOT MATCHED THEN INSERT (id, subject, hobby)
					VALUES (incoming.id, incoming.subject, incoming.hobby)
				""")
				.setParameter("id", id)
				.setParameter("subject", subject)
				.setParameter("hobby", hobby)
				.executeUpdate();
	}

	private void mergeInstructor(int id, String firstName, String lastName, String email) {
		entityManager.createNativeQuery("""
				MERGE INTO instructor target
				USING (
					SELECT :id id, :firstName first_name, :lastName last_name,
						:email email, :detailId instructor_detail_id
					FROM dual
				) incoming
				ON (target.id = incoming.id)
				WHEN MATCHED THEN UPDATE SET
					target.first_name = incoming.first_name,
					target.last_name = incoming.last_name,
					target.email = incoming.email,
					target.instructor_detail_id = incoming.instructor_detail_id
				WHEN NOT MATCHED THEN INSERT
					(id, first_name, last_name, email, instructor_detail_id)
					VALUES (
						incoming.id, incoming.first_name, incoming.last_name,
						incoming.email, incoming.instructor_detail_id
					)
				""")
				.setParameter("id", id)
				.setParameter("firstName", firstName)
				.setParameter("lastName", lastName)
				.setParameter("email", email)
				.setParameter("detailId", id)
				.executeUpdate();
	}
}
