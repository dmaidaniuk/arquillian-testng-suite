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
package org.arquillian.example.itest.jpa;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import org.arquillian.example.itest.ArquillianTestBase;
import org.arquillian.example.jpa.LineItem;
import org.arquillian.example.jpa.Record;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class RecordPersistenceTest extends ArquillianTestBase {
    
    private Random random = new Random(13527);
    
    private static final RecordData[] RECORDS = {
        new RecordData("Record A", 5),
        new RecordData("Record B", 3),
        new RecordData("Record C", 6)
    };
    
    protected static class RecordData {
        String name;
        Integer itemsCount;

        public RecordData(String name, int itemsCount) {
            this.name = name;
            this.itemsCount = itemsCount;
        }
        
    }
    
    @Override
    protected void beforeTestMethodInContainer() throws Exception {
        clearData();
        insertData();
        startTransaction();
    }

    @Override
    protected void afterTestMethodInContainer() throws Exception {
        utx.commit();
    }
    
    @Test
    public void shouldFindAllGamesUsingJpqlQuery() throws Exception {
        String fetchingAllRecordsInJpql = "select r from Record r order by r.id";

        System.out.println("Selecting (using JPQL)...");
        List<Record> records = em.createQuery(fetchingAllRecordsInJpql, Record.class).getResultList();

        System.out.println("Found " + records.size() + " records (using JPQL):");
        assertContainsAllRecords(records);
    }
    
    private void clearData() throws Exception {
        utx.begin();
        em.joinTransaction();
        System.out.println("Dumping old records...");
        em.createQuery("delete from Record").executeUpdate();
        utx.commit();
    }

    private void insertData() throws Exception {
        utx.begin();
        em.joinTransaction();
        System.out.println("Inserting records...");
        for (RecordData recordData : RECORDS) {
            Record record = new Record(recordData.name);
            for (int i = 0; i < recordData.itemsCount; i++) {
                LineItem item = new LineItem(new BigDecimal(random.nextInt(50) + 1));
                record.addLineItem(item);
            }
            em.persist(record);
        }
        utx.commit();
        em.clear();
    }

    private void startTransaction() throws Exception {
        utx.begin();
        em.joinTransaction();
    }
    
    private static void assertContainsAllRecords(Collection<Record> retrievedRecords) {
        assertEquals(RECORDS.length, retrievedRecords.size());
        for (Record record : retrievedRecords) {
            System.out.println("* " + record);
            boolean found = false;
            for (RecordData data : RECORDS) {
                if (data.name.equals(record.getName())) {
                    found = true;
                    int itemsSize = record.getLineItems().size();
                    assertEquals(data.itemsCount.intValue(), itemsSize);
                }
            }
            assertTrue(found);
        }
    }
    
}
