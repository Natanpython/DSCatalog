package com.estudo.aula.repositories;

import com.estudo.aula.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryesRepository extends JpaRepository<Category, Long> {

}
