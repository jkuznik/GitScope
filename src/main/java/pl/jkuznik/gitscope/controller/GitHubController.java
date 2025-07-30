package pl.jkuznik.gitscope.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.jkuznik.gitscope.model.github.GitHubRepository;
import pl.jkuznik.gitscope.service.GitHubService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GitHubController {

    public static final String REPOSITORIES = "/repos";

    private final GitHubService gitHubService;

    @GetMapping(REPOSITORIES)
    public ResponseEntity<List<GitHubRepository>> getRepositories(
            @RequestParam("username") String username,
            @RequestHeader(value = "Authorization", required = false) String token
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(gitHubService.getUserRepositories(username, token));
    }
}
