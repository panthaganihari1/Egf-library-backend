package com.egf.library.repository;

import com.egf.library.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("SELECT m FROM Member m WHERE " +
           "LOWER(m.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "m.phone LIKE CONCAT('%', :query, '%')")
    List<Member> searchMembers(@Param("query") String query);

    boolean existsByPhone(String phone);
}
