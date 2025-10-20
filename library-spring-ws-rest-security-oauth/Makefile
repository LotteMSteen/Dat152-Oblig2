.PHONY: help run test package verify clean

MVN := mvn
ifneq ($(wildcard .mvn),)
MVN := ./mvnw
endif

.DEFAULT_GOAL := help

help:
	@echo "Targets:"
	@echo "  run     - Start the Spring Boot application"
	@echo "  test    - Execute the project's tests"
	@echo "  package - Build the executable jar"
	@echo "  verify  - Run the full Maven verify lifecycle"
	@echo "  clean   - Remove build artifacts"

run:
	$(MVN) spring-boot:run

test:
	$(MVN) test

package:
	$(MVN) package

verify:
	$(MVN) verify

clean:
	$(MVN) clean
