/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.arquillian.example.itest;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import org.arquillian.example.Greeter;
import org.arquillian.example.jpa.Game;
import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

@ArquillianSuiteDeployment
public abstract class ArquillianTestBase extends Arquillian {
    
    @PersistenceContext
    protected EntityManager em;

    @Inject
    protected UserTransaction utx;
    
    @Deployment
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackage(Greeter.class.getPackage())
                .addPackage(Game.class.getPackage())
                // add test classes in order to be able to run ITs from IDE without maven repackaging
                .addPackages(true, "org.arquillian.example.itest")
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }
    
    protected final boolean isInContainer() {
        return em != null;
    }
    
    /**
     * Know that BeforeMethod and AfterMethod are actually invoked twice per test.
     * See more here: http://planet.jboss.org/post/arquillian_and_testng
     * @throws Exception 
     */
    @BeforeMethod
    public final void beforeTestMethod() throws Exception {
        if (isInContainer()) {
            beforeTestMethodInContainer();
        }
        else {
            beforeTestMethodOutOfContainer();
        }
    }

    /**
     * Override this method to execute code before every test method
     * (outside of container, i.e. injections are not available here)
     */
    protected void beforeTestMethodOutOfContainer() {
    }

    /**
     * Override this method to execute code before every test method
     * (inside of container, i.e. injections can be used here)
     */
    protected void beforeTestMethodInContainer() throws Exception {
    }
    
    /**
     * Know that BeforeMethod and AfterMethod are actually invoked twice per test.
     * See more here: http://planet.jboss.org/post/arquillian_and_testng
     * @throws Exception 
     */
    @AfterMethod
    public final void afterTestMethod() throws Exception {
        if (isInContainer()) {
            afterTestMethodInContainer();
        }
        else {
            afterTestMethodOutOfContainer();
        }
    }

    /**
     * Override this method to execute code after every test method
     * (outside of container, i.e. injections are not available here)
     */
    protected void afterTestMethodOutOfContainer() {
    }

    /**
     * Override this method to execute code after every test method
     * (inside of container, i.e. injections can be used here)
     */
    protected void afterTestMethodInContainer() throws Exception {
    }
    
}

