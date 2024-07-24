package com.magnasha.powerjolt.repository;

import com.magnasha.powerjolt.document.ContactForm;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactFormRepository extends ReactiveCrudRepository<ContactForm, Long> {
}
