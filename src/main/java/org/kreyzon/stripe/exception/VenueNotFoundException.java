package org.kreyzon.stripe.exception;

public class VenueNotFoundException extends RuntimeException {
    public VenueNotFoundException(Long id) {super("Cloud not found the venue with id"+id);}
}