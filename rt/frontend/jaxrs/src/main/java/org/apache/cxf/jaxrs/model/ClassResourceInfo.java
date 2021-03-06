/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.cxf.jaxrs.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.jaxrs.lifecycle.ResourceProvider;
import org.apache.cxf.jaxrs.utils.AnnotationUtils;
import org.apache.cxf.jaxrs.utils.InjectionUtils;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.jaxrs.utils.ResourceUtils;

public class ClassResourceInfo extends AbstractResourceInfo {
    
    private URITemplate uriTemplate;
    private MethodDispatcher methodDispatcher;
    private ResourceProvider resourceProvider;
    private ConcurrentHashMap<SubresourceKey, ClassResourceInfo> subResources 
        = new ConcurrentHashMap<SubresourceKey, ClassResourceInfo>();
   
    private List<Field> paramFields;
    private List<Method> paramMethods;
    private boolean enableStatic;
    private boolean createdFromModel; 
    private String consumesTypes;
    private String producesTypes;
    private ClassResourceInfo parent;
    
    public ClassResourceInfo(ClassResourceInfo cri) {
        super(cri.getBus());       
        if (cri.isCreatedFromModel() && !InjectionUtils.isConcreteClass(cri.getServiceClass())) {
            this.root = cri.root;
            this.serviceClass = cri.serviceClass;
            this.uriTemplate = cri.uriTemplate;    
            this.methodDispatcher = new MethodDispatcher(cri.methodDispatcher, this);
            this.subResources = cri.subResources;
            this.paramFields = cri.paramFields;
            this.paramMethods = cri.paramMethods;
            this.enableStatic = true;
            this.parent = cri.parent;
        } else {
            throw new IllegalArgumentException();
        }
        
    }
    
    public ClassResourceInfo(Class<?> theResourceClass, Class<?> theServiceClass, 
                             boolean theRoot, boolean enableStatic, Bus bus) {
        super(theResourceClass, theServiceClass, theRoot, bus);
        this.enableStatic = enableStatic;
        if (root && resourceClass != null) {
            setParamField(serviceClass);
            setParamMethods(serviceClass);
        }
    }
    
    public ClassResourceInfo(Class<?> theResourceClass, Class<?> theServiceClass, 
                             boolean theRoot, boolean enableStatic, boolean createdFromModel, Bus bus) {
        this(theResourceClass, theServiceClass, theRoot, enableStatic, bus);
        this.createdFromModel = createdFromModel;
    }
    //CHECKSTYLE:OFF
    public ClassResourceInfo(Class<?> theResourceClass, Class<?> c, 
                             boolean theRoot, boolean enableStatic, boolean createdFromModel,
                             String consumesTypes, String producesTypes, Bus bus) {
    //CHECKSTYLE:ON    
        this(theResourceClass, theResourceClass, theRoot, enableStatic, createdFromModel, bus);
        this.consumesTypes = consumesTypes;
        this.producesTypes = producesTypes;
    }
    
    // The following constructors are used by tests only
    public ClassResourceInfo(Class<?> theResourceClass) {
        this(theResourceClass, true);
    }
    
    public ClassResourceInfo(Class<?> theResourceClass, boolean theRoot) {
        this(theResourceClass, theResourceClass, theRoot);
    }
    
    public ClassResourceInfo(Class<?> theResourceClass, Class<?> theServiceClass) {
        this(theResourceClass, theServiceClass, false);
    }

    public ClassResourceInfo(Class<?> theResourceClass, Class<?> theServiceClass, boolean theRoot) {
        this(theResourceClass, theServiceClass, theRoot, false, BusFactory.getDefaultBus(true));
    }
    
    public ClassResourceInfo findResource(Class<?> typedClass, Class<?> instanceClass) {
        instanceClass = enableStatic ? typedClass : instanceClass;
        SubresourceKey key = new SubresourceKey(typedClass, instanceClass);
        return subResources.get(key);
    }
    
    public ClassResourceInfo getSubResource(Class<?> typedClass, Class<?> instanceClass) {
        
        instanceClass = enableStatic ? typedClass : instanceClass;
        
        SubresourceKey key = new SubresourceKey(typedClass, instanceClass);
        ClassResourceInfo cri = subResources.get(key);
        if (cri == null && !enableStatic) {
            cri = ResourceUtils.createClassResourceInfo(typedClass, instanceClass, false, enableStatic,
                                                        getBus());
            if (cri != null) {
                ClassResourceInfo tmpCri = subResources.putIfAbsent(key, cri);
                if (tmpCri != null) {
                    cri = tmpCri;
                }
            }
        }
        return cri;
    }
    
