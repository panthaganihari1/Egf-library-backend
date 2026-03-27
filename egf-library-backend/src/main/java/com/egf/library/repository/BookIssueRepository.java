package com.egf.library.repository;

import com.egf.library.model.BookIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface BookIssueRepository extends JpaRepository<BookIssue, Long> {

    List<BookIssue> findByMemberId(Long memberId);

    List<BookIssue> findByBookId(Long bookId);

    List<BookIssue> findByStatus(BookIssue.IssueStatus status);

    @Query("SELECT bi FROM BookIssue bi WHERE bi.status = 'ISSUED' AND bi.dueDate < CURRENT_DATE")
    List<BookIssue> findOverdueBooks();

    @Query("SELECT COUNT(bi) FROM BookIssue bi WHERE bi.status = 'ISSUED'")
    long countActiveIssues();

    @Query("SELECT COUNT(bi) FROM BookIssue bi WHERE bi.status = 'RETURNED'")
    long countReturnedBooks();
}
