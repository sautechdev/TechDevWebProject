package com.techdevweb.techdevbackend.Project.Controller;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Project.Entity.ProjectMember;
import com.techdevweb.techdevbackend.Project.Service.ProjectMemberService;
import com.techdevweb.techdevbackend.Security.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/members")
@RequiredArgsConstructor
public class ProjectMemberController {

    private final ProjectMemberService memberService;
    private final CurrentUserResolver currentUserResolver;

    @GetMapping
    public List<ProjectMember> getMembers(@PathVariable Long projectId) {
        return memberService.getMembers(projectId);
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> remove(@PathVariable Long projectId,
                                        @PathVariable Long memberId) {
        User currentUser = currentUserResolver.getCurrentUser();
        memberService.removeMember(projectId, memberId, currentUser);
        return ResponseEntity.noContent().build();
    }
}
