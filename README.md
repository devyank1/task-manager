``` mermaid
classDiagram
    class User {
        Long id
        String name
        String email
        String password
    }

    class Task {
        Long id
        String title
        String description
        Integer priority
        Status status
        Long userId
    }

    class Project {
        Long id
        String name
        List<Task> tasks
    }

    %% Enum for Task Status
    class Status {
        <<enum>>
        TODO
        IN_PROGRESS
        DONE
    }

    %% Relationships
    User "1" --> "0..*" Task : owns
    Project "1" --> "0..*" Task : contains
```