    public void addSubClassResourceInfo(ClassResourceInfo cri) {
        subResources.putIfAbsent(new SubresourceKey(cri.getResourceClass(), 
                                            cri.getServiceClass()),
                                 cri);
    }
    
    public Collection<ClassResourceInfo> getSubResources() {
        return Collections.unmodifiableCollection(subResources.values());
    }
    
    public Set<String> getAllowedMethods() {
        Set<String> methods = new HashSet<String>();
        for (OperationResourceInfo o : methodDispatcher.getOperationResourceInfos()) {
            String method = o.getHttpMethod();
            if (method != null) {
                methods.add(method);
            }
        }
        return methods;
    }
    
    private void setParamField(Class<?> cls) {
        if (Object.class == cls || cls == null) {
            return;
        }
        for (Field f : cls.getDeclaredFields()) {
            for (Annotation a : f.getAnnotations()) {
                if (AnnotationUtils.isParamAnnotationClass(a.annotationType())) {
                    if (paramFields == null) {
                        paramFields = new ArrayList<Field>();
                    }
                    paramFields.add(f);
                }
            }
        }
        setParamField(cls.getSuperclass());
    }
    
    private void setParamMethods(Class<?> cls) {
        
        for (Method m : cls.getMethods()) {
        
            if (!m.getName().startsWith("set") || m.getParameterTypes().length != 1) {
                continue;
            }
            for (Annotation a : m.getAnnotations()) {
                if (AnnotationUtils.isParamAnnotationClass(a.annotationType())) {
                    checkParamMethod(m, AnnotationUtils.getAnnotationValue(a));
                    break;
                }
            }
        }
        Class<?>[] interfaces = cls.getInterfaces();
        for (Class<?> i : interfaces) {
            setParamMethods(i);
        }
    }

    public URITemplate getURITemplate() {
        return uriTemplate;
    }

    public void setURITemplate(URITemplate u) {
        uriTemplate = u;
    }

    public MethodDispatcher getMethodDispatcher() {
        return methodDispatcher;
    }

    public void setMethodDispatcher(MethodDispatcher md) {
        methodDispatcher = md;
    }

    public boolean hasSubResources() {
        return !subResources.isEmpty();
    }
    
    
    public boolean isCreatedFromModel() {
        return createdFromModel;
    }
    
    public ResourceProvider getResourceProvider() {
        return resourceProvider;
    }

    public void setResourceProvider(ResourceProvider rp) {
        resourceProvider = rp;
    }
    
    public List<MediaType> getProduceMime() {
        if (producesTypes != null) {
            return JAXRSUtils.parseMediaTypes(producesTypes);
        }
        Produces produces = AnnotationUtils.getClassAnnotation(getServiceClass(), Produces.class);
        if (produces != null || parent == null) {
            return JAXRSUtils.getProduceTypes(produces);
        } else {
            return parent.getProduceMime();
        }
    }
    
    public List<MediaType> getConsumeMime() {
        if (consumesTypes != null) {
            return JAXRSUtils.parseMediaTypes(consumesTypes);
        }
        Consumes consumes = AnnotationUtils.getClassAnnotation(getServiceClass(), Consumes.class);
        if (consumes != null || parent == null) {
            return JAXRSUtils.getConsumeTypes(consumes);
        } else {
            return parent.getConsumeMime();
        }
    }
    
    public Path getPath() {
        return AnnotationUtils.getClassAnnotation(getServiceClass(), Path.class);
    }
    
    private void addParamMethod(Method m) {
        if (paramMethods == null) {
            paramMethods = new ArrayList<Method>();
        }
        paramMethods.add(m);
    }
    
    @SuppressWarnings("unchecked")
    public List<Method> getParameterMethods() {
        return paramMethods == null ? Collections.EMPTY_LIST 
                                    : Collections.unmodifiableList(paramMethods);
    }
    
    @SuppressWarnings("unchecked")
    public List<Field> getParameterFields() {
        return paramFields == null ? Collections.EMPTY_LIST 
                                    : Collections.unmodifiableList(paramFields);
    }
    
    private void checkParamMethod(Method m, String value) {
        if (m.getName().equalsIgnoreCase("set" + value)) {
            addParamMethod(m);
        }
    }
    
    @Override
    public boolean isSingleton() {
        return resourceProvider != null && resourceProvider.isSingleton();
    }

    public void setParent(ClassResourceInfo parent) {
        this.parent = parent;
    }
    
    public ClassResourceInfo getParent() {
        return parent;
    }
}
