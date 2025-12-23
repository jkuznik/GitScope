package pl.jkuznik.gitscope.gitHub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import pl.jkuznik.gitscope.WireMockIT;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

@SpringBootTest()
class GitHubIntegrationTest extends WireMockIT {

    @Autowired
    GitHubController controller;

    @Test
    void getPublicRepos_shouldReturnStatus_200_withExpectedBody_whenReposExists() {
        // given
        wireMockGitHubServer.stubFor(get(urlEqualTo("/users/jkuznik/repos"))
                .willReturn(okJson("""
                [
                  {
                    "name": "public-repo",
                    "fork": false,
                    "private": false,
                    "owner": { "login": "jkuznik" }
                  },
                  {
                    "name": "public-repo2",
                    "fork": false,
                    "private": false,
                    "owner": { "login": "jkuznik" }
                  },
                  {
                    "name": "fork-repo",
                    "fork": true,
                    "private": false,
                    "owner": { "login": "jkuznik" }
                  }
                ]
            """)));

        wireMockGitHubServer.stubFor(get(urlEqualTo("/repos/jkuznik/public-repo/branches"))
                .willReturn(okJson("""
                [
                  { "name": "main", "commit": { "sha": "foo" } },
                  { "name": "feature", "commit": { "sha": "bar" } }
                ]
            """)));

        // when
        ResponseEntity<List<GitHubRepositoryModel>> response = controller.getPublicRepositories("jkuznik");
        List<GitHubRepositoryModel> repos = response.getBody();

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(repos).hasSize(2);
        assertThat(repos.getFirst().name()).isEqualTo("public-repo");
        assertThat(repos.getFirst().owner().login()).isEqualTo("jkuznik");
        assertThat(repos.getFirst().branches()).hasSize(2);
        assertThat(repos.getFirst().branches().getFirst().name()).isEqualTo("main");
        assertThat(repos.getFirst().branches().getFirst().commit().sha()).isEqualTo("foo");
    }

    @Test
    void getPrivateRepos_shouldReturnStatus_200_withExpectedBody_whenTokenIsValid() {
        // given
        var token = "valid-token";

        wireMockGitHubServer.stubFor(get(urlEqualTo("/user/repos"))
                .withHeader("Authorization", equalTo("Bearer " + token))
                .willReturn(okJson("""
            [
              {
                "name": "private-repo-1",
                "fork": false,
                "private": true,
                "owner": { "login": "jkuznik" }
              },
              {
                "name": "private-repo-2",
                "fork": false,
                "private": true,
                "owner": { "login": "jkuznik" }
              }
            ]
            """)));

        // when
        List<GitHubRepositoryModel> privateRepos = controller.getAllRepositories("Bearer " + token)
                .getBody();

        // then
        assertThat(privateRepos).hasSize(2);
        assertThat(privateRepos.get(0).name()).isEqualTo("private-repo-1");
        assertThat(privateRepos.get(0).isPrivate()).isTrue();
        assertThat(privateRepos.get(0).owner().login()).isEqualTo("jkuznik");
    }


    @Test
    void getPublicRepositories_shouldReturnStatus_404_whenUserNotExists() {
        // given
        wireMockGitHubServer.stubFor(get(urlEqualTo("/users/unknown/repos"))
                .willReturn(aResponse().withStatus(404)));

        // when
        var result = catchException(() ->
                controller.getPublicRepositories("unknown")
        );

        // then
        assertThat(result).isInstanceOf(GitHubException.UsernameNotFound.class);

        assertThat(result.getMessage())
                .contains("Username 'unknown' not found");
    }

    @Test
    void getPrivateRepos_shouldReturnStatus_403_whenTokenIsNotValid() {
        // given
        var notValidToken = "notValidToken";

        wireMockGitHubServer.stubFor(get(urlEqualTo("/user/repos"))
                .willReturn(aResponse().withStatus(401)));

        // when
        var result = catchException(() ->
                controller.getAllRepositories(notValidToken)
        );

        // then
        assertThat(result).isInstanceOf(GitHubException.Unauthorized.class);

        assertThat(result.getMessage())
                .contains("Unauthorized GitHub token");
    }
}
