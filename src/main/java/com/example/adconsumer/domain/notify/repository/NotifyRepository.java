package com.example.adconsumer.domain.notify.repository;

import com.example.adconsumer.domain.notify.entity.Notify;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotifyRepository extends JpaRepository<Notify, Long> {

}
