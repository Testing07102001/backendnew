package org.kreyzon.stripe.repository;

import org.kreyzon.stripe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByEmail(String email);
    public Optional<User>  findByEmail(String email);
    Optional<User> findByUserId(String userId);
    Optional<User> findBySfidAndUsertype(long sfid, int usertype);

    Optional<User> findBySfid(Long sfid);
    List<User> findByUsertype(int usertype);

    Optional<User> findById(Long id);

}
