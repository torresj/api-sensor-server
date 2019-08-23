package com.torresj.apisensorserver.exceptions;

import lombok.Getter;

@Getter
public class EntityAlreadyExists extends Exception {

  private static final long serialVersionUID = -7306413635068340585L;
  private Object object;

  public EntityAlreadyExists() {
  }

  public EntityAlreadyExists(Object object) {
    this.object = object;
  }

}