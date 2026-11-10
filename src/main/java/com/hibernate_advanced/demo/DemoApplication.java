package com.hibernate_advanced.demo;

import com.hibernate_advanced.demo.dao.AppDao;
import com.hibernate_advanced.demo.entity.Course;
import com.hibernate_advanced.demo.entity.Instructor;
import com.hibernate_advanced.demo.entity.InstructorDetail;
import com.hibernate_advanced.demo.entity.Review;
import com.hibernate_advanced.demo.entity.Student;
import java.util.List;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(AppDao appDao) {
		return args -> {
			// createInstructor(appDao);
			// findInstructor(appDao);
			// deleteInstructor(appDao);
			// findInstructorDetail(appDao);
			// deleteInstructorDetail(appDao);
			// createInstructorWithCourses(appDao);
			// findInstructorWithCourses(appDao);
			// findCoursesForInstructor(appDao);
			// deleteInstructor(appDao);
			deleteCourse(appDao);
			// deleteStudent(appDao);
		};
	}

	private void createInstructor(AppDao appDao) {
		Instructor instructor = new Instructor("Vicky", "Magtanggol", "vicky.mags@example.com");
		InstructorDetail instructorDetail = new InstructorDetail("Math", "Solving problems");
		instructor.setInstructorDetail(instructorDetail);
		appDao.save(instructor);
		System.out.println("Information saved!");
	}

	private void createInstructorWithCourses(AppDao appDao) {
		Instructor instructor = new Instructor("Perry", "Winkle", "perry.winkle@example.com");
		InstructorDetail instructorDetail = new InstructorDetail("Science", "Sprinkling dust");
		instructor.setInstructorDetail(instructorDetail);

		Course course1 = new Course("Physics");
		Course course2 = new Course("Biology");

		instructor.add(course1);
		instructor.add(course2);

		appDao.save(instructor);
		System.out.println("Courses saved: " + instructor.getCourses());
	}

	private void createCourseAndReviews(AppDao appDao) {
		Course course = new Course("How to Speed Run Spring in 1 Day");
		course.addReview(new Review("Great speed 2x...loved it!!!"));
		course.addReview(new Review("I wanted the speed 0.75x"));
		course.addReview(new Review("Hoping to do a speed of 3x :D. Anyways, nice!"));

		System.out.println("Course saved!");
		appDao.save(course);
	}

	private void retrieveCourseAndReviews(AppDao appDao) {
		System.out.println("Get a course with its reviews");
		Course course = appDao.findCourseAndReviews();
		if (course == null) {
			System.out.println("No course with reviews found");
			return;
		}

		System.out.println(course);
		System.out.println(course.getReviews());
	}

	private void createCourseAndStudents(AppDao appDao) {
		String title = "How to Be A FullStack Dev in Half A Day";
		if (appDao.courseExistsByTitle(title)) {
			System.out.println("Demo course and students already exist.");
			return;
		}

		Course course = new Course(title);
		Student student1 = new Student("Light", "Yagami", "light.yagami@example.com");
		Student student2 = new Student("Leslie", "Andrews", "leslie.andrews@example.com");

		student1.addCourse(course);
		student2.addCourse(course);

		appDao.save(course);
		System.out.println("Course and students saved: " + course + ", " + student1 + ", " + student2);
	}

	private void findCourseAndStudents(AppDao appDao) {
		String title = "How to Be A FullStack Dev in Half A Day";
		Course course = appDao.findCourseAndStudentsByTitle(title);
		if (course == null) {
			System.out.println("No course found with title: " + title);
			return;
		}

		System.out.println("Course: " + course);
		System.out.println("Students: " + course.getStudents());
	}

	private void findStudentAndCourses(AppDao appDao) {
		String email = "leslie.andrews@example.com";
		Student student = appDao.findStudentAndCoursesByEmail(email);
		if (student == null) {
			System.out.println("No student found with email: " + email);
			return;
		}

		System.out.println("Student: " + student);
		System.out.println("Courses: " + student.getCourses());
	}

	private void addMoreCoursesForStudent(AppDao appDao) {
		String email = "light.yagami@example.com";
		Student student = appDao.findStudentAndCoursesByEmail(email);
		if (student == null) {
			System.out.println("No student found with email: " + email);
			return;
		}

		addCourseIfMissing(student, "How to Test without Running Application");
		addCourseIfMissing(student, "How to Develop without Computer");
		addCourseIfMissing(student, "How to Be a Legit Developer");

		System.out.println("Successfully enrolled in new courses!");
		appDao.update(student);
	}

	private void deleteStudent(AppDao appDao) {
		String email = "light.yagami@example.com";
		Student student = appDao.findStudentAndCoursesByEmail(email);
		if (student == null) {
			System.out.println("No student found with email: " + email);
			return;
		}

		System.out.println("Delete student: " + student.getId());
		appDao.deleteStudentById(student.getId());
		System.out.println("Deleted successfully!");
	}

	private void deleteCourse(AppDao appDao) {
		int id = 15;
		System.out.println("Delete course: " + id);
		appDao.deleteCourseById(id);
		System.out.println("Deleted successfully!");
	}

	private void addCourseIfMissing(Student student, String title) {
		boolean alreadyEnrolled = student.getCourses().stream()
				.anyMatch(course -> title.equals(course.getTitle()));
		if (!alreadyEnrolled) {
			student.addCourse(new Course(title));
		}
	}

	private void findInstructor(AppDao appDao) {
		int id = 1;
		System.out.println("Find id: " + id);
		Instructor instructor = appDao.findInstructorById(id);
		System.out.println("Instructor: " + instructor);
		if (instructor != null) {
			System.out.println("Details: " + instructor.getInstructorDetail());
		}
	}

	private void findInstructorWithCourses(AppDao appDao) {
		int id = 24;
		System.out.println("Find instructor id: " + id);
		Instructor instructor = appDao.findInstructorWithCourses(id);
		if (instructor == null) {
			System.out.println("No instructor found with id " + id);
			return;
		}
		System.out.println("Instructor: " + instructor);
		System.out.println("Courses: " + instructor.getCourses());
	}

	private void findCoursesForInstructor(AppDao appDao) {
		int id = 24;
		System.out.println("Find instructor id: " + id);

		Instructor instructor = appDao.findInstructorById(id);
		if (instructor == null) {
			System.out.println("No instructor found with id " + id);
			return;
		}

		System.out.println("Instructor: " + instructor);
		System.out.println("Courses from id: " + id);
		List<Course> courses = appDao.findCoursesByInstructorId(id);
		instructor.setCourses(courses);
		System.out.println(instructor.getCourses());
	}

	private void findInstructorWithCoursesJoinFetch(AppDao appDao) {
		int id = 24;
		System.out.println("Find instructor id: " + id);
		Instructor instructor = appDao.findInstructorByIdJoinFetch(id);
		if (instructor == null) {
			System.out.println("No instructor found with id " + id);
			return;
		}

		System.out.println("Instructor: " + instructor);
		System.out.println("Courses: " + instructor.getCourses());
	}

	private void updateInstructor(AppDao appDao) {
		int id = 24;
		System.out.println("Find instructor id: " + id);

		Instructor instructor = appDao.findInstructorById(id);
		if (instructor == null) {
			System.out.println("No instructor found with id " + id);
			return;
		}

		instructor.setLastName("WHATDOESTHEFOXSAY");
		System.out.println("Updated!");
		appDao.update(instructor);
	}

	private void deleteInstructor(AppDao appDao) {
		int id = 25;
		System.out.println("Delete id: " + id);
		if (appDao.findInstructorById(id) == null) {
			System.out.println("No instructor found with id " + id);
			return;
		}
		appDao.deleteInstructorById(id);
		System.out.println("Deleted successfully!");
	}

	private void findInstructorDetail(AppDao appDao) {
		int id = 23;
		InstructorDetail instructorDetail = appDao.findInstructorDetailById(id);
		if (instructorDetail == null) {
			System.out.println("No instructor detail found with id " + id);
			return;
		}

		System.out.println("Details: " + instructorDetail);
		System.out.println("Instructor: " + instructorDetail.getInstructor());
	}

	private void deleteInstructorDetail(AppDao appDao) {
		int id = 21;
		System.out.println("Delete id: " + id);
		appDao.deleteInstructorDetail(id);
		System.out.println("Deleted successfully!");
	}

}
