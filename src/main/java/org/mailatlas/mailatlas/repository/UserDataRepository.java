package org.mailatlas.mailatlas.repository;

import jakarta.validation.constraints.Email;
import org.mailatlas.mailatlas.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDataRepository extends JpaRepository<UserData, @Email String> {
    public UserData findByEmail(@Email String email);
}
