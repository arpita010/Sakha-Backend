package com.sakha.repository;

import com.sakha.entity.Chat;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepo extends CrudRepository<Chat, Long> {}
