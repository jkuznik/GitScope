package pl.jkuznik.gitscope.infrasctructure.github;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;

@Slf4j
@RequiredArgsConstructor
public class GitHubClient {

    private final RestClient restClient;

}
