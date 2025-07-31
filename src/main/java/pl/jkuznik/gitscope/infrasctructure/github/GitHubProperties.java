package pl.jkuznik.gitscope.infrasctructure.github;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("properties.github")
@Getter
@Setter
public class GitHubProperties {

    private String baseUrl;
    private String token;
}
