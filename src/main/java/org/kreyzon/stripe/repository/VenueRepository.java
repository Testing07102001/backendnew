package org.kreyzon.stripe.repository;

import org.kreyzon.stripe.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Long> {
}
