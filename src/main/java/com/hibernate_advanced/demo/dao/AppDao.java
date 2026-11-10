package com.hibernate_advanced.demo.dao;

import com.hibernate_advanced.demo.entity.Course;
import com.hibernate_advanced.demo.entity.Instructor;
import com.hibernate_advanced.demo.entity.InstructorDetail;
import com.hibernate_advanced.demo.entity.Student;
import java.util.List;

public interface AppDao {

	void save(Instructor instructor);

	void save(Course course);

	boolean courseExistsByTitle(String title);

	Course findCourseAndStudentsByTitle(String title);

	Student findStudentAndCoursesByEmail(String email);

	Course findCourseAndReviews();

	void update(Instructor instructor);

	void update(Student student);

	void deleteCourseById(int id);

	void deleteStudentById(int id);

	Instructor findInstructorById(int id);

	Instructor findInstructorWithCourses(int id);

	List<Course> findCoursesByInstructorId(int id);

	Instructor findInstructorByIdJoinFetch(int id);

	void deleteInstructorById(int id);

	InstructorDetail findInstructorDetailById(int id);

	void populateExampleData();

	void deleteInstructorDetail(int id);
}
