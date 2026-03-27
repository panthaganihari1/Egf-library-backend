package com.egf.library.controller;

import com.egf.library.model.Book;
import com.egf.library.model.BookIssue;
import com.egf.library.model.Member;
import com.egf.library.repository.BookIssueRepository;
import com.egf.library.repository.BookRepository;
import com.egf.library.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/issues")
public class BookIssueController {

    @Autowired
    private BookIssueRepository bookIssueRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping
    public List<BookIssue> getAllIssues() {
        return bookIssueRepository.findAll();
    }

    @GetMapping("/active")
    public List<BookIssue> getActiveIssues() {
        return bookIssueRepository.findByStatus(BookIssue.IssueStatus.ISSUED);
    }

    @GetMapping("/overdue")
    public List<BookIssue> getOverdueIssues() {
        return bookIssueRepository.findOverdueBooks();
    }

    @GetMapping("/member/{memberId}")
    public List<BookIssue> getIssuesByMember(@PathVariable Long memberId) {
        return bookIssueRepository.findByMemberId(memberId);
    }

    @PostMapping("/issue")
    public ResponseEntity<?> issueBook(@RequestBody Map<String, Object> request, Authentication auth) {
        Long bookId = Long.valueOf(request.get("bookId").toString());
        Long memberId = Long.valueOf(request.get("memberId").toString());
        int dueDays = request.containsKey("dueDays") ? Integer.parseInt(request.get("dueDays").toString()) : 14;

        Book book = bookRepository.findById(bookId)
                .orElse(null);
        if (book == null) return ResponseEntity.badRequest().body(Map.of("error", "Book not found"));
        if (book.getAvailableCopies() <= 0) return ResponseEntity.badRequest().body(Map.of("error", "No copies available"));

        Member member = memberRepository.findById(memberId)
                .orElse(null);
        if (member == null) return ResponseEntity.badRequest().body(Map.of("error", "Member not found"));

        BookIssue issue = new BookIssue();
        issue.setBook(book);
        issue.setMember(member);
        issue.setIssueDate(LocalDate.now());
        issue.setDueDate(LocalDate.now().plusDays(dueDays));
        issue.setIssuedBy(auth.getName());
        issue.setStatus(BookIssue.IssueStatus.ISSUED);

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        if (book.getAvailableCopies() == 0) book.setStatus(Book.BookStatus.OUT_OF_STOCK);
        else book.setStatus(Book.BookStatus.ISSUED);
        bookRepository.save(book);

        return ResponseEntity.ok(bookIssueRepository.save(issue));
    }

    @PutMapping("/return/{issueId}")
    public ResponseEntity<?> returnBook(@PathVariable Long issueId, @RequestBody(required = false) Map<String, String> request) {
        return bookIssueRepository.findById(issueId).map(issue -> {
            issue.setReturnDate(LocalDate.now());
            issue.setStatus(BookIssue.IssueStatus.RETURNED);
            if (request != null && request.containsKey("remarks")) {
                issue.setRemarks(request.get("remarks"));
            }

            Book book = issue.getBook();
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            book.setStatus(Book.BookStatus.AVAILABLE);
            bookRepository.save(book);

            return ResponseEntity.ok(bookIssueRepository.save(issue));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBooks", bookRepository.count());
        stats.put("totalMembers", memberRepository.count());
        stats.put("activeIssues", bookIssueRepository.countActiveIssues());
        stats.put("returnedBooks", bookIssueRepository.countReturnedBooks());
        stats.put("overdueBooks", bookIssueRepository.findOverdueBooks().size());
        stats.put("availableBooks", bookRepository.findByStatus(Book.BookStatus.AVAILABLE).size());
        return ResponseEntity.ok(stats);
    }
}
