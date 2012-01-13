/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.spockframework.builder;

import java.lang.reflect.*;

import org.spockframework.gentyref.GenericTypeReflector;
import org.spockframework.util.MopUtil;
import org.spockframework.util.UnreachableCodeError;

import groovy.lang.MetaProperty;

public class PropertySlot implements ISlot {
  private final String name;
  private final Object owner;
  private final Type ownerType;
  private final MetaProperty property;

  PropertySlot(String name, Object owner, Type ownerType, MetaProperty property) {
    this.name = name;
    this.owner = owner;
    this.ownerType = ownerType;
    this.property = property;
  }

  public String getName() {
    return name;
  }

  public Type getType() {
    // could possibly add fast path here, but be careful (inner classes etc.)

    Method setter = MopUtil.setterFor(property);
    if (setter != null) return GenericTypeReflector.getExactParameterTypes(setter, ownerType)[0];

    Field field = MopUtil.fieldFor(property);
    if (field != null) return GenericTypeReflector.getExactFieldType(field, ownerType);

    throw new UnreachableCodeError();
  }

  public boolean isReadable() {
    return MopUtil.isReadable(property);
  }

  public boolean isWriteable() {
    return MopUtil.isWriteable(property);
  }

  public Object read() {
    return property.getProperty(owner);
  }

  public void write(Object value) {
    property.setProperty(owner, value);
  }

  public void configure(Object value) {
    write(value);
  }
}
