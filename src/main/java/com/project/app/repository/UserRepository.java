package com.project.app.repository;

import com.project.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // 사용자명으로 조회
	List<User> findByUserName(@Param("userName") String userName);

	Optional<User> findByUserIdAndPassword(String userId, String password);
    
//    // JPQL을 사용한 커스텀 쿼리 예시
//    @Query("SELECT u FROM User u WHERE u.email LIKE %:domain%")
//    List<User> findUsersByEmailDomain(@Param("domain") String domain);
//    
//    // 네이티브 SQL 쿼리 예시
//    @Query(value = "SELECT * FROM users WHERE created_at > :date", nativeQuery = true)
//    List<User> findUsersCreatedAfter(@Param("date") String date);
}