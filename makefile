
REST_DIR := library-spring-ws-rest
SEC_DIR := library-spring-ws-rest-security-oauth

.PHONY: help run-rest run-security test-rest test-security package-rest package-security verify-rest verify-security clean-rest clean-security

.DEFAULT_GOAL := help

help:
	@echo "Targets:"
	@echo "  run-rest        - Start the REST service"
	@echo "  run-security    - Start the OAuth-secured REST service"

	@echo "  test-rest       - Run tests for the REST service"
	@echo "  test-security   - Run tests for the OAuth-secured service"
	
	@echo "  package-rest    - Package the REST service"
	@echo "  package-security- Package the OAuth-secured service"
	
	@echo "  verify-rest     - Run Maven verify for the REST service"
	@echo "  verify-security - Run Maven verify for the OAuth-secured service"
	
	@echo "  clean-rest      - Clean build artifacts for the REST service"
	@echo "  clean-security  - Clean build artifacts for the OAuth-secured service"

# run-keycloak:
# 	cd ../../
#     docker run --name keycloak_unoptimized -p 8080:8080 \
#               -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin \
#               -v ./keycloak/imports:/opt/keycloak/data/import \
#               quay.io/keycloak/keycloak:latest \
#               start-dev --import-realm

tokens:
	curl -X POST http://localhost:8080/realms/DAT152/protocol/openid-connect/token --data 'grant_type=password&client_id=dat152oblig2&username=user1&password=user1'  
	curl -X POST http://localhost:8080/realms/DAT152/protocol/openid-connect/token --data 'grant_type=password&client_id=dat152oblig2&username=user2&password=user2' 
	curl -X POST http://localhost:8080/realms/DAT152/protocol/openid-connect/token --data 'grant_type=password&client_id=dat152oblig2&username=user3&password=user3' 

run-rest:
	$(MAKE) -C $(REST_DIR) run

run-security:
	$(MAKE) -C $(SEC_DIR) run

test-rest:
	$(MAKE) -C $(REST_DIR) test

test-security:
	$(MAKE) -C $(SEC_DIR) test

package-rest:
	$(MAKE) -C $(REST_DIR) package

package-security:
	$(MAKE) -C $(SEC_DIR) package

verify-rest:
	$(MAKE) -C $(REST_DIR) verify

verify-security:
	$(MAKE) -C $(SEC_DIR) verify

clean-rest:
	$(MAKE) -C $(REST_DIR) clean

clean-security:
	$(MAKE) -C $(SEC_DIR) clean
