# Micronaut Template

## References

-   [DOCUMENTATION](https://docs.micronaut.io/latest/guide/#introduction)
-   Application
    -   [CREATING YOUR FIRST MICRONAUT APPLICATION](https://guides.micronaut.io/latest/creating-your-first-micronaut-app-maven-java.html)
-   JWT Authentication
    -   [MICRONAUT JWT AUTHENTICATION](https://guides.micronaut.io/latest/micronaut-security-jwt-maven-java.html)
    -   [MICRONAUT BASIC AUTH](https://guides.micronaut.io/latest/micronaut-security-basicauth-maven-java.html)
    -   [MICRONAUT JWT AUTHENTICATION VIA COOKIES](https://guides.micronaut.io/latest/micronaut-security-jwt-cookie-maven-java.html)
    -   [MICRONAUT TOKEN PROPAGATION](https://guides.micronaut.io/latest/micronaut-token-propagation-maven-java.html)
-   Hibernate JPA
    -   [ACCESS A DATABASE WITH JPA AND HIBERNATE](https://guides.micronaut.io/latest/micronaut-jpa-hibernate-maven-java.html)
-   Liquibase
    -   [SCHEMA MIGRATION WITH LIQUIBASE](https://guides.micronaut.io/latest/micronaut-liquibase-maven-java.html)
    -   [SQL FILES](https://docs.liquibase.com/change-types/sql-file.html)
    -   [CHANGESET](https://docs.liquibase.com/concepts/changelogs/sql-format.html)

## Development

### 1. [Install Docker](https://docs.docker.com/get-docker/)
### 2. [Install asdf](https://asdf-vm.com/#/core-manage-asdf?id=install)
### 3. [Install Java](https://asdf-vm.com/#/core-manage-asdf?id=install)
```bash
# install java plugin
asdf plugin add java

# install java version 11
asdf install java adoptopenjdk-11.0.16+101

# use java 11
asdf local java adoptopenjdk-11.0.16+101
```

### 4. [Install Graalvm](https://github.com/asdf-community/asdf-graalvm)

```bash
# install asdf plugin
asdf plugin add graalvm

# install graalvm v21.1.0
asdf install graalvm 21.1.0-java11

# setup graalvm version
asdf local graalvm 21.1.0-java11
```

> [Enabled **annotation processing**](https://docs.micronaut.io/latest/guide/index.html#ideSetup) at Setting → Build, Execution, Deployment → Compiler → Annotation Processors

> Set graalvm directry as **$HOME/.asdf/installs/graalvm/21.1.0-java11**