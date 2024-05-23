# Opinionated Spring Boot App


## How to rename the project

Edit `rewrite.yml`, then

```
./mvnw -U org.openrewrite.maven:rewrite-maven-plugin:run -Drewrite.activeRecipes=com.example.RenameProject
./mvnw clean spring-javaformat:apply test
git add -A
git clean -fd 
```