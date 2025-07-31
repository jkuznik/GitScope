## Zadanie rekrutacyjne

### Treść zadania

Acceptance criteria:

As an api consumer, I would like to list all his github repositories, which are not forks. Information, which I require in the response, is:

Repository Name

Owner Login

For each branch it’s name and last commit sha



As an api consumer, given not existing github user, I would like to receive 404 response in such a format:

{

    “status”: ${responseCode}

    “message”: ${whyHasItHappened}

}

### Quick Start
The easiest way to run this app is by cloning the repository and opening it as a new project in your IDE (e.g., IntelliJ IDEA). No additional configuration is required.

Make sure port 8080 is free on your local machine — the app will bind to it by default.

Once started, you can access the application at:

Accessing Public Repositories

```
http://localhost:8080/public?username=<your_github_username>
```

Accessing Private Repositories

```
http://localhost:8080/private
```
When calling the /private endpoint GitHub token is required, and you can include in the HTTP request header as follows:

```
curl -L \
  -H "Authorization: Bearer <github_token>" \
  http://localhost:8080/private
```

Note: Accessing private repositories requires a valid GitHub token with the necessary permissions.