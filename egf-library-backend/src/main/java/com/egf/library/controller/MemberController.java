package com.egf.library.controller;

import com.egf.library.model.Member;
import com.egf.library.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Member> getMember(@PathVariable Long id) {
        return memberRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<Member> searchMembers(@RequestParam String query) {
        return memberRepository.searchMembers(query);
    }

    @PostMapping
    public ResponseEntity<?> createMember(@RequestBody Member member) {
        if (memberRepository.existsByPhone(member.getPhone())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Phone number already registered"));
        }
        return ResponseEntity.ok(memberRepository.save(member));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(@PathVariable Long id, @RequestBody Member memberDetails) {
        return memberRepository.findById(id).map(member -> {
            member.setName(memberDetails.getName());
            member.setPhone(memberDetails.getPhone());
            member.setEmail(memberDetails.getEmail());
            member.setAddress(memberDetails.getAddress());
            member.setActive(memberDetails.isActive());
            return ResponseEntity.ok(memberRepository.save(member));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable Long id) {
        if (memberRepository.existsById(id)) {
            memberRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Member deleted successfully"));
        }
        return ResponseEntity.notFound().build();
    }
}
