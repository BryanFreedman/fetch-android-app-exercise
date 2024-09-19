package com.example.fetch_exercise;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView;

import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdapter adapter;  // manage list of items
    private EditText searchEditText;  // search bar input
    private List<Item> allItems = new ArrayList<>();  // store all fetched items for filtering
    private Spinner listIdSpinner;  // spinner for filtering by listId

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // initialize search bar
        searchEditText = findViewById(R.id.searchEditText);

        // init spinner
        listIdSpinner = findViewById(R.id.listIdSpinner);

        // fetch from API
        fetchItems();

        // TextWatcher to search for items by name or ID when the user types
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // nothing needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // filter items by ID or Name as user types
                filterItemsByIdOrName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // nothing needed
            }
        });


        // set up listener for Spinner to filter by listID
        listIdSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //get selected listID (string)
                String selectedListId = (String) parent.getItemAtPosition(position);

                // if "All List IDs" is selected, show all items
                if (selectedListId.equals("All List IDs")) {
                    updateRecyclerView(allItems);  // Show all items
                } else {
                    // convert the selectedListId back to int and filter
                    filterItemsByListId(Integer.parseInt(selectedListId));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // nothing needed
            }
        });

    }


    // fetch items from API
    private void fetchItems() {
        // set up Retrofit for network
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://fetch-hiring.s3.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // create API service
        ApiService apiService = retrofit.create(ApiService.class);


        // fetch items via API call
        apiService.getItems().enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // get list of items
                    allItems = response.body();

                    // filter out items with blank or null names
                    allItems.removeIf(item -> item.getName() == null || item.getName().trim().isEmpty());

                    // sort the items first by listId, then by name alphabetically and by numeric value
                    Collections.sort(allItems, new Comparator<Item>() {
                        @Override
                        public int compare(Item o1, Item o2) {
                            // compare by listId
                            int listIdCompare = Integer.compare(o1.getListId(), o2.getListId());
                            if (listIdCompare != 0) {
                                return listIdCompare;
                            }

                            // extract the alphabetic part of the name
                            String alphaPart1 = extractAlphabeticPart(o1.getName());
                            String alphaPart2 = extractAlphabeticPart(o2.getName());

                            // compare the alphabetic parts first
                            int alphaCompare = alphaPart1.compareToIgnoreCase(alphaPart2); // case-insensitive comparison
                            if (alphaCompare != 0) {
                                return alphaCompare;  // return if names are different
                            }

                            // if names are the same, extract and compare the numeric parts
                            int numPart1 = extractFirstNumericPart(o1.getName());
                            int numPart2 = extractFirstNumericPart(o2.getName());

                            return Integer.compare(numPart1, numPart2);  // compare nums
                        }
                    });

                    // set up adapter with the filtered and sorted items
                    adapter = new ItemAdapter(allItems);
                    recyclerView.setAdapter(adapter);

                    // populate spinner with unique listId's
                    populateSpinnerWithListIds();
                } else {
                    // display error if API call fails
                    Toast.makeText(MainActivity.this, "Failed to load items", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                // Handle failure
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // extract the alphabetic part of the name (first sequence of letters)
    private String extractAlphabeticPart(String name) {
        StringBuilder alphabeticPart = new StringBuilder();
        for (char c : name.toCharArray()) {
            if (Character.isLetter(c)) {
                alphabeticPart.append(c);
            } else if (alphabeticPart.length() > 0) {
                break;  // stop if letters end
            }
        }
        return alphabeticPart.toString().trim();  // return alphabetic part of string
    }

    // extract the numeric part of the name
    private int extractFirstNumericPart(String name) {
        StringBuilder numericPart = new StringBuilder();
        for (char c : name.toCharArray()) {
            if (Character.isDigit(c)) {
                numericPart.append(c);
            } else if (numericPart.length() > 0) {
                break;  // stop collecting once we find a non-digit
            }
        }
        // return the numeric part if found, otherwise return -1 (no number)
        return numericPart.length() > 0 ? Integer.parseInt(numericPart.toString()) : -1;
    }


    // populate spinner with unique listId's
    private void populateSpinnerWithListIds() {
        // use set to collect unique instances
        Set<Integer> uniqueListIds = new HashSet<>();
        for (Item item : allItems) {
            uniqueListIds.add(item.getListId());
        }

        // convert set to list and sort
        List<String> listIds = new ArrayList<>();
        listIds.add("All List IDs");  // add "All List IDs" as first option
        for (Integer id : uniqueListIds) {
            listIds.add(id.toString());  // Convert list IDs to Strings
        }

        // set up the spinner adapter with string items
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listIds);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listIdSpinner.setAdapter(spinnerAdapter);
    }


    // filter items by ID, update the RecyclerView
    private void filterItemsById(String query) {
        List<Item> filteredList = new ArrayList<>();
        if (query.isEmpty()) {
            filteredList.addAll(allItems);  // Show all items if search is empty
        } else {
            for (Item item : allItems) {
                // Convert ID to string and check if it contains the search query
                if (String.valueOf(item.getId()).contains(query)) {
                    filteredList.add(item);
                }
            }
        }
        updateRecyclerView(filteredList);  // Update the RecyclerView with filtered items
    }


    // filter items by ID or Name, update RecyclerView
    private void filterItemsByIdOrName(String query) {
        List<Item> filteredList = new ArrayList<>();

        if (query.isEmpty()) {
            filteredList.addAll(allItems);  // Show all items if search query is empty
        } else {
            for (Item item : allItems) {
                // Check if the query is numeric (indicating an ID search)
                boolean isNumeric = query.matches("\\d+");
                if (isNumeric) {
                    // If it's numeric, search by ID
                    if (String.valueOf(item.getId()).contains(query)) {
                        filteredList.add(item);
                    }
                } else {
                    // Otherwise, search by name (case-insensitive)
                    if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                        filteredList.add(item);
                    }
                }
            }
        }

        updateRecyclerView(filteredList);  // Update the RecyclerView with filtered items
    }


    // Function to filter items by List ID and update the RecyclerView
    private void filterItemsByListId(Integer listId) {
        List<Item> filteredList = new ArrayList<>();
        for (Item item : allItems) {
            if (item.getListId().equals(listId)) {
                filteredList.add(item);
            }
        }
        updateRecyclerView(filteredList);  // Update the RecyclerView with filtered items
    }

    // update the RecyclerView with filtered list
    private void updateRecyclerView(List<Item> filteredItems) {
        adapter = new ItemAdapter(filteredItems);
        recyclerView.setAdapter(adapter);
    }
}
