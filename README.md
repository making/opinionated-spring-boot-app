# Opinionated Spring Boot App


## How to rename the project

Edit `rewrite.yml`, then

```
./mvnw -U org.openrewrite.maven:rewrite-maven-plugin:run -Drewrite.activeRecipes=com.example.RenameProject
./mvnw clean spring-javaformat:apply test
git add -A
git clean -fd 
```

> [!NOTE]
> You can check the changes before running the above command by 
> ```
> ./mvnw -U org.openrewrite.maven:rewrite-maven-plugin:dryRun -Drewrite.activeRecipes=com.example.RenameProject
> ```
> Check `./target/rewrite/rewrite.patch`