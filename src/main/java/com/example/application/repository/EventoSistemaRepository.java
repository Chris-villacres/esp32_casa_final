package com.example.application.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventoSistemaRepository extends MongoRepository<com.example.application.model.EventoSistema, String> {}