package com.example.fetch_exercise;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortingTest {

    private List<Item> itemList;

    @Before
    public void setUp() {
        itemList = new ArrayList<>();

        itemList.add(new Item(1, 1, "123 Shampoo"));
        itemList.add(new Item(2, 2, "Apple"));
        itemList.add(new Item(3, 3, "Banana 42"));
        itemList.add(new Item(4, 3, "Banana 10"));
        itemList.add(new Item(5, 2, "Carrot"));
        itemList.add(new Item(6, 1, "123 Soap"));
        itemList.add(new Item(7, 1, null));  // Null name
        itemList.add(new Item(8, 2, ""));    // Blank name
    }

    @Test
    public void testFilteringAndSorting() {
        // filter out items with null or blank names
        itemList.removeIf(item -> item.getName() == null || item.getName().trim().isEmpty());

        // sort the items by listId, then by name (alpha and num)
        Collections.sort(itemList, new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                // listId compare
                int listIdCompare = Integer.compare(o1.getListId(), o2.getListId());
                if (listIdCompare != 0) {
                    return listIdCompare;
                }

                // extract alpha name
                String alphaPart1 = extractAlphabeticPart(o1.getName());
                String alphaPart2 = extractAlphabeticPart(o2.getName());

                // compare alpha parts
                int alphaCompare = alphaPart1.compareToIgnoreCase(alphaPart2);
                if (alphaCompare != 0) {
                    return alphaCompare;
                }

                // compare numeric parts of name secondarily
                int numPart1 = extractFirstNumericPart(o1.getName());
                int numPart2 = extractFirstNumericPart(o2.getName());

                return Integer.compare(numPart1, numPart2);
            }
        });

        // correct list size and list order
        assertEquals(6, itemList.size());
        assertEquals("123 Shampoo", itemList.get(0).getName());
        assertEquals("123 Soap", itemList.get(1).getName());
        assertEquals("Apple", itemList.get(2).getName());
        assertEquals("Carrot", itemList.get(3).getName());
        assertEquals("Banana 10", itemList.get(4).getName());
        assertEquals("Banana 42", itemList.get(5).getName());

        //PRINT
        System.out.println();
        System.out.println("Final list:");
        for (Item item : itemList) {
            System.out.println("Name: " + item.getName() + ", ID: " +item.getId() + item.getListId() + ", List ID: " + item.getListId());
        }
    }

    // extract the alphabetic part of the name
    private String extractAlphabeticPart(String name) {
        StringBuilder alphabeticPart = new StringBuilder();
        for (char c : name.toCharArray()) {
            if (Character.isLetter(c)) {
                alphabeticPart.append(c);
            } else if (alphabeticPart.length() > 0) {
                break;
            }
        }
        return alphabeticPart.toString().trim();
    }

    // extract the first numeric part of the name
    private int extractFirstNumericPart(String name) {
        StringBuilder numericPart = new StringBuilder();
        for (char c : name.toCharArray()) {
            if (Character.isDigit(c)) {
                numericPart.append(c);
            } else if (numericPart.length() > 0) {
                break;
            }
        }
        return numericPart.length() > 0 ? Integer.parseInt(numericPart.toString()) : -1;
    }
}
