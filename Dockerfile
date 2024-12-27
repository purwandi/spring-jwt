FROM maven:3.9 as maven

# FROM ghcr.io/graalvm/jdk-community:17
# FROM ghcr.io/graalvm/native-image-community:17-muslib
FROM ghcr.io/graalvm/native-image-community:17 as build

ENV MAVEN_HOME=/opt/maven
ENV MAVEN_CONFIG=/root/.m2
ENV PATH=$MAVEN_HOME/bin:$PATH

WORKDIR /workspace
COPY --from=maven /usr/share/maven /opt/maven
COPY . .
RUN mvn dependency:resolve
RUN mvn -Pnative native:compile

FROM rockylinux:9-minimal
WORKDIR /app
COPY --from=build /workspace/target/gateway .
