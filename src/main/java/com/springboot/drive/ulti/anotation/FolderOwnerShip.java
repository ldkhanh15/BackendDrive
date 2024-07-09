package com.springboot.drive.ulti.anotation;

import com.springboot.drive.ulti.constant.AccessEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FolderOwnerShip {
    AccessEnum action() ;
}

