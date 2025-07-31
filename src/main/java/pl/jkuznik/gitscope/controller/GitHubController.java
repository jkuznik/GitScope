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

    public static final String PUBLIC = "/public";
    public static final String PRIVATE = "/private";

    private final GitHubService gitHubService;

    @GetMapping(PUBLIC)
    public ResponseEntity<List<GitHubRepository>> getPublicRepositories(
            @RequestParam("username") String credentials
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(gitHubService.getReposByUsername(credentials));
    }

    @GetMapping(PRIVATE)
    public ResponseEntity<List<GitHubRepository>> getAllRepositories(
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(gitHubService.getAllRepos(token));
    }
}
