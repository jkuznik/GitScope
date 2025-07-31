package pl.jkuznik.gitscope;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import pl.jkuznik.gitscope.infrasctructure.github.GitHubProperties;
import pl.jkuznik.gitscope.model.github.GitHubBranch;
import pl.jkuznik.gitscope.model.github.GitHubRepository;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.jkuznik.gitscope.controller.GitHubController.PRIVATE;
import static pl.jkuznik.gitscope.controller.GitHubController.PUBLIC;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private GitHubProperties gitHubProperties;

    private static final String TEST_USERNAME = "jkuznik";
    private static final String EXPECTED_REPO = "GitScope";

    @Test
    void getBaseUrl_ShouldReturnExpectedBaseUrl() {
        // given
        String baseUrl = "https://api.github.com";

        // when
        String result = gitHubProperties.getBaseUrl();

        // then
        Assertions.assertThat(baseUrl).isEqualTo(result);
    }

    @Test
    void shouldFetchPublicRepositoriesFromGitHubApi() {
        // when
        ResponseEntity<GitHubRepository[]> response = restTemplate.getForEntity(
                "http://localhost:" + port + PUBLIC + "?username=" + TEST_USERNAME,
                GitHubRepository[].class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThan(0);

        List<GitHubRepository> repositories = Arrays.asList(response.getBody());

        repositories.forEach(repo -> {
            assertThat(repo.ownerLogin()).isEqualTo(TEST_USERNAME);
        });

        assertThat(repositories.contains(new GitHubRepository(EXPECTED_REPO, TEST_USERNAME, List.of()))).isTrue();
    }

// TODO: Keep this test disabled until a valid GitHub token with access to the 'jkuznik' user's private repositories is provided.

//    @Test
//    void shouldFetchPrivateRepositoriesFromGitHubApi_whenTokenProvided_forUser_jkuznik() {
//        // given
//        String token = gitHubProperties.getToken();
//        var ATIPERA = "PrivateRepoDedicatedToAtipera";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set(HttpHeaders.ACCEPT, "application/vnd.github+json");
//        headers.set("X-GitHub-Api-Version", "2022-11-28");
//        headers.setBearerAuth(token);
//        HttpEntity<Void> request = new HttpEntity<>(headers);
//
//        var expectedBranchInPrivateRepo = new GitHubBranch("N/A (private repository)", "Branch details not accessible");
//
//        // when
//        ResponseEntity<List<GitHubRepository>> result = restTemplate.exchange(
//                "http://localhost:" + port + PRIVATE,
//                HttpMethod.GET,
//                request,
//                new ParameterizedTypeReference<>() {}
//        );
//
//        // then
//        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//        assertThat(result.getBody())
//                .isNotNull()
//                .isNotEmpty()
//                .anySatisfy(repo -> assertThat(repo.repositoryName()).isEqualTo(ATIPERA));
//
//        assertThat(result.getBody())
//                .flatExtracting(GitHubRepository::branches)
//                .anySatisfy(branch -> assertThat(branch).isEqualTo(expectedBranchInPrivateRepo));
//    }

// TODO: Keep this test disabled until a valid GitHub token with access to any user's private repositories is available.

//    @Test
//    void shouldFetchPrivateRepositoriesFromGitHubApi_whenTokenProvided() {
//        // given
//        String token = gitHubProperties.getToken();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set(HttpHeaders.ACCEPT, "application/vnd.github+json");
//        headers.set("X-GitHub-Api-Version", "2022-11-28");
//        headers.setBearerAuth(token);
//        HttpEntity<Void> request = new HttpEntity<>(headers);
//
//        var expectedBranchInPrivateRepo = new GitHubBranch("N/A (private repository)", "Branch details not accessible");
//
//        // when
//        ResponseEntity<List<GitHubRepository>> result = restTemplate.exchange(
//                "http://localhost:" + port + PRIVATE,
//                HttpMethod.GET,
//                request,
//                new ParameterizedTypeReference<>() {}
//        );
//
//        // then
//        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//        assertThat(result.getBody())
//                .flatExtracting(GitHubRepository::branches)
//                .anySatisfy(branch -> assertThat(branch).isEqualTo(expectedBranchInPrivateRepo));
//    }
}
