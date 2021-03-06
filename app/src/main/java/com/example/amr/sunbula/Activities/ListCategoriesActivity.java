package com.example.amr.sunbula.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.Toast;

import com.example.amr.sunbula.Adapters.List_CategoriesAdapter;
import com.example.amr.sunbula.Models.APIResponses.AllCategoriesResponse;
import com.example.amr.sunbula.Models.DBFlowModels.Categories;
import com.example.amr.sunbula.R;
import com.example.amr.sunbula.RetrofitAPIs.APIService;
import com.example.amr.sunbula.RetrofitAPIs.ApiUtils;
import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListCategoriesActivity extends AppCompatActivity {
    String UserID;
    ListView listView_allCategoriesBeen;
    APIService mAPIService;
    List_CategoriesAdapter adapter;
    List<AllCategoriesResponse.AllCategoriesBean> allCategoriesBeen;
    Categories categories;
    List<Categories> categoriesList;
    Gson gson;
    private ProgressDialog pdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_categories);

        FirebaseCrash.log("Here comes the exception!");
        FirebaseCrash.report(new Exception("oops!"));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_list_categories);
        toolbar.setTitle("List Categories");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        allCategoriesBeen = new ArrayList<AllCategoriesResponse.AllCategoriesBean>();

        SharedPreferences sharedPreferences = ListCategoriesActivity.this.getSharedPreferences("sharedPreferences_name", Context.MODE_PRIVATE);
        UserID = sharedPreferences.getString("UserID", "null");

        pdialog = new ProgressDialog(ListCategoriesActivity.this);
        pdialog.setIndeterminate(true);
        pdialog.setCancelable(false);
        pdialog.setMessage("Loading. Please wait...");

        mAPIService = ApiUtils.getAPIService();

        listView_allCategoriesBeen = (ListView) findViewById(R.id.list_categories);

        GetAllCategories(UserID);
    }

    public void GetAllCategories(String UserId) {
        pdialog.show();
        mAPIService.GetAllCategories(UserId).enqueue(new Callback<AllCategoriesResponse>() {

            @Override
            public void onResponse(Call<AllCategoriesResponse> call, Response<AllCategoriesResponse> response) {

                if (response.isSuccessful()) {
                    if (response.body().isIsSuccess()) {
                        AllCategoriesResponse allCategoriesResponse = response.body();

                        if (allCategoriesResponse.getAllCategories().size() > 0) {
                            allCategoriesBeen = allCategoriesResponse.getAllCategories();

                            categoriesList = (new Select().from(Categories.class).queryList());

                            if (categoriesList.size() > 0) {
                                Delete.table(Categories.class);
                            }

                            for (int i = 0; i < allCategoriesBeen.size(); i++) {
                                categories = new Categories();
                                if (allCategoriesBeen.size() > 0) {

                                    categories.setCategoryID(allCategoriesBeen.get(i).getCategoryID());
                                    categories.setCategoryName(allCategoriesBeen.get(i).getCategoryName());
                                    categories.setCategoryDescription(allCategoriesBeen.get(i).getCategoryDescription());
                                    categories.setDateCreated(allCategoriesBeen.get(i).getDateCreated());

                                    gson = new Gson();
                                    String jsonCauses = gson.toJson(allCategoriesBeen.get(i).getAllCases());
                                    categories.setAllCauses(jsonCauses);

                                    categories.save();
                                }
                            }

                            categoriesList = (new Select().from(Categories.class).queryList());
                            adapter = new List_CategoriesAdapter(ListCategoriesActivity.this, R.layout.item_in_list_categories, categoriesList);
                            listView_allCategoriesBeen.setAdapter(adapter);
                        }
                    } else
                        Toast.makeText(ListCategoriesActivity.this, response.body().getErrorMessage(), Toast.LENGTH_SHORT).show();
                    pdialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<AllCategoriesResponse> call, Throwable t) {

                categoriesList = (new Select().from(Categories.class).queryList());

                if (categoriesList.size() > 0) {
                    adapter = new List_CategoriesAdapter(ListCategoriesActivity.this, R.layout.item_in_list_categories, categoriesList);
                    listView_allCategoriesBeen.setAdapter(adapter);
                } else {
                    Toast.makeText(ListCategoriesActivity.this, "No Data", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(ListCategoriesActivity.this, R.string.string_internet_connection_warning, Toast.LENGTH_SHORT).show();
                pdialog.dismiss();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
