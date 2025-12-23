package pl.jkuznik.gitscope.gitHub;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
class GitHubController {

    public static final String PUBLIC = "/public";
    public static final String PRIVATE = "/private";

    private final GitHubService gitHubService;

    @GetMapping(PUBLIC)
    public ResponseEntity<List<GitHubRepositoryModel>> getPublicRepositories(
            @RequestParam("username") String username
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(gitHubService.getPublicReposByUsername(username));
    }

    @GetMapping(PRIVATE)
    public ResponseEntity<List<GitHubRepositoryModel>> getAllRepositories(
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(gitHubService.getAllReposByToken(token));
    }
}
