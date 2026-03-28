package com.egf.library.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "book_issues")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDate issueDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    private LocalDate returnDate;

    @Column(nullable = false)
    private String issuedBy; // Incharge name

    @Enumerated(EnumType.STRING)
    private IssueStatus status = IssueStatus.ISSUED;

    private String remarks;

    @Column(columnDefinition = "datetime")
    private LocalDateTime createdAt;

    @Column(columnDefinition = "datetime")
    private LocalDateTime joinedAt;

    public enum IssueStatus {
        ISSUED, RETURNED, OVERDUE
    }
}
