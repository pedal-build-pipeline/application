package com.pedalbuildpipeline.pbp.persistence;

import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.sql.Blob;

@Service
@RequiredArgsConstructor
public class LobHelper {
  private final SessionFactory sessionFactory;

  public Blob createBlob(InputStream content, long size) {
    return sessionFactory.getCurrentSession().getLobHelper().createBlob(content, size);
  }
}
