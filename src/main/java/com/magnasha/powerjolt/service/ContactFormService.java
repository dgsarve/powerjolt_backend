package com.magnasha.powerjolt.service;



import com.magnasha.powerjolt.document.ContactForm;
import com.magnasha.powerjolt.repository.ContactFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ContactFormService {

    @Autowired
    private ContactFormRepository contactFormRepository;

    public Mono<ContactForm> saveContactForm(ContactForm contactForm) {
        return contactFormRepository.save(contactForm);
    }
}
