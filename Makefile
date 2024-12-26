dep:
	mvn dependency:resolve

clean:
	mvn clean

build: clean
	mvn package -DskipTests
build-native: clean
	mvn package -Pnative -DskipTests

run: clean
	mvn spring-boot:run
