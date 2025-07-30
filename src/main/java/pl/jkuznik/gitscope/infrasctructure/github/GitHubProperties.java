package pl.jkuznik.gitscope.infrasctructure.github;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("properties.github")
@Getter
@Setter
class GitHubProperties {

    private String baseUrl;
}
