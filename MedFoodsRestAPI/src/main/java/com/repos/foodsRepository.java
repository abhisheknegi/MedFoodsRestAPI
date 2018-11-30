package com.repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.objects.foods;

@Repository
public interface foodsRepository extends CrudRepository<foods, Long> {
}