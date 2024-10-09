FROM bellsoft/liberica-openjdk-debian:21 as builder
WORKDIR application
RUN --mount=type=cache,target=/var/lib/apt,sharing=locked \
    --mount=type=cache,target=/var/cache/apt,sharing=locked \
    apt-get update -qq && \
    apt-get install unzip --no-install-recommends -y -qq
RUN --mount=type=cache,target=/root/.m2,sharing=locked \
    --mount=type=bind,target=.,readwrite \
    ./mvnw -V clean package -DskipTests --no-transfer-progress && \
    cp target/*.jar /opt/application.jar
RUN --mount=type=bind,target=. \
    java -Djarmode=tools -jar /opt/application.jar extract --layers --launcher --destination /opt/extracted >/dev/null 2>&1 || \
    java -Djarmode=layertools -jar /opt/application.jar extract --destination /opt/extracted >/dev/null && \
    curl -sL -o /opt/entrypoint.sh https://github.com/making/dockerfile-utils/raw/refs/heads/main/entrypoint.sh && \
    curl -sL -o /opt/class_counter.sh https://github.com/making/dockerfile-utils/raw/refs/heads/main/class_counter.sh && \
    bash /opt/class_counter.sh /opt/extracted/application /opt/extracted/dependencies > /opt/class_count

FROM bellsoft/liberica-openjre-debian:21
ARG USERNAME=spring
ARG GROUPNAME=spring
ARG UID=1002
ARG GID=1000
WORKDIR application
RUN groupadd -g $GID $GROUPNAME && \
    useradd -m -s /bin/bash -u $UID -g $GID $USERNAME
RUN curl -sL -o memory-calculator.tgz https://java-buildpack.cloudfoundry.org/memory-calculator/trusty/x86_64/memory-calculator-3.13.0_RELEASE.tar.gz && \
    tar -xzf memory-calculator.tgz -C /usr/local/bin && \
    rm -f memory-calculator.tgz
RUN curl -sL -o jattach.tgz https://github.com/jattach/jattach/releases/download/v2.2/jattach-linux-$(uname -m | sed -e 's/86_//' -e 's/aarch/arm/').tgz; \
    tar -xzf jattach.tgz -C /usr/local/bin && \
    rm -f jattach.tgz
USER $USERNAME
COPY --from=builder /opt/extracted/dependencies/ ./
COPY --from=builder /opt/extracted/spring-boot-loader/ ./
COPY --from=builder /opt/extracted/snapshot-dependencies/ ./
COPY --from=builder /opt/extracted/application/ ./
COPY --from=builder /opt/class_count /opt/
COPY --from=builder /opt/entrypoint.sh /opt/
ENTRYPOINT ["bash", "/opt/entrypoint.sh"]
