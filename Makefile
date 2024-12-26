build:
	mvn clean package -DskipTests
build-native:
	mvn clean package -Pnative -DskipTests

dep:
	mvn dependency:resolve
run:
	mvn spring-boot:run