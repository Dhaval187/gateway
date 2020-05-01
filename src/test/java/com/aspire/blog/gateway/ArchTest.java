package com.aspire.blog.gateway;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;

class ArchTest {

	@Test
	void servicesAndRepositoriesShouldNotDependOnWebLayer() {

		JavaClasses importedClasses = new ClassFileImporter()
				.withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
				.importPackages("com.aspire.blog.gateway");

		noClasses().that().resideInAnyPackage("..service..").or().resideInAnyPackage("..repository..").should()
				.dependOnClassesThat().resideInAnyPackage("..com.aspire.blog.gateway.web..")
				.because("Services and repositories should not depend on web layer").check(importedClasses);
	}
}
