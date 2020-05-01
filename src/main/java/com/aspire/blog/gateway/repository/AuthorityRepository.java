package com.aspire.blog.gateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aspire.blog.gateway.domain.Authority;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
